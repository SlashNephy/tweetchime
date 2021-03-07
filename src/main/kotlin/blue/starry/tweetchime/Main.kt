package blue.starry.tweetchime

import kotlinx.coroutines.delay
import mu.KotlinLogging
import kotlin.time.seconds

private val logger = KotlinLogging.createFeedchimeLogger("tweetchime")

suspend fun main() {
    logger.info { "Application started!" }

    val feeds = TweetchimeConfig.tweets
    require(feeds.isNotEmpty()) {
        "No tweets available. Exit..."
    }

    require(TweetchimeConfig.interval >= 10) {
        "Too short interval passed. Please set it to 10 or greater value."
    }

    require(TweetchimeConfig.limit > 0) {
        "Limit requires positive number (> 0)."
    }

    while (true) {
        TweetNotifier.check(feeds)

        logger.trace { "Sleep ${TweetchimeConfig.interval.seconds}." }
        delay(TweetchimeConfig.interval.seconds)
    }
}
