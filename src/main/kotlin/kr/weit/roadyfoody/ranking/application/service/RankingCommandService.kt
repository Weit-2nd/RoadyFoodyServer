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
) {
    @Async("asyncTask")
    @Scheduled(cron = "0 0 5 * * *")
    fun updateReportRanking() {
        updateRanking(
            lockName = REPORT_RANKING_UPDATE_LOCK,
            key = REPORT_RANKING_KEY,
            dataProvider = foodSpotsHistoryRepository::findAllUserReportCount,
        )
    }

    @Async("asyncTask")
    @Scheduled(cron = "0 0 5 * * *")
    fun updateReviewRanking() {
        updateRanking(
            lockName = REVIEW_RANKING_UPDATE_LOCK,
            key = REVIEW_RANKING_KEY,
            dataProvider = reviewRepository::findAllUserReviewCount,
        )
    }

    private fun updateRanking(
        lockName: String,
        key: String,
        dataProvider: () -> List<UserRanking>,
    ) {
        val lock: RLock = redissonClient.getLock(lockName)

        if (lock.tryLock(0, 10, TimeUnit.MINUTES)) {
            redisTemplate.delete(key)

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
        }
    }
}
