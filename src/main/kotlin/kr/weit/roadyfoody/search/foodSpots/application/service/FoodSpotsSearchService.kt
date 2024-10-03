package kr.weit.roadyfoody.search.foodSpots.application.service

import POPULAR_SEARCH_KEY
import USER_ENTITY_LOCK_KEY
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import kr.weit.roadyfoody.common.exception.ErrorCode
import kr.weit.roadyfoody.common.exception.RoadyFoodyBadRequestException
import kr.weit.roadyfoody.foodSpots.application.service.FoodSpotsQueryService
import kr.weit.roadyfoody.global.annotation.DistributedLock
import kr.weit.roadyfoody.global.circuitbreaker.targetexception.REDIS_CIRCUIT_BREAKER_TARGET_EXCEPTIONS
import kr.weit.roadyfoody.rewards.application.service.RewardsCommandService
import kr.weit.roadyfoody.rewards.domain.RewardType
import kr.weit.roadyfoody.rewards.domain.Rewards
import kr.weit.roadyfoody.search.foodSpots.domain.FoodSpotsSearchHistory
import kr.weit.roadyfoody.search.foodSpots.domain.SearchCoinCache
import kr.weit.roadyfoody.search.foodSpots.dto.FoodSpotsPopularSearchesResponse
import kr.weit.roadyfoody.search.foodSpots.dto.FoodSpotsSearchCondition
import kr.weit.roadyfoody.search.foodSpots.dto.FoodSpotsSearchResponses
import kr.weit.roadyfoody.search.foodSpots.dto.RequiredCoinRequest
import kr.weit.roadyfoody.search.foodSpots.dto.RequiredCoinResponse
import kr.weit.roadyfoody.search.foodSpots.repository.FoodSpotsSearchHistoryRepository
import kr.weit.roadyfoody.search.foodSpots.repository.SearchCoinCacheRepository
import kr.weit.roadyfoody.user.application.service.UserCommandService
import kr.weit.roadyfoody.user.domain.User
import org.springframework.cache.CacheManager
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.CompletableFuture
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

private const val EARTH_RADIUS = 6371
private const val BASE_RADIUS = 500

