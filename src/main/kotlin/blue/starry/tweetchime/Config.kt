package blue.starry.tweetchime

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import java.nio.file.Paths
import kotlin.io.path.readText

@Serializable
data class Config(
    val ck: String,
    val cs: String,
    val at: String,
    val ats: String,
    val interval: Int = 3600,
    val limit: Int = 1,
    val logLevel: String? = null,
    val tweets: List<Tweet> = emptyList()
) {
    @Serializable
    data class Tweet(
        val userScreenName: String? = null,
        val userId: Long? = null,
        val listId: Long? = null,
        val discordWebhookUrl: String? = null,
        val ignoreRTs: Boolean = false,
        val ignoreTexts: List<String> = emptyList()
    )

    companion object {
        private val path = Paths.get("config.yml")

        fun load(): Config {
            val content = path.readText()
            return Yaml.default.decodeFromString(content)
        }
    }
}
