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
        transaction(TweetchimeDatabase) {
            SchemaUtils.create(TweetUserScreenNameHistories)
            SchemaUtils.create(TweetUserIdHistories)
            SchemaUtils.create(TweetListIdHistories)
        }
    }

    private val logger = KotlinLogging.createFeedchimeLogger("tweetchime.notifier")

    suspend fun check(tweets: List<Config.Tweet>) = coroutineScope {
        tweets.map {
            launch {
                when {
                    it.userScreenName != null -> {
                        checkByUserScreenName(it.userScreenName, it)
                    }
                    it.userId != null -> {
                        checkByUserId(it.userId, it)
                    }
                    it.listId != null -> {
                        checkByListId(it.listId, it)
                    }
                    else -> {
                        logger.warn { "$it is not valid." }
                    }
                }
            }
        }.joinAll()
    }

    private suspend fun checkByUserScreenName(screenName: String, config: Config.Tweet) {
        val lastId = transaction(TweetchimeDatabase) {
            TweetUserScreenNameHistories.select { TweetUserScreenNameHistories.user eq screenName }.firstOrNull()?.get(TweetUserScreenNameHistories.id)
        }
        var newId = 0L

        TweetFetcher.byUserScreenName(screenName, lastId, TweetchimeConfig.limit)
            .collectIndexed { i, tweet ->
                logger.trace { tweet }

                // only notify when lastId is present and tweet is not ignored
                if (lastId != null && config.ignoreTexts.none { it in tweet.text } && (tweet.retweetedStatus == null || !config.ignoreRTs)) {
                    notify(tweet, config)
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

        transaction(TweetchimeDatabase) {
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

    private suspend fun checkByUserId(userId: Long, config: Config.Tweet) {
        val lastId = transaction(TweetchimeDatabase) {
            TweetUserIdHistories.select { TweetUserIdHistories.user eq userId }.firstOrNull()?.get(TweetUserIdHistories.id)
        }
        var newId = 0L

        TweetFetcher.byUserId(userId, lastId, TweetchimeConfig.limit)
            .collectIndexed { i, tweet ->
                logger.trace { tweet }

                // only notify when lastId is present and tweet is not ignored
                if (lastId != null && config.ignoreTexts.none { it in tweet.text } && (tweet.retweetedStatus == null || !config.ignoreRTs)) {
                    notify(tweet, config)
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

        transaction(TweetchimeDatabase) {
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

    private suspend fun checkByListId(listId: Long, config: Config.Tweet) {
        val lastId = transaction(TweetchimeDatabase) {
            TweetListIdHistories.select { TweetListIdHistories.list eq listId }.firstOrNull()?.get(TweetListIdHistories.id)
        }
        var newId = 0L

        TweetFetcher.byListId(listId, lastId, TweetchimeConfig.limit)
            .collectIndexed { i, tweet ->
                logger.trace { tweet }

                // only notify when lastId is present and tweet is not ignored
                if (lastId != null && config.ignoreTexts.none { it in tweet.text } && (tweet.retweetedStatus == null || !config.ignoreRTs)) {
                    notify(tweet, config)
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

        transaction(TweetchimeDatabase) {
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

    private suspend fun notify(tweet: Status, config: Config.Tweet) {
        if (config.discordWebhookUrl != null) {
            notifyToDiscordWebhook(tweet, config.discordWebhookUrl)
        }
    }

    private suspend fun notifyToDiscordWebhook(tweet: Status, webhookUrl: String) {
        TweetchimeHttpClient.post<Unit>(webhookUrl) {
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
