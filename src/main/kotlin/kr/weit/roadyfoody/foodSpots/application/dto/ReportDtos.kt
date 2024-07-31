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
import kr.weit.roadyfoody.foodSpots.domain.ReportOperationHours
import kr.weit.roadyfoody.foodSpots.utils.FOOD_SPOTS_NAME_REGEX_DESC
import kr.weit.roadyfoody.foodSpots.utils.FOOD_SPOTS_NAME_REGEX_STR
import kr.weit.roadyfoody.foodSpots.utils.OPERATION_HOURS_REGEX_DESC
import kr.weit.roadyfoody.foodSpots.utils.OPERATION_HOURS_REGEX_STR
import kr.weit.roadyfoody.foodSpots.validator.Latitude
import kr.weit.roadyfoody.foodSpots.validator.Longitude
import kr.weit.roadyfoody.global.utils.CoordinateUtils.Companion.createCoordinate
import kr.weit.roadyfoody.user.domain.User

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
    @Schema(description = "푸드트럭여부(이동여부)", example = "false")
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
