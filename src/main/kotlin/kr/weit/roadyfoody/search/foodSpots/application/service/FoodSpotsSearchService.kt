package kr.weit.roadyfoody.search.foodSpots.application.service

import kr.weit.roadyfoody.common.exception.ErrorCode
import kr.weit.roadyfoody.common.exception.RoadyFoodyBadRequestException
import kr.weit.roadyfoody.foodSpots.domain.DayOfWeek
import kr.weit.roadyfoody.foodSpots.domain.FoodSpots
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsRepository
import kr.weit.roadyfoody.search.foodSpots.dto.FoodSpotsSearchCondition
import kr.weit.roadyfoody.search.foodSpots.dto.FoodSpotsSearchResponse
import kr.weit.roadyfoody.search.foodSpots.dto.FoodSpotsSearchResponses
import kr.weit.roadyfoody.search.foodSpots.dto.OperationStatus
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import kotlin.math.pow

@Service
class FoodSpotsSearchService(
    private val foodSpotsRepository: FoodSpotsRepository,
    private val userRepository: UserRepository,
) {
    @Transactional
    fun searchFoodSpots(
        user: User,
        foodSpotsSearchQuery: FoodSpotsSearchCondition,
    ): FoodSpotsSearchResponses {
        val baseRadius = 500
        val searchRadius = foodSpotsSearchQuery.radius

        if (searchRadius > baseRadius) {
            handleRadiusExpansion(searchRadius, baseRadius, user)
        }

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

    private fun handleRadiusExpansion(
        searchRadius: Int,
        baseRadius: Int,
        user: User,
    ) {
        val additionalRadius = (searchRadius - baseRadius) / baseRadius
        val coinRequired = (2.0.pow(additionalRadius.toDouble()) * 100).toInt()
        val lockKey = generateRedisUserLockKey(user.id)

        if (user.coin >= coinRequired) {
            user.decreaseCoin(coinRequired)
            userRepository.save(
                User(
                    user.id,
                    user.socialId,
                    user.profile,
                    user.coin,
                ),
            )
        } else {
            throw RoadyFoodyBadRequestException(ErrorCode.COIN_NOT_ENOUGH)
        }
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

    private fun generateRedisUserLockKey(id: Long): String {
        val baseKey = "user:$id:coin-lock"
        return baseKey
    }
}
