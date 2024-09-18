package kr.weit.roadyfoody.ranking.application.service

import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsHistoryRepository
import kr.weit.roadyfoody.ranking.dto.UserRanking
import kr.weit.roadyfoody.ranking.exception.RankingNotFoundException
import kr.weit.roadyfoody.ranking.utils.LIKE_RANKING_KEY
import kr.weit.roadyfoody.ranking.utils.LIKE_RANKING_UPDATE_LOCK
import kr.weit.roadyfoody.ranking.utils.REPORT_RANKING_KEY
import kr.weit.roadyfoody.ranking.utils.REPORT_RANKING_UPDATE_LOCK
import kr.weit.roadyfoody.ranking.utils.REVIEW_RANKING_KEY
import kr.weit.roadyfoody.ranking.utils.REVIEW_RANKING_UPDATE_LOCK
import kr.weit.roadyfoody.ranking.utils.TOTAL_RANKING_KEY
import kr.weit.roadyfoody.ranking.utils.TOTAL_RANKING_UPDATE_LOCK
import kr.weit.roadyfoody.review.repository.FoodSpotsReviewRepository
import org.springframework.cache.CacheManager
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService

@Service
class RankingQueryService(
    private val redisTemplate: RedisTemplate<String, String>,
    private val foodSpotsHistoryRepository: FoodSpotsHistoryRepository,
    private val reviewRepository: FoodSpotsReviewRepository,
    private val rankingCommandService: RankingCommandService,
    private val executor: ExecutorService,
    private val cacheManager: CacheManager,
) {
    fun getReportRanking(size: Long): List<UserRanking> =
        getRanking(
            lockName = REPORT_RANKING_UPDATE_LOCK,
            size = size,
            key = REPORT_RANKING_KEY,
            dataProvider = foodSpotsHistoryRepository::findAllUserReportCount,
        )

    fun getReviewRanking(size: Long): List<UserRanking> =
        getRanking(
            lockName = REVIEW_RANKING_UPDATE_LOCK,
            size = size,
            key = REVIEW_RANKING_KEY,
            dataProvider = reviewRepository::findAllUserReviewCount,
        )

    fun getLikeRanking(size: Long): List<UserRanking> =
        getRanking(
            lockName = LIKE_RANKING_UPDATE_LOCK,
            size = size,
            key = LIKE_RANKING_KEY,
            dataProvider = reviewRepository::findAllUserLikeCount,
        )

    fun getTotalRanking(size: Long): List<UserRanking> =
        getRanking(
            lockName = TOTAL_RANKING_UPDATE_LOCK,
            size = size,
            key = TOTAL_RANKING_KEY,
            dataProvider = reviewRepository::findAllUserTotalCount,
        )

    private fun getRanking(
        lockName: String,
        size: Long,
        key: String,
        dataProvider: () -> List<UserRanking>,
    ): List<UserRanking> {
        val cache = cacheManager.getCache(key)
        val cachedData =
            cache?.get(key, List::class.java) as? List<String>
        if (!cachedData.isNullOrEmpty()) {
            return convertToUserRanking(cachedData.take(size.toInt()))
        }

        val ranking =
            redisTemplate
                .opsForList()
                .range(key, 0, size - 1)

        if (ranking.isNullOrEmpty()) {
            CompletableFuture.runAsync({
                rankingCommandService.updateRanking(
                    lockName = lockName,
                    key = key,
                    dataProvider = dataProvider,
                )
            }, executor)
            throw RankingNotFoundException()
        }

        return convertToUserRanking(ranking)
    }

    private fun convertToUserRanking(ranking: List<String>): List<UserRanking> =
        ranking.map { score ->
            val (userNickname, total) = score.split(":")
            UserRanking(
                userNickname = userNickname,
                total = total.toLong(),
            )
        }
}
