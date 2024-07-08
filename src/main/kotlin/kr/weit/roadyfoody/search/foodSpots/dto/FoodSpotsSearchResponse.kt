package kr.weit.roadyfoody.search.foodSpots.dto

import io.swagger.v3.oas.annotations.media.Schema

data class FoodSpotsSearchResponse(
    val id: Long,
    val name: String,
    val longitude: Double,
    val latitude: Double,
    val open: OperationStatus,
    val foodCategories: List<String>,
)

data class FoodSpotsSearchResponses(
    @Schema(description = "검색 결과 항목 리스트")
    val items: List<FoodSpotsSearchResponse>,
)

enum class OperationStatus {
    OPEN,
    CLOSED,
    TEMPORARILY_CLOSED,
}
