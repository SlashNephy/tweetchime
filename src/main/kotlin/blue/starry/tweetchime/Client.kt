package blue.starry.tweetchime

import blue.starry.penicillin.PenicillinClient
import blue.starry.penicillin.core.session.config.account
import blue.starry.penicillin.core.session.config.application
import blue.starry.penicillin.core.session.config.httpClient
import blue.starry.penicillin.core.session.config.token
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.http.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Connection

val AppHttpClient by lazy {
    HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }

        defaultRequest {
            userAgent("tweetchime (+https://github.com/SlashNephy/tweetchime)")
        }
    }
}

val AppTwitterClient by lazy {
    PenicillinClient {
        account {
            application(Env.TWITTER_CK, Env.TWITTER_CS)
            token(Env.TWITTER_AT, Env.TWITTER_ATS)
        }
        httpClient(AppHttpClient)
    }
}

val AppDatabase by lazy {
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

    Files.createDirectories(Paths.get("data"))

    Database.connect("jdbc:sqlite:data/database.db", "org.sqlite.JDBC")
}
