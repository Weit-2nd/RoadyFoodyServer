package kr.weit.roadyfoody.search.foodSpots.application.service

import POPULAR_SEARCH_KEY
import kr.weit.roadyfoody.search.foodSpots.repository.FoodSpotsSearchHistoryRepository
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class FoodSpotsSearchCommandService(
    private val foodSpotsSearchHistoryRepository: FoodSpotsSearchHistoryRepository,
    private val redisTemplate: StringRedisTemplate,
) {
    @Scheduled(cron = "0 0 * * * *")
    fun updatePopularSearchesCache() {
        val popularSearches =
            foodSpotsSearchHistoryRepository
                .getRecentPopularSearches()
                .joinToString(separator = ":")
        if (popularSearches.isNotBlank()) {
            redisTemplate
                .opsForValue()
                .set(POPULAR_SEARCH_KEY, popularSearches, Duration.ofHours(1).plusMinutes(1))
        }
    }
}
