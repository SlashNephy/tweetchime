package blue.starry.tweetchime

import blue.starry.penicillin.endpoints.timeline
import blue.starry.penicillin.endpoints.timeline.listTimeline
import blue.starry.penicillin.endpoints.timeline.userTimelineByScreenName
import blue.starry.penicillin.endpoints.timeline.userTimelineByUserId
import kotlinx.coroutines.flow.flow

object TweetFetcher {
    fun byUserScreenName(screenName: String, lastId: Long?, count: Int) = flow {
        AppTwitterClient.timeline.userTimelineByScreenName(screenName, sinceId = lastId, count = count).execute().forEach {
            emit(it)
        }
    }

    fun byUserId(id: Long, lastId: Long? = null, count: Int) = flow {
        AppTwitterClient.timeline.userTimelineByUserId(id, sinceId = lastId, count = count).execute().forEach {
            emit(it)
        }
    }

    fun byListId(id: Long, lastId: Long? = null, count: Int) = flow {
        AppTwitterClient.timeline.listTimeline(id, sinceId = lastId, count = count).execute().forEach {
            emit(it)
        }
    }
}
