package kr.weit.roadyfoody.search.foodSpots.fixture

import kr.weit.roadyfoody.search.foodSpots.dto.CalculateCoinResponse
import kr.weit.roadyfoody.search.foodSpots.dto.FoodSpotsSearchCondition

fun createFoodSpotsSearchCondition(
    centerLongitude: Double,
    centerLatitude: Double,
    radius: Int,
    name: String? = null,
    categoryIds: List<Long> = emptyList(),
): FoodSpotsSearchCondition =
    FoodSpotsSearchCondition(
        centerLongitude = centerLongitude,
        centerLatitude = centerLatitude,
        radius = radius,
        name = name,
        categoryIds = categoryIds,
    )

fun createCalculateCoinResponse(coin: Int): CalculateCoinResponse = CalculateCoinResponse(coin)
