package blue.starry.tweetchime

import ch.qos.logback.classic.Level
import mu.KLogger
import mu.KotlinLogging

fun KotlinLogging.createFeedchimeLogger(name: String): KLogger {
    val logger = logger(name)
    val underlying = logger.underlyingLogger
    if (underlying is ch.qos.logback.classic.Logger) {
        underlying.level = Level.toLevel(TweetchimeConfig.logLevel, Level.INFO)
    }

    return logger
}
