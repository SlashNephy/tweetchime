package blue.starry.tweetchime

import kotlinx.coroutines.delay
import mu.KotlinLogging
import kotlin.time.Duration

private val logger = KotlinLogging.create("app")

suspend fun main() {
    logger.info { "Application started!" }

    require(Env.LIMIT_NOTIFICATIONS > 0) {
        "LIMIT_NOTIFICATIONS requires positive number (> 0)."
    }

    while (true) {
        TweetNotifier.check()

        val sleep = Duration.seconds(Env.INTERVAL_SEC)
        logger.trace { "Sleep ${sleep}." }
        delay(sleep)
    }
}
