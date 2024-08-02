package kr.weit.roadyfoody.foodSpots.application.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import kr.weit.roadyfoody.foodSpots.domain.DayOfWeek
import kr.weit.roadyfoody.foodSpots.domain.FoodSpots
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsHistory
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsOperationHours
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsPhoto
import kr.weit.roadyfoody.foodSpots.domain.ReportFoodCategory
import kr.weit.roadyfoody.foodSpots.domain.ReportOperationHours
import kr.weit.roadyfoody.foodSpots.utils.FOOD_SPOTS_NAME_REGEX_DESC
import kr.weit.roadyfoody.foodSpots.utils.FOOD_SPOTS_NAME_REGEX_STR
import kr.weit.roadyfoody.foodSpots.utils.OPERATION_HOURS_REGEX_DESC
import kr.weit.roadyfoody.foodSpots.utils.OPERATION_HOURS_REGEX_STR
import kr.weit.roadyfoody.foodSpots.validator.Latitude
import kr.weit.roadyfoody.foodSpots.validator.Longitude
import kr.weit.roadyfoody.global.utils.CoordinateUtils.Companion.createCoordinate
import kr.weit.roadyfoody.review.application.dto.ReviewPhotoResponse
import kr.weit.roadyfoody.review.domain.FoodSpotsReview
import kr.weit.roadyfoody.user.application.dto.ReviewerInfoResponse
import kr.weit.roadyfoody.user.domain.User
import java.time.LocalDateTime

data class ReportRequest(
    @Schema(
        description =
            "상호명 : $FOOD_SPOTS_NAME_REGEX_DESC\t(허용된 특수문자 : 마침표 (.) 쉼표 (,) 작은따옴표 (') 가운데점 (·) 앰퍼샌드 (&) 하이픈 (-))",
        example = "명륜진사갈비 본사",
    )
    @field:Pattern(regexp = FOOD_SPOTS_NAME_REGEX_STR, message = FOOD_SPOTS_NAME_REGEX_DESC)
    @field:NotBlank(message = "상호명은 필수입니다.")
    val name: String,
    @Schema(description = "경도", example = "127.12312219099")
    @field:NotNull(message = "경도는 필수입니다.")
    @field:Longitude
    val longitude: Double,
    @Schema(description = "위도", example = "37.4940529587731")
    @field:NotNull(message = "위도는 필수입니다.")
    @field:Latitude
    val latitude: Double,
    @NotNull(message = "음식점 여부는 필수입니다.")
    @Schema(description = "이동여부(푸드 트럭, 푸드 카트 등 여부)", example = "false")
    val foodTruck: Boolean,
    @Schema(description = "영업 여부", example = "true")
    @NotNull(message = "영업 여부는 필수입니다.")
    val open: Boolean,
    @Schema(description = "폐업 여부", example = "false")
    @NotNull(message = "폐업 여부는 필수입니다.")
    val closed: Boolean,
    @Schema(description = "음식 카테고리", example = "[1, 2]")
    @field:NotEmpty(message = "음식 카테고리는 최소 1개 이상 선택해야 합니다.")
    val foodCategories: Set<Long>,
    @field:Valid
    @Schema(
        description = "운영시간 리스트 ex) 사용자가 월/수/금의 운영시간 입력-> 월/수/금 데이터만 보내주세요. 없을 경우, 빈 배열",
    )
    val operationHours: List<OperationHoursRequest>,
) {
    fun toFoodSpotsEntity() =
        FoodSpots(
            name = name,
            point = createCoordinate(longitude, latitude),
            foodTruck = foodTruck,
            open = open,
            storeClosure = closed,
            foodCategoryList = mutableListOf(),
            operationHoursList = mutableListOf(),
        )

    fun toFoodSpotsHistoryEntity(
        foodSpots: FoodSpots,
        user: User,
    ) = FoodSpotsHistory(
        name = name,
        foodSpots = foodSpots,
        user = user,
        point = createCoordinate(longitude, latitude),
        foodTruck = foodTruck,
        open = open,
        storeClosure = closed,
        foodCategoryList = mutableListOf(),
        operationHoursList = mutableListOf(),
    )

    fun toReportOperationHoursEntity(foodSpotsHistory: FoodSpotsHistory) =
        operationHours.map {
            ReportOperationHours(
                foodSpotsHistory = foodSpotsHistory,
                dayOfWeek = it.dayOfWeek,
                openingHours = it.openingHours,
                closingHours = it.closingHours,
            )
        }

    fun toOperationHoursEntity(foodSpots: FoodSpots) =
        operationHours.map {
            FoodSpotsOperationHours(
                foodSpots = foodSpots,
                dayOfWeek = it.dayOfWeek,
                openingHours = it.openingHours,
                closingHours = it.closingHours,
            )
        }
}

