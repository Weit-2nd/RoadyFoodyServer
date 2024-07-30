package kr.weit.roadyfoody.foodSpots.application.service

import kr.weit.roadyfoody.common.dto.SliceResponse
import kr.weit.roadyfoody.foodSpots.application.dto.ReportCategoryResponse
import kr.weit.roadyfoody.foodSpots.application.dto.ReportHistoriesResponse
import kr.weit.roadyfoody.foodSpots.application.dto.ReportHistoryDetailResponse
import kr.weit.roadyfoody.foodSpots.application.dto.ReportOperationHoursResponse
import kr.weit.roadyfoody.foodSpots.application.dto.ReportPhotoResponse
import kr.weit.roadyfoody.foodSpots.domain.DayOfWeek
import kr.weit.roadyfoody.foodSpots.domain.FoodSpots
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsHistoryRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsPhotoRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsRepository
import kr.weit.roadyfoody.foodSpots.repository.ReportFoodCategoryRepository
import kr.weit.roadyfoody.foodSpots.repository.ReportOperationHoursRepository
import kr.weit.roadyfoody.foodSpots.repository.getByHistoryId
import kr.weit.roadyfoody.foodSpots.repository.getHistoriesByUser
import kr.weit.roadyfoody.global.service.ImageService
import kr.weit.roadyfoody.search.foodSpots.dto.FoodSpotsSearchCondition
import kr.weit.roadyfoody.search.foodSpots.dto.FoodSpotsSearchResponse
import kr.weit.roadyfoody.search.foodSpots.dto.FoodSpotsSearchResponses
import kr.weit.roadyfoody.search.foodSpots.dto.OperationStatus
import kr.weit.roadyfoody.user.repository.UserRepository
import kr.weit.roadyfoody.user.repository.getByUserId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField

@Service
class FoodSpotsQueryService(
    private val userRepository: UserRepository,
    private val foodSpotsHistoryRepository: FoodSpotsHistoryRepository,
    private val foodSpotsPhotoRepository: FoodSpotsPhotoRepository,
    private val reportFoodCategoryRepository: ReportFoodCategoryRepository,
    private val imageService: ImageService,
    private val foodSpotsRepository: FoodSpotsRepository,
    private val reportOperationHoursRepository: ReportOperationHoursRepository,
) {
    @Transactional(readOnly = true)
    fun searchFoodSpots(foodSpotsSearchQuery: FoodSpotsSearchCondition): FoodSpotsSearchResponses {
        val result: List<FoodSpots> =
            foodSpotsRepository.findFoodSpotsByPointWithinRadius(
                foodSpotsSearchQuery.centerLongitude,
                foodSpotsSearchQuery.centerLatitude,
                foodSpotsSearchQuery.radius,
                foodSpotsSearchQuery.name,
                foodSpotsSearchQuery.categoryIds ?: emptyList(),
            )

        val foodSpotsSearchResponses =
            result.map { foodSpots ->
                val openValue: OperationStatus = determineOpenStatus(foodSpots)
                FoodSpotsSearchResponse(
                    id = foodSpots.id,
                    name = foodSpots.name,
                    longitude = foodSpots.point.x,
                    latitude = foodSpots.point.y,
                    open = openValue,
                    foodCategories = foodSpots.foodCategoryList.map { it.foodCategory.name },
                    createdDateTime = foodSpots.createdDateTime,
                )
            }

        return FoodSpotsSearchResponses(foodSpotsSearchResponses)
    }

    @Transactional(readOnly = true)
    fun getReportHistory(historyId: Long): ReportHistoryDetailResponse {
        val foodSpotsHistory = foodSpotsHistoryRepository.getByHistoryId(historyId)
        val reportPhotoResponse =
            foodSpotsPhotoRepository.getByHistoryId(historyId).map { photo ->
                ReportPhotoResponse(photo, imageService.getDownloadUrl(photo.fileName))
            }
        val reportCategoryResponse =
            reportFoodCategoryRepository.getByHistoryId(historyId).map { category ->
                ReportCategoryResponse(category)
            }
        val reportOperationHoursResponse =
            reportOperationHoursRepository
                .getByHistoryId(foodSpotsHistory.foodSpots.id)
                .map { operationHours ->
                    ReportOperationHoursResponse(operationHours)
                }
        return ReportHistoryDetailResponse(
            foodSpotsHistory,
            reportPhotoResponse,
            reportCategoryResponse,
            reportOperationHoursResponse,
        )
    }

    private fun determineOpenStatus(foodSpot: FoodSpots): OperationStatus {
        val today = LocalDate.now()
        val dayOfWeekValue = today.get(ChronoField.DAY_OF_WEEK) - 1
        val dayOfWeek = DayOfWeek.of(dayOfWeekValue)

        val now = LocalTime.now()
        val format = DateTimeFormatter.ofPattern("HH:mm")

        return if (foodSpot.open) {
            foodSpot.operationHoursList
                .firstOrNull { it.dayOfWeek == dayOfWeek }
                ?.let {
                    if (now.isAfter(LocalTime.parse(it.openingHours, format)) &&
                        now.isBefore(LocalTime.parse(it.closingHours, format))
                    ) {
                        OperationStatus.OPEN
                    } else {
                        OperationStatus.CLOSED
                    }
                } ?: OperationStatus.CLOSED
        } else {
            OperationStatus.TEMPORARILY_CLOSED
        }
    }
}
