package kr.weit.roadyfoody.user.application.dto

import io.swagger.v3.oas.annotations.media.Schema
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsHistory
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsPhoto
import kr.weit.roadyfoody.foodSpots.domain.ReportFoodCategory
import java.time.LocalDateTime

data class UserReportHistoriesResponse(
    @Schema(description = "리포트 이력 ID", example = "1")
    val id: Long,
    @Schema(description = "유저 ID", example = "1")
    val userId: Long,
    @Schema(description = "음식점 ID", example = "1")
    val foodSpotsId: Long,
    @Schema(description = "상호명", example = "명륜진사갈비 본사")
    val name: String,
    @Schema(description = "경도", example = "127.12312219099")
    val longitude: Double,
    @Schema(description = "위도", example = "37.4940529587731")
    val latitude: Double,
    @Schema(description = "생성일시", example = "2021-08-01T00:00:00")
    val createdDateTime: LocalDateTime,
    @Schema(description = "리포트 사진 리스트")
    val reportPhotos: List<UserReportPhotoResponse>,
    @Schema(description = "음식 카테고리 리스트")
    val categories: List<UserReportCategoryResponse>,
) {
    constructor(
        foodSpotsHistory: FoodSpotsHistory,
        reportPhotoResponse: List<UserReportPhotoResponse>,
        categories: List<UserReportCategoryResponse>,
    ) : this(
        id = foodSpotsHistory.id,
        userId = foodSpotsHistory.user.id,
        foodSpotsId = foodSpotsHistory.foodSpots.id,
        name = foodSpotsHistory.name,
        longitude = foodSpotsHistory.point.x,
        latitude = foodSpotsHistory.point.y,
        createdDateTime = foodSpotsHistory.createdDateTime,
        reportPhotos = reportPhotoResponse,
        categories = categories,
    )
}

data class UserReportCategoryResponse(
    @Schema(description = "리포트 카테고리 ID", example = "1")
    val id: Long,
    @Schema(description = "음식 카테고리명", example = "한식")
    val name: String,
) {
    constructor(reportFoodCategory: ReportFoodCategory) : this(
        id = reportFoodCategory.id,
        name = reportFoodCategory.foodCategory.name,
    )
}

data class UserReportPhotoResponse(
    @Schema(description = "리포트 사진 ID", example = "1")
    val id: Long,
    @Schema(description = "리포트 사진 URL")
    val url: String,
) {
    constructor(reportPhoto: FoodSpotsPhoto, url: String) : this(
        id = reportPhoto.id,
        url = url,
    )
}
