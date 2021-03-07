package blue.starry.tweetchime

import org.jetbrains.exposed.sql.Table

object TweetUserScreenNameHistories: Table() {
    val user = varchar("user", 20)
    val id = long("id")
}

object TweetUserIdHistories: Table() {
    val user = long("user")
    val id = long("id")
}

object TweetListIdHistories: Table() {
    val list = long("list")
    val id = long("id")
}
