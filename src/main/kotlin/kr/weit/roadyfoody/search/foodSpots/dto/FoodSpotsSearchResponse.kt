package kr.weit.roadyfoody.search.foodSpots.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "음식점 검색 응답 데이터")
data class FoodSpotsSearchResponse(
    @Schema(description = "음식점 ID")
    val id: Long,
    @Schema(description = "음식점 이름")
    val name: String,
    @Schema(description = "경도")
    val longitude: Double,
    @Schema(description = "위도")
    val latitude: Double,
    @Schema(description = "영업 상태")
    val open: OperationStatus,
    @Schema(description = "음식 카테고리 목록")
    val foodCategories: List<String>,
)

@Schema(description = "음식점 검색 응답 데이터 리스트")
data class FoodSpotsSearchResponses(
    @Schema(description = "검색 결과 항목 리스트")
    val items: List<FoodSpotsSearchResponse>,
)

@Schema(description = "운영 상태")
enum class OperationStatus {
    @Schema(description = "영업 중")
    OPEN,

    @Schema(description = "영업 종료")
    CLOSED,

    @Schema(description = "임시 휴무")
    TEMPORARILY_CLOSED,
}
