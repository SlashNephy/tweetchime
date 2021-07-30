package blue.starry.tweetchime

import kotlinx.coroutines.delay
import mu.KotlinLogging
import kotlin.time.Duration

private val logger = KotlinLogging.create("app")

suspend fun main() {
    logger.info { "Application started!" }

    val feeds = AppConfig.tweets
    require(feeds.isNotEmpty()) {
        "No tweets available. Exit..."
    }

    require(AppConfig.interval >= 10) {
        "Too short interval passed. Please set it to 10 or greater value."
    }

    require(AppConfig.limit > 0) {
        "Limit requires positive number (> 0)."
    }

    while (true) {
        TweetNotifier.check(feeds)

        logger.trace { "Sleep ${Duration.seconds(AppConfig.interval)}." }
        delay(Duration.seconds(AppConfig.interval))
    }
}