data class OperationHoursRequest(
    @Schema(description = "요일", example = "MON")
    val dayOfWeek: DayOfWeek,
    @Schema(description = "오픈 시간", example = "01:00")
    @field:Pattern(regexp = OPERATION_HOURS_REGEX_STR, message = OPERATION_HOURS_REGEX_DESC)
    val openingHours: String,
    @Schema(description = "마감 시간", example = "23:59")
    @field:Pattern(regexp = OPERATION_HOURS_REGEX_STR, message = OPERATION_HOURS_REGEX_DESC)
    val closingHours: String,
)

data class FoodSpotsUpdateRequest(
    @field:Pattern(regexp = FOOD_SPOTS_NAME_REGEX_STR, message = FOOD_SPOTS_NAME_REGEX_DESC)
    @Schema(
        description =
            "상호명 : $FOOD_SPOTS_NAME_REGEX_DESC\t(허용된 특수문자 : 마침표 (.) 쉼표 (,) 작은따옴표 (') 가운데점 (·) 앰퍼샌드 (&) 하이픈 (-))",
        example = "명륜진사갈비 본사",
    )
    val name: String?,
    @field:Longitude
    @Schema(description = "경도", example = "127.12312219099")
    val longitude: Double?,
    @field:Latitude
    @Schema(description = "위도", example = "37.4940529587731")
    val latitude: Double?,
    @Schema(description = "영업 여부", example = "true")
    val open: Boolean?,
    @Schema(description = "폐업 여부", example = "false")
    val closed: Boolean?,
    @field:Size(min = 1, message = "음식 카테고리는 최소 1개 이상 선택해야 합니다.")
    @Schema(
        description = "음식 카테고리 ex) 변경된, 변경되지 않은 카테고리 모두 입력해주세요. 없을 경우, 생략해주세요",
        example = "[1, 2]",
    )
    val foodCategories: Set<Long>?,
    @field:Valid
    @Schema(
        description = "운영시간 리스트 ex) 변경된, 변경되지 않은 운영시간 모두 입력해주세요. 없을 경우, 생략해주세요",
    )
    val operationHours: List<OperationHoursRequest>?,
) {
    fun toFoodSpotsHistoryEntity(
        foodSpots: FoodSpots,
        user: User,
    ) = FoodSpotsHistory(
        name = name ?: foodSpots.name,
        foodSpots = foodSpots,
        user = user,
        point = if (longitude != null && latitude != null) createCoordinate(longitude, latitude) else foodSpots.point,
        foodTruck = foodSpots.foodTruck,
        open = open ?: foodSpots.open,
        storeClosure = closed ?: foodSpots.storeClosure,
        foodCategoryList = mutableListOf(),
        operationHoursList = mutableListOf(),
    )

    fun toOperationHoursEntity(foodSpots: FoodSpots) =
        operationHours
            ?.map {
                FoodSpotsOperationHours(
                    foodSpots = foodSpots,
                    dayOfWeek = it.dayOfWeek,
                    openingHours = it.openingHours,
                    closingHours = it.closingHours,
                )
            }?.toSet()

    fun toReportOperationHoursEntity(foodSpotsHistory: FoodSpotsHistory) =
        operationHours
            ?.map {
                ReportOperationHours(
                    foodSpotsHistory = foodSpotsHistory,
                    dayOfWeek = it.dayOfWeek,
                    openingHours = it.openingHours,
                    closingHours = it.closingHours,
                )
            }
}

