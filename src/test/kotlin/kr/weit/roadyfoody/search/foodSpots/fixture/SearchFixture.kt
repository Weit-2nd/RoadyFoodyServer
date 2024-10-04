package kr.weit.roadyfoody.search.foodSpots.fixture

import kr.weit.roadyfoody.search.foodSpots.domain.FoodSpotsSearchHistory
import kr.weit.roadyfoody.search.foodSpots.dto.FoodSpotsPopularSearchesResponse
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

fun createFoodSpotsSearchKeywords(): List<String> = listOf("횟집", "치킨")

fun createFoodSpotsSearchHistory(keyword: String = "횟집"): FoodSpotsSearchHistory = FoodSpotsSearchHistory(keyword = keyword)

fun createFoodSpotsPopularSearchesResponse(
    foodSpotsSearchKeywords: List<String> = createFoodSpotsSearchKeywords(),
): List<FoodSpotsPopularSearchesResponse> =
    foodSpotsSearchKeywords.mapIndexed { index, keyword ->
        FoodSpotsPopularSearchesResponse(
            index + 1,
            keyword,
        )
    }
