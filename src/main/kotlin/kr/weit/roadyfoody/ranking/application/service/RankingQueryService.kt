package kr.weit.roadyfoody.ranking.application.service

import kr.weit.roadyfoody.foodSpots.application.dto.UserReportCount
import kr.weit.roadyfoody.ranking.utils.REPORT_RANKING_KEY
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class RankingQueryService(
    private val redisTemplate: RedisTemplate<String, String>,
) {
    fun getReportRanking(size: Long): List<UserReportCount> {
        val typedTuple =
            redisTemplate.opsForZSet().reverseRangeWithScores(
                REPORT_RANKING_KEY,
                0,
                size - 1,
            ) ?: emptySet()

        return typedTuple.map { tuple ->
            val userNickname = tuple.value ?: ""
            val reportCount = tuple.score ?: 0.0

            UserReportCount(
                userNickname = userNickname,
                reportCount = reportCount.toLong(),
            )
        }
    }
}