data class ReportPhotoResponse(
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

data class ReportCategoryResponse(
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

data class ReportHistoryDetailResponse(
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
    @Schema(description = "이동여부(푸드 트럭, 푸드 카트 등 여부)", example = "false")
    val movableFoodSpots: Boolean,
    @Schema(description = "영업 여부", example = "true")
    val open: Boolean,
    @Schema(description = "폐업 여부", example = "false")
    val closed: Boolean,
    @Schema(description = "생성일시", example = "2021-08-01T00:00:00")
    val createdDateTime: LocalDateTime,
    @Schema(description = "리포트 사진 리스트")
    val reportPhotos: List<ReportPhotoResponse>,
    @Schema(description = "음식 카테고리 리스트")
    val categories: List<ReportCategoryResponse>,
    @Schema(description = "운영 시간 리스트")
    val operationHours: List<ReportOperationHoursResponse>,
) {
    constructor(
        foodSpotsHistory: FoodSpotsHistory,
        reportPhotos: List<ReportPhotoResponse>,
        categories: List<ReportCategoryResponse>,
        operationHours: List<ReportOperationHoursResponse>,
    ) : this(
        id = foodSpotsHistory.id,
        userId = foodSpotsHistory.user.id,
        foodSpotsId = foodSpotsHistory.foodSpots.id,
        name = foodSpotsHistory.name,
        longitude = foodSpotsHistory.point.x,
        latitude = foodSpotsHistory.point.y,
        movableFoodSpots = foodSpotsHistory.foodTruck,
        open = foodSpotsHistory.open,
        closed = foodSpotsHistory.storeClosure,
        createdDateTime = foodSpotsHistory.createdDateTime,
        reportPhotos = reportPhotos,
        categories = categories,
        operationHours = operationHours,
    )
}

data class ReportOperationHoursResponse(
    @Schema(description = "리포트 ID", example = "1")
    val historyId: Long,
    @Schema(description = "요일", example = "MON")
    val dayOfWeek: DayOfWeek,
    @Schema(description = "오픈 시간", example = "01:00")
    val openingHours: String,
    @Schema(description = "마감 시간", example = "23:59")
    val closingHours: String,
) {
    constructor(operationHours: ReportOperationHours) : this(
        historyId = operationHours.foodSpotsHistory.id,
        dayOfWeek = operationHours.dayOfWeek,
        openingHours = operationHours.openingHours,
        closingHours = operationHours.closingHours,
    )
}

data class FoodSpotsReviewResponse(
    @Schema(description = "리뷰 ID")
    val id: Long,
    @Schema(description = "음식점 ID")
    val foodSpotsId: Long,
    @Schema(description = "직성자 정보")
    val userInfo: ReviewerInfoResponse,
    @Schema(description = "리뷰 내용")
    val contents: String,
    @Schema(description = "별점")
    val rate: Int,
    @Schema(description = "사진 리스트")
    val photos: List<ReviewPhotoResponse>,
    @Schema(description = "리뷰 작성일")
    val createdAt: LocalDateTime,
) {
    companion object {
        fun of(
            review: FoodSpotsReview,
            userInfo: ReviewerInfoResponse,
            photoList: List<ReviewPhotoResponse>,
        ) = FoodSpotsReviewResponse(
            id = review.id,
            foodSpotsId = review.foodSpots.id,
            userInfo = userInfo,
            contents = review.contents,
            rate = review.rate,
            photos = photoList,
            createdAt = review.createdDateTime,
        )
    }
}
