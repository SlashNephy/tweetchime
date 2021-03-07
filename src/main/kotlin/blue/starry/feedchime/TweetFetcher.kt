package blue.starry.feedchime

import blue.starry.penicillin.endpoints.timeline
import blue.starry.penicillin.endpoints.timeline.listTimeline
import blue.starry.penicillin.endpoints.timeline.userTimelineByScreenName
import blue.starry.penicillin.endpoints.timeline.userTimelineByUserId
import blue.starry.penicillin.extensions.execute
import kotlinx.coroutines.flow.flow

object TweetFetcher {
    fun byUserScreenName(screenName: String, lastId: Long?, count: Int) = flow {
        TweetchimeTwitterClient.timeline.userTimelineByScreenName(screenName, sinceId = lastId, count = count).execute().forEach {
            emit(it)
        }
    }

    fun byUserId(id: Long, lastId: Long? = null, count: Int) = flow {
        TweetchimeTwitterClient.timeline.userTimelineByUserId(id, sinceId = lastId, count = count).execute().forEach {
            emit(it)
        }
    }

    fun byListId(id: Long, lastId: Long? = null, count: Int) = flow {
        TweetchimeTwitterClient.timeline.listTimeline(id, sinceId = lastId, count = count).execute().forEach {
            emit(it)
        }
    }
}
