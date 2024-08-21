package kr.weit.roadyfoody.search.foodSpots.fixture

import kr.weit.roadyfoody.search.foodSpots.dto.FoodSpotsSearchCondition
import kr.weit.roadyfoody.search.foodSpots.dto.RequiredCoinRequest
import kr.weit.roadyfoody.search.foodSpots.dto.RequiredCoinResponse

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

fun createRequiredCoinRequest(
    centerLongitude: Double,
    centerLatitude: Double,
    radius: Int,
): RequiredCoinRequest =
    RequiredCoinRequest(
        centerLongitude = centerLongitude,
        centerLatitude = centerLatitude,
        radius = radius,
    )

fun createRequiredCoinResponse(coin: Int): RequiredCoinResponse = RequiredCoinResponse(coin)
