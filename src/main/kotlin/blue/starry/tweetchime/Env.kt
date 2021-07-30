package blue.starry.tweetchime

import java.util.*
import kotlin.properties.ReadOnlyProperty

object Env {
    val TWITTER_CK by string
    val TWITTER_CS by string
    val TWITTER_AT by string
    val TWITTER_ATS by string
    val DISCORD_WEBHOOK_URL by string
    val INTERVAL_SEC by long { 3600 }
    val LIMIT_NOTIFICATIONS by int { 1 }
    val LOG_LEVEL by stringOrNull
    val TARGET_SCREEN_NAMES by stringList
    val TARGET_USER_IDS by longList
    val TARGET_LIST_IDS by longList
    val IGNORE_RTS by boolean
    val IGNORE_TEXTS by stringList
}

private val string: ReadOnlyProperty<Env, String>
    get() = ReadOnlyProperty { _, property ->
        System.getenv(property.name) ?: error("Env: ${property.name} is not present.")
    }

private val stringOrNull: ReadOnlyProperty<Env, String?>
    get() = ReadOnlyProperty { _, property ->
        System.getenv(property.name)
    }

private val stringList: ReadOnlyProperty<Env, List<String>>
    get() = ReadOnlyProperty { _, property ->
        System.getenv(property.name)?.split(",").orEmpty()
    }

private fun int(default: () -> Int): ReadOnlyProperty<Env, Int> {
    return ReadOnlyProperty { _, property ->
        System.getenv(property.name).toIntOrNull() ?: default()
    }
}

private fun long(default: () -> Long): ReadOnlyProperty<Env, Long> {
    return ReadOnlyProperty { _, property ->
        System.getenv(property.name).toLongOrNull() ?: default()
    }
}

private val longList: ReadOnlyProperty<Env, List<Long>>
    get() = ReadOnlyProperty { _, property ->
        System.getenv(property.name)?.split(",")?.mapNotNull { it.toLongOrNull() }.orEmpty()
    }

private fun String?.toBooleanFazzy(): Boolean {
    return when (this) {
        null -> false
        "1", "yes" -> true
        else -> lowercase(Locale.getDefault()).toBoolean()
    }
}

private val boolean: ReadOnlyProperty<Env, Boolean>
    get() = ReadOnlyProperty { _, property ->
        System.getenv(property.name).toBooleanFazzy()
    }
