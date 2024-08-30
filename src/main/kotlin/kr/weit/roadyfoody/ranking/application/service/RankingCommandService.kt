package kr.weit.roadyfoody.ranking.application.service

import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsHistoryRepository
import kr.weit.roadyfoody.ranking.dto.UserRanking
import kr.weit.roadyfoody.ranking.utils.REPORT_RANKING_KEY
import kr.weit.roadyfoody.ranking.utils.REPORT_RANKING_UPDATE_LOCK
import kr.weit.roadyfoody.ranking.utils.REVIEW_RANKING_KEY
import kr.weit.roadyfoody.ranking.utils.REVIEW_RANKING_UPDATE_LOCK
import kr.weit.roadyfoody.review.repository.FoodSpotsReviewRepository
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import java.util.concurrent.TimeUnit

class RankingCommandService(
    private val redisTemplate: RedisTemplate<String, String>,
    private val redissonClient: RedissonClient,
    private val foodSpotsHistoryRepository: FoodSpotsHistoryRepository,
    private val reviewRepository: FoodSpotsReviewRepository,
) {
    @Scheduled(cron = "0 0 5 * * *")
    fun updateReportRanking() {
        val lock: RLock = redissonClient.getLock(REPORT_RANKING_UPDATE_LOCK)

        if (lock.tryLock(0, 10, TimeUnit.MINUTES)) {
            redisTemplate.delete(REPORT_RANKING_KEY)

            val userReports = foodSpotsHistoryRepository.findAllUserReportCount()

            userReports.forEach {
                redisTemplate
                    .opsForZSet()
                    .add(
                        REPORT_RANKING_KEY,
                        it.userNickname,
                        it.score.toDouble(),
                    )
            }
        }
    }

    @Scheduled(cron = "0 0 5 * * *")
    fun updateReviewRanking() {
        val lock: RLock = redissonClient.getLock(REVIEW_RANKING_UPDATE_LOCK)

        if (lock.tryLock(0, 10, TimeUnit.MINUTES)) {
            redisTemplate.delete(REVIEW_RANKING_KEY)

            val userReviews = reviewRepository.findAllUserReviewCount()

            userReviews.forEach {
                redisTemplate
                    .opsForZSet()
                    .add(
                        REVIEW_RANKING_KEY,
                        it.userNickname,
                        it.score.toDouble(),
                    )
            }
        }
    }

    private fun userRanking(
        lockName: String,
        key: String,
        dataProvider: () -> List<UserRanking>,
    ) {
        val lock: RLock = redissonClient.getLock(lockName)

        if (lock.tryLock(0, 10, TimeUnit.MINUTES)) {
            redisTemplate.delete(key)

            val userReviews = dataProvider()

            userReviews.forEach {
                redisTemplate
                    .opsForZSet()
                    .add(
                        key,
                        it.userNickname,
                        it.score.toDouble(),
                    )
            }
        }
    }
}
