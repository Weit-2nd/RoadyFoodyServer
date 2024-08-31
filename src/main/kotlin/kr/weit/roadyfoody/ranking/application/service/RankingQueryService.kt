package kr.weit.roadyfoody.ranking.application.service

import kr.weit.roadyfoody.ranking.dto.UserRanking
import kr.weit.roadyfoody.ranking.utils.REPORT_RANKING_KEY
import kr.weit.roadyfoody.ranking.utils.REVIEW_RANKING_KEY
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class RankingQueryService(
    private val redisTemplate: RedisTemplate<String, String>,
) {
    fun getReportRanking(size: Long): List<UserRanking> = getRanking(size, REPORT_RANKING_KEY)

    fun getReviewRanking(size: Long): List<UserRanking> = getRanking(size, REVIEW_RANKING_KEY)

    private fun getRanking(
        size: Long,
        key: String,
    ): List<UserRanking> {
        val typedTuple =
            redisTemplate.opsForZSet().reverseRangeWithScores(
                key,
                0,
                size - 1,
            ) ?: emptySet()

        return typedTuple.map { tuple ->
            val userNickname = tuple.value ?: ""
            val total = tuple.score ?: 0.0

            UserRanking(
                userNickname = userNickname,
                total = total.toLong(),
            )
        }
    }
}
