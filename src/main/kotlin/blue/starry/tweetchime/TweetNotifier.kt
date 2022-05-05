package blue.starry.tweetchime

import blue.starry.penicillin.extensions.createdAt
import blue.starry.penicillin.extensions.instant
import blue.starry.penicillin.extensions.models.text
import blue.starry.penicillin.models.Status
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.ZoneOffset

object TweetNotifier {
    init {
        transaction(AppDatabase) {
            SchemaUtils.create(TweetUserScreenNameHistories)
            SchemaUtils.create(TweetUserIdHistories)
            SchemaUtils.create(TweetListIdHistories)
        }
    }

    private val logger = KotlinLogging.create("app.notifier")

    suspend fun check() = coroutineScope {
        listOf(
            Env.TARGET_SCREEN_NAMES.map {
                launch {
                    checkByUserScreenName(it)
                }
            },
            Env.TARGET_USER_IDS.map {
                launch {
                    checkByUserId(it)
                }
            },
            Env.TARGET_LIST_IDS.map {
                launch {
                    checkByListId(it)
                }
            }
        ).flatten().joinAll()
    }

    private suspend fun checkByUserScreenName(screenName: String) {
        val lastId = transaction(AppDatabase) {
            TweetUserScreenNameHistories.select { TweetUserScreenNameHistories.user eq screenName }.firstOrNull()?.get(TweetUserScreenNameHistories.id)
        }
        var newId = 0L

        TweetFetcher.byUserScreenName(screenName, lastId, Env.LIMIT_NOTIFICATIONS)
            .collectIndexed { i, tweet ->
                logger.trace { tweet }

                // only notify when lastId is present and tweet is not ignored
                if (lastId != null && Env.IGNORE_TEXTS.none { it in tweet.text } && (tweet.retweetedStatus == null || !Env.IGNORE_RTS)) {
                    notify(tweet)
                }

                // save first id
                if (i == 0) {
                    newId = tweet.id
                }
            }

        // skip updating if new id is zero
        if (newId == 0L) {
            return
        }

        transaction(AppDatabase) {
            // update if exists
            if (lastId != null) {
                TweetUserScreenNameHistories.update({ TweetUserScreenNameHistories.user eq screenName }) {
                    it[id] = newId
                }
            // insert if not exists
            } else {
                TweetUserScreenNameHistories.insert {
                    it[user] = screenName
                    it[id] = newId
                }
            }
        }
    }

    private suspend fun checkByUserId(userId: Long) {
        val lastId = transaction(AppDatabase) {
            TweetUserIdHistories.select { TweetUserIdHistories.user eq userId }.firstOrNull()?.get(TweetUserIdHistories.id)
        }
        var newId = 0L

        TweetFetcher.byUserId(userId, lastId, Env.LIMIT_NOTIFICATIONS)
            .collectIndexed { i, tweet ->
                logger.trace { tweet }

                // only notify when lastId is present and tweet is not ignored
                if (lastId != null && Env.IGNORE_TEXTS.none { it in tweet.text } && (tweet.retweetedStatus == null || !Env.IGNORE_RTS)) {
                    notify(tweet)
                }

                // save first id
                if (i == 0) {
                    newId = tweet.id
                }
            }

        // skip updating if new id is zero
        if (newId == 0L) {
            return
        }

        transaction(AppDatabase) {
            // update if exists
            if (lastId != null) {
                TweetUserIdHistories.update({ TweetUserIdHistories.user eq userId }) {
                    it[id] = newId
                }
            // insert if not exists
            } else {
                TweetUserIdHistories.insert {
                    it[user] = userId
                    it[id] = newId
                }
            }
        }
    }

    private suspend fun checkByListId(listId: Long) {
        val lastId = transaction(AppDatabase) {
            TweetListIdHistories.select { TweetListIdHistories.list eq listId }.firstOrNull()?.get(TweetListIdHistories.id)
        }
        var newId = 0L

        TweetFetcher.byListId(listId, lastId, Env.LIMIT_NOTIFICATIONS)
            .collectIndexed { i, tweet ->
                logger.trace { tweet }

                // only notify when lastId is present and tweet is not ignored
                if (lastId != null && Env.IGNORE_TEXTS.none { it in tweet.text } && (tweet.retweetedStatus == null || !Env.IGNORE_RTS)) {
                    notify(tweet)
                }

                // save first id
                if (i == 0) {
                    newId = tweet.id
                }
            }

        // skip updating if new id is zero
        if (newId == 0L) {
            return
        }

        transaction(AppDatabase) {
            // update if exists
            if (lastId != null) {
                TweetListIdHistories.update({ TweetListIdHistories.list eq listId }) {
                    it[id] = newId
                }
            // insert if not exists
            } else {
                TweetListIdHistories.insert {
                    it[list] = listId
                    it[id] = newId
                }
            }
        }
    }

    private suspend fun notify(tweet: Status) {
        notifyToDiscordWebhook(tweet, Env.DISCORD_WEBHOOK_URL)
    }

    private suspend fun notifyToDiscordWebhook(tweet: Status, webhookUrl: String) {
        AppHttpClient.post<Unit>(webhookUrl) {
            contentType(ContentType.Application.Json)

            body = DiscordWebhookMessage(
                embeds = listOf(
                    DiscordEmbed(
                        color = 1942002,
                        author = DiscordEmbed.Author(
                            name = "${tweet.user.name} (@${tweet.user.screenName})",
                            url = "https://twitter.com/${tweet.user.screenName}",
                            iconUrl = tweet.user.profileImageUrlHttps
                        ),
                        description = tweet.text,
                        fields = listOf(
                            DiscordEmbed.Field(
                                name = "Link",
                                value = "https://twitter.com/${tweet.user.screenName}/status/${tweet.id}"
                            )
                        ),
                        image = tweet.entities.media.firstOrNull()?.let {
                            DiscordEmbed.Image(
                                url = it.mediaUrlHttps,
                                height = it.sizes?.large?.h,
                                width = it.sizes?.large?.w
                            )
                        },
                        footer = DiscordEmbed.Footer(
                            text = "Twitter",
                            iconUrl = "https://abs.twimg.com/icons/apple-touch-icon-192x192.png"
                        ),
                        timestamp = tweet.createdAt.instant.atOffset(ZoneOffset.UTC)?.toZonedDateTime()
                    )
                )
            )
        }
    }
}
