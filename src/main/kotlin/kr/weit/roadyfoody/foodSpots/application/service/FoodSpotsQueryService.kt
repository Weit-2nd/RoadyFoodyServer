package kr.weit.roadyfoody.foodSpots.application.service

import kr.weit.roadyfoody.foodSpots.domain.DayOfWeek
import kr.weit.roadyfoody.foodSpots.domain.FoodSpots
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsRepository
import kr.weit.roadyfoody.search.foodSpots.dto.FoodSpotsSearchCondition
import kr.weit.roadyfoody.search.foodSpots.dto.FoodSpotsSearchResponse
import kr.weit.roadyfoody.search.foodSpots.dto.FoodSpotsSearchResponses
import kr.weit.roadyfoody.search.foodSpots.dto.OperationStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField

@Service
class FoodSpotsQueryService(
    private val foodSpotsRepository: FoodSpotsRepository,
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
