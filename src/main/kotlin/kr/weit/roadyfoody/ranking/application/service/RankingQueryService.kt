package kr.weit.roadyfoody.ranking.application.service

import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsHistoryRepository
import kr.weit.roadyfoody.ranking.dto.UserRanking
import kr.weit.roadyfoody.ranking.utils.LIKE_RANKING_KEY
import kr.weit.roadyfoody.ranking.utils.REPORT_RANKING_KEY
import kr.weit.roadyfoody.ranking.utils.REVIEW_RANKING_KEY
import kr.weit.roadyfoody.review.repository.FoodSpotsReviewRepository
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class RankingQueryService(
    private val redisTemplate: RedisTemplate<String, String>,
    private val foodSpotsHistoryRepository: FoodSpotsHistoryRepository,
    private val reviewRepository: FoodSpotsReviewRepository,
) {
    fun getReportRanking(size: Long): List<UserRanking> =
        getRanking(
            size = size,
            key = REPORT_RANKING_KEY,
            dataProvider = foodSpotsHistoryRepository::findAllUserReportCount,
        )

    fun getReviewRanking(size: Long): List<UserRanking> =
        getRanking(
            size = size,
            key = REVIEW_RANKING_KEY,
            dataProvider = reviewRepository::findAllUserReviewCount,
        )

    fun getLikeRanking(size: Long): List<UserRanking> =
        getRanking(
            size = size,
            key = LIKE_RANKING_KEY,
            dataProvider = reviewRepository::findAllUserLikeCount,
        )

    private fun getRanking(
        size: Long,
        key: String,
        dataProvider: () -> List<UserRanking>,
    ): List<UserRanking> {
        val typedTuple =
            redisTemplate.opsForZSet().reverseRangeWithScores(
                key,
                0,
                size - 1,
            )

        return typedTuple?.takeIf { it.isNotEmpty() }?.let {
            it.map { tuple ->
                val userNickname = tuple.value ?: ""
                val total = tuple.score ?: 0.0

                UserRanking(
                    userNickname = userNickname,
                    total = total.toLong(),
                )
            }
        } ?: fallback(key, dataProvider)
    }

    private fun fallback(
        key: String,
        dataProvider: () -> List<UserRanking>,
    ): List<UserRanking> {
        val userRanking = dataProvider()

        userRanking.forEach {
            redisTemplate
                .opsForZSet()
                .add(
                    key,
                    it.userNickname,
                    it.total.toDouble(),
                )
        }
        return userRanking
    }
}
