package kr.weit.roadyfoody.search.foodSpots.application.service

import jakarta.transaction.Transactional
import kr.weit.roadyfoody.common.exception.ErrorCode
import kr.weit.roadyfoody.common.exception.RoadyFoodyBadRequestException
import kr.weit.roadyfoody.foodSpots.application.service.FoodSpotsQueryService
import kr.weit.roadyfoody.search.foodSpots.domain.SearchCoinCache
import kr.weit.roadyfoody.search.foodSpots.dto.FoodSpotsSearchCondition
import kr.weit.roadyfoody.search.foodSpots.dto.FoodSpotsSearchResponses
import kr.weit.roadyfoody.search.foodSpots.repository.SearchCoinCacheRepository
import kr.weit.roadyfoody.user.application.UserCommandService
import kr.weit.roadyfoody.user.domain.User
import org.springframework.stereotype.Service
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

private const val EARTH_RADIUS = 6371

@Service
class FoodSpotsSearchService(
    private val foodSpotsQueryService: FoodSpotsQueryService,
    private val userCommandService: UserCommandService,
    private val searchCoinCacheRepository: SearchCoinCacheRepository,
) {
    @Transactional
    fun searchFoodSpots(
        user: User,
        foodSpotsSearchQuery: FoodSpotsSearchCondition,
    ): FoodSpotsSearchResponses {
        val baseRadius = 500
        val searchRadius = foodSpotsSearchQuery.radius

        if (baseRadius == searchRadius) {
            return foodSpotsQueryService.searchFoodSpots(foodSpotsSearchQuery)
        }

        val additionalRadius = (searchRadius - baseRadius) / baseRadius
        val coinRequired = (2.0.pow(additionalRadius.toDouble()) * 100).toInt()
        val recentSearches = searchCoinCacheRepository.findByUserId(user.id)

        println(recentSearches.size)

        val existingCache =
            recentSearches.find { cache ->
                println(
                    calculateDistance(
                        foodSpotsSearchQuery.centerLatitude,
                        foodSpotsSearchQuery.centerLongitude,
                        cache.latitude,
                        cache.longitude,
                    ),
                )
                print("$searchRadius ${cache.radius}")
                calculateDistance(
                    foodSpotsSearchQuery.centerLatitude,
                    foodSpotsSearchQuery.centerLongitude,
                    cache.latitude,
                    cache.longitude,
                ) <= 0.1 &&
                    searchRadius <= cache.radius
            }

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
        userCommandService.decreaseCoin(user.id, coinRequired)

        return foodSpotsSearchResponses
    }

    fun calculateDistance(
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

    fun haversine(`val`: Double): Double = sin(`val` / 2).pow(2.0)
}