@Service
class FoodSpotsSearchService(
    private val foodSpotsQueryService: FoodSpotsQueryService,
    private val userCommandService: UserCommandService,
    private val searchCoinCacheRepository: SearchCoinCacheRepository,
    private val rewardsCommandService: RewardsCommandService,
    private val foodSpotsSearchHistoryRepository: FoodSpotsSearchHistoryRepository,
    private val foodSpotsSearchCommandService: FoodSpotsSearchCommandService,
    private val redisTemplate: RedisTemplate<String, String>,
    private val cacheManager: CacheManager,
) {
    @DistributedLock(lockName = USER_ENTITY_LOCK_KEY, identifier = "user")
    @Transactional
    fun searchFoodSpots(
        user: User,
        foodSpotsSearchQuery: FoodSpotsSearchCondition,
    ): FoodSpotsSearchResponses {
        val searchRadius = foodSpotsSearchQuery.radius

        if (BASE_RADIUS == searchRadius) {
            return foodSpotsQueryService.searchFoodSpots(foodSpotsSearchQuery)
        }

        val coinRequired = calculateRequiredCoin(searchRadius)
        val existingCache = isCacheValidate(user, foodSpotsSearchQuery.centerLongitude, foodSpotsSearchQuery.centerLatitude, searchRadius)

        if (existingCache != null) {
            return foodSpotsQueryService.searchFoodSpots(foodSpotsSearchQuery)
        }
        if (coinRequired > user.coin) {
            throw RoadyFoodyBadRequestException(ErrorCode.COIN_NOT_ENOUGH)
        }
        val foodSpotsSearchResponses = foodSpotsQueryService.searchFoodSpots(foodSpotsSearchQuery)

        val newCache =
            SearchCoinCache.of(
                userId = user.id,
                longitude = foodSpotsSearchQuery.centerLongitude,
                latitude = foodSpotsSearchQuery.centerLatitude,
                radius = searchRadius,
            )
        searchCoinCacheRepository.save(newCache)

        rewardsCommandService.createRewards(
            Rewards(
                user = user,
                foodSpotsHistory = null,
                rewardPoint = coinRequired,
                coinReceived = false,
                rewardType = RewardType.SEARCH_SPOT,
            ),
        )
        userCommandService.decreaseCoin(user.id, coinRequired)
        if (foodSpotsSearchQuery.name != null) {
            foodSpotsSearchHistoryRepository.save(FoodSpotsSearchHistory(keyword = foodSpotsSearchQuery.name))
        }
        return foodSpotsSearchResponses
    }

    @CircuitBreaker(name = "redisCircuitBreaker", fallbackMethod = "fallbackRequiredCoin")
    @Transactional(readOnly = true)
    fun getRequiredCoin(
        user: User,
        requiredCoinRequest: RequiredCoinRequest,
    ): RequiredCoinResponse {
        val searchRadius = requiredCoinRequest.radius

        if (BASE_RADIUS == searchRadius) {
            return RequiredCoinResponse(0)
        }

        val coinRequired = calculateRequiredCoin(searchRadius)
        val existingCache = isCacheValidate(user, requiredCoinRequest.centerLongitude, requiredCoinRequest.centerLatitude, searchRadius)

        if (existingCache != null) {
            return RequiredCoinResponse(0)
        }

        return RequiredCoinResponse(coinRequired)
    }

    private fun calculateDistance(
        startLat: Double,
        startLong: Double,
        endLat: Double,
        endLong: Double,
    ): Double {
        var startLat = startLat
        var endLat = endLat
        val dLat = Math.toRadians((endLat - startLat))
        val dLong = Math.toRadians((endLong - startLong))

        startLat = Math.toRadians(startLat)
        endLat = Math.toRadians(endLat)

        val a = haversine(dLat) + cos(startLat) * cos(endLat) * haversine(dLong)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return EARTH_RADIUS * c
    }

    fun haversine(value: Double): Double = sin(value / 2).pow(2.0)

    private fun calculateRequiredCoin(searchRadius: Int): Int {
        val additionalRadius = (searchRadius - BASE_RADIUS) / BASE_RADIUS
        val coinRequired = (2.0.pow(additionalRadius.toDouble()) * 100).toInt()
        return coinRequired
    }

    private fun isCacheValidate(
        user: User,
        centerLongitude: Double,
        centerLatitude: Double,
        searchRadius: Int,
    ): SearchCoinCache? {
        val recentSearches = searchCoinCacheRepository.findByUserId(user.id)

        val existingCache =
            recentSearches.find { cache ->
                calculateDistance(
                    centerLatitude,
                    centerLongitude,
                    cache.latitude,
                    cache.longitude,
                ) <= 0.1 &&
                    searchRadius <= cache.radius
            }

        return existingCache
    }

    fun fallbackRequiredCoin(
        user: User,
        requiredCoinRequest: RequiredCoinRequest,
        exception: Exception,
    ): RequiredCoinResponse {
        if (REDIS_CIRCUIT_BREAKER_TARGET_EXCEPTIONS.any { it.isInstance(exception) }) {
            return RequiredCoinResponse(0)
        }
        throw exception
    }

    fun getPopularSearches(): List<FoodSpotsPopularSearchesResponse> {
        val cachedLocalPopularSearches = cacheManager.getCache(POPULAR_SEARCH_KEY)
        val cacheData = cachedLocalPopularSearches?.get(POPULAR_SEARCH_KEY, String::class.java)
        if (cacheData != null) {
            return convertToResponse(cacheData)
        }
        val cachedPopularSearches = redisTemplate.opsForValue().get(POPULAR_SEARCH_KEY)
        if (cachedPopularSearches == null) {
            CompletableFuture.runAsync {
                foodSpotsSearchCommandService.updatePopularSearchesCache()
            }
            throw RoadyFoodyBadRequestException(ErrorCode.POPULAR_SEARCHES_NOT_FOUND)
        }
        return convertToResponse(cachedPopularSearches)
    }

    private fun convertToResponse(popularSearches: String): List<FoodSpotsPopularSearchesResponse> =
        popularSearches.split(":").mapIndexed { index, keyword ->
            FoodSpotsPopularSearchesResponse(
                index + 1,
                keyword,
            )
        }
}
