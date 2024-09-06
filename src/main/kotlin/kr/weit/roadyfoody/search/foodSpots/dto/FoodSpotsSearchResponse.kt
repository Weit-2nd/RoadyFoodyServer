package kr.weit.roadyfoody.search.foodSpots.dto

import io.swagger.v3.oas.annotations.media.Schema
import kr.weit.roadyfoody.foodSpots.application.dto.FoodSpotsOperationHoursResponse
import java.time.LocalDateTime

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
    @Schema(description = "금일 영업 시간")
    val operationHours: FoodSpotsOperationHoursResponse,
    @Schema(description = "음식 카테고리 목록")
    val foodCategories: List<String>,
    @Schema(description = "음식점 이미지 URL")
    val imageUrl: String?,
    @Schema(description = "푸드트럭 여부")
    val foodTruck: Boolean,
    @Schema(description = "리뷰 평균 별점")
    val averageRating: Double,
    @Schema(description = "리뷰 개수")
    val reviewCount: Int,
    @Schema(description = "음식점 생성 일자")
    val createdDateTime: LocalDateTime,
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

@Schema(description = "코인 소모량 응답 데이터")
data class RequiredCoinResponse(
    @Schema(description = "코인 소모량")
    val requiredCoin: Int,
)
