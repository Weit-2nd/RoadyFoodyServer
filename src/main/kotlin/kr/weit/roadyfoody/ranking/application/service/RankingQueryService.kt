package kr.weit.roadyfoody.ranking.application.service

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsHistoryRepository
import kr.weit.roadyfoody.global.circuitbreaker.targetexception.REDIS_CIRCUIT_BREAKER_TARGET_EXCEPTIONS
import kr.weit.roadyfoody.global.service.ImageService
import kr.weit.roadyfoody.ranking.dto.UserRanking
import kr.weit.roadyfoody.ranking.dto.UserRankingResponse
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
    private val imageService: ImageService,
) {
    @CircuitBreaker(name = "redisCircuitBreaker", fallbackMethod = "fallbackRankings")
    fun getReportRanking(
        size: Long,
        start: Long,
    ): List<UserRankingResponse> =
        getRanking(
            lockName = REPORT_RANKING_UPDATE_LOCK,
            size = size,
            start = start,
            key = REPORT_RANKING_KEY,
            dataProvider = foodSpotsHistoryRepository::findAllUserReportCount,
        )

    @CircuitBreaker(name = "redisCircuitBreaker", fallbackMethod = "fallbackRankings")
    fun getReviewRanking(
        size: Long,
        start: Long,
    ): List<UserRankingResponse> =
        getRanking(
            lockName = REVIEW_RANKING_UPDATE_LOCK,
            size = size,
            start = start,
            key = REVIEW_RANKING_KEY,
            dataProvider = reviewRepository::findAllUserReviewCount,
        )

    @CircuitBreaker(name = "redisCircuitBreaker", fallbackMethod = "fallbackRankings")
    fun getLikeRanking(
        size: Long,
        start: Long,
    ): List<UserRankingResponse> =
        getRanking(
            lockName = LIKE_RANKING_UPDATE_LOCK,
            size = size,
            start = start,
            key = LIKE_RANKING_KEY,
            dataProvider = reviewRepository::findAllUserLikeCount,
        )

    @CircuitBreaker(name = "redisCircuitBreaker", fallbackMethod = "fallbackRankings")
    fun getTotalRanking(
        size: Long,
        start: Long,
    ): List<UserRankingResponse> =
        getRanking(
            lockName = TOTAL_RANKING_UPDATE_LOCK,
            size = size,
            start = start,
            key = TOTAL_RANKING_KEY,
            dataProvider = reviewRepository::findAllUserTotalCount,
        )

    private fun getRanking(
        lockName: String,
        size: Long,
        start: Long,
        key: String,
        dataProvider: () -> List<UserRanking>,
    ): List<UserRankingResponse> {
        val cache = cacheManager.getCache(key)
        val cachedData =
            cache?.get(key, List::class.java) as? List<String>
        if (!cachedData.isNullOrEmpty()) {
            return convertToUserRanking(cachedData.drop(start.toInt() - 1).take(size.toInt()))
        }

        val ranking =
            redisTemplate
                .opsForList()
                .range(key, start - 1, start + size - 2)

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

    private fun convertToUserRanking(ranking: List<String>): List<UserRankingResponse> =
        ranking.map { score ->
            val (ranking, userNickname, userId, profileImage, rankChange) = score.split(":")
            val profileImageUrl =
                profileImage.let {
                    imageService.getDownloadUrl(it)
                }
            UserRankingResponse(
                ranking = ranking.toLong(),
                userNickname = userNickname,
                userId = userId.toLong(),
                profileImageUrl = profileImageUrl,
                rankChange = rankChange.toLong(),
            )
        }

    fun fallbackRankings(
        size: Long,
        start: Long,
        throwable: Throwable,
    ): List<UserRankingResponse> {
        if (REDIS_CIRCUIT_BREAKER_TARGET_EXCEPTIONS.any { it.isInstance(throwable) }) {
            return emptyList()
        }
        throw throwable
    }
}
