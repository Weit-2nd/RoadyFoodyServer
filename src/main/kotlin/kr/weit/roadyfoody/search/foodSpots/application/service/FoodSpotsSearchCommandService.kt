package kr.weit.roadyfoody.search.foodSpots.application.service

import POPULAR_SEARCH_KEY
import POPULAR_SEARCH_SCHEDULER_LOCK
import POPULAR_SEARCH_TOPIC
import kr.weit.roadyfoody.global.cache.CachePublisher
import kr.weit.roadyfoody.search.foodSpots.repository.FoodSpotsSearchHistoryRepository
import org.redisson.api.RedissonClient
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.concurrent.TimeUnit

@Service
class FoodSpotsSearchCommandService(
    private val foodSpotsSearchHistoryRepository: FoodSpotsSearchHistoryRepository,
    private val redisTemplate: RedisTemplate<String, String>,
    private val cachePublisher: CachePublisher,
    private val redissonClient: RedissonClient,
) {
    @Scheduled(cron = "0 0 * * * *")
    fun updatePopularSearchesCache() {
        val lock = redissonClient.getLock(POPULAR_SEARCH_SCHEDULER_LOCK)
        val isLocked = lock.tryLock(0, 10, TimeUnit.MINUTES)
        if (isLocked) {
            val popularSearches =
                foodSpotsSearchHistoryRepository
                    .getRecentPopularSearches()
                    .joinToString(separator = ":")
            if (popularSearches.isNotBlank()) {
                redisTemplate
                    .opsForValue()
                    .set(POPULAR_SEARCH_KEY, popularSearches, Duration.ofHours(1).plusMinutes(1))
                cachePublisher.publishCacheUpdate(
                    ChannelTopic.of(POPULAR_SEARCH_TOPIC),
                    POPULAR_SEARCH_KEY,
                )
            }
        }
    }
}
