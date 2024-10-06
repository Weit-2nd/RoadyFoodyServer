package kr.weit.roadyfoody.ranking.application.service

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsHistoryRepository
import kr.weit.roadyfoody.global.cache.CachePublisher
import kr.weit.roadyfoody.ranking.dto.UserRanking
import kr.weit.roadyfoody.ranking.utils.LIKE_RANKING_KEY
import kr.weit.roadyfoody.ranking.utils.LIKE_RANKING_UPDATE_LOCK
import kr.weit.roadyfoody.ranking.utils.RANKING_TOPIC
import kr.weit.roadyfoody.ranking.utils.REPORT_RANKING_KEY
import kr.weit.roadyfoody.ranking.utils.REPORT_RANKING_UPDATE_LOCK
import kr.weit.roadyfoody.ranking.utils.REVIEW_RANKING_KEY
import kr.weit.roadyfoody.ranking.utils.REVIEW_RANKING_UPDATE_LOCK
import kr.weit.roadyfoody.ranking.utils.TOTAL_RANKING_KEY
import kr.weit.roadyfoody.ranking.utils.TOTAL_RANKING_UPDATE_LOCK
import kr.weit.roadyfoody.review.repository.FoodSpotsReviewRepository
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class RankingCommandService(
    private val redisTemplate: RedisTemplate<String, String>,
    private val redissonClient: RedissonClient,
    private val foodSpotsHistoryRepository: FoodSpotsHistoryRepository,
    private val reviewRepository: FoodSpotsReviewRepository,
    private val cachePublisher: CachePublisher,
) {
    @Async("asyncTask")
    @Scheduled(cron = "0 0 5 * * *")
    @CircuitBreaker(name = "redisCircuitBreaker")
    fun updateReportRanking() {
        updateRanking(
            lockName = REPORT_RANKING_UPDATE_LOCK,
            key = REPORT_RANKING_KEY,
            dataProvider = foodSpotsHistoryRepository::findAllUserReportCount,
        )
    }

    @Async("asyncTask")
    @Scheduled(cron = "0 0 5 * * *")
    @CircuitBreaker(name = "redisCircuitBreaker")
    fun updateReviewRanking() {
        updateRanking(
            lockName = REVIEW_RANKING_UPDATE_LOCK,
            key = REVIEW_RANKING_KEY,
            dataProvider = reviewRepository::findAllUserReviewCount,
        )
    }

    @Async("asyncTask")
    @Scheduled(cron = "0 0 5 * * *")
    @CircuitBreaker(name = "redisCircuitBreaker")
    fun updateLikeRanking() {
        updateRanking(
            lockName = LIKE_RANKING_UPDATE_LOCK,
            key = LIKE_RANKING_KEY,
            dataProvider = reviewRepository::findAllUserLikeCount,
        )
    }

    @Async("asyncTask")
    @Scheduled(cron = "0 0 5 * * *")
    @CircuitBreaker(name = "redisCircuitBreaker")
    fun updateTotalRanking() {
        updateRanking(
            lockName = TOTAL_RANKING_UPDATE_LOCK,
            key = TOTAL_RANKING_KEY,
            dataProvider = reviewRepository::findAllUserTotalCount,
        )
    }

    fun updateRanking(
        lockName: String,
        key: String,
        dataProvider: () -> List<UserRanking>,
    ) {
        val lock: RLock = redissonClient.getLock(lockName)
        if (lock.tryLock(0, 10, TimeUnit.MINUTES)) {
            val ranking =
                redisTemplate
                    .opsForList()
                    .range(key, 0, -1)
            val userRanking = dataProvider()

            redisTemplate.delete(key)

            val splitRanking =
                ranking?.map { score ->
                    score.split(":")
                }

            val rankingData =
                userRanking.mapIndexed { index, it ->
                    val rankChange =
                        splitRanking
                            ?.indexOfFirst { parts ->
                                parts[2] == it.userId.toString()
                            }?.let { result ->
                                if (result == -1) 0 else result - index
                            }

                    "${index + 1}:${it.userNickname}:${it.userId}:${it.profileImageUrl}:$rankChange"
                }

            redisTemplate
                .opsForList()
                .rightPushAll(key, rankingData)

            cachePublisher.publishCacheUpdate(ChannelTopic.of(RANKING_TOPIC), key)
        }
    }
}
