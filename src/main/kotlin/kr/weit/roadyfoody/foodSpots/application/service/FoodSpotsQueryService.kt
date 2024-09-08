package kr.weit.roadyfoody.foodSpots.application.service

import kr.weit.roadyfoody.badge.domain.Badge
import kr.weit.roadyfoody.common.dto.SliceResponse
import kr.weit.roadyfoody.foodSpots.application.dto.FoodSpotsDetailResponse
import kr.weit.roadyfoody.foodSpots.application.dto.FoodSpotsOperationHoursResponse
import kr.weit.roadyfoody.foodSpots.application.dto.FoodSpotsReviewResponse
import kr.weit.roadyfoody.foodSpots.application.dto.RatingCountResponse
import kr.weit.roadyfoody.foodSpots.application.dto.ReportCategoryResponse
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
import kr.weit.roadyfoody.foodSpots.repository.getByFoodSpots
import kr.weit.roadyfoody.foodSpots.repository.getByFoodSpotsId
import kr.weit.roadyfoody.foodSpots.repository.getByHistoryId
import kr.weit.roadyfoody.global.service.ImageService
import kr.weit.roadyfoody.review.application.dto.ReviewPhotoResponse
import kr.weit.roadyfoody.review.repository.FoodSpotsReviewPhotoRepository
import kr.weit.roadyfoody.review.repository.FoodSpotsReviewRepository
import kr.weit.roadyfoody.review.repository.ReviewSortType
import kr.weit.roadyfoody.review.repository.getByReview
import kr.weit.roadyfoody.search.foodSpots.dto.FoodSpotsSearchCondition
import kr.weit.roadyfoody.search.foodSpots.dto.FoodSpotsSearchResponse
import kr.weit.roadyfoody.search.foodSpots.dto.FoodSpotsSearchResponses
import kr.weit.roadyfoody.search.foodSpots.dto.OperationStatus
import kr.weit.roadyfoody.user.application.dto.ReviewerInfoResponse
import kr.weit.roadyfoody.user.repository.UserRepository
import kr.weit.roadyfoody.user.repository.getByUserId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService

@Service
class FoodSpotsQueryService(
    private val foodSpotsHistoryRepository: FoodSpotsHistoryRepository,
    private val foodSpotsPhotoRepository: FoodSpotsPhotoRepository,
    private val reportFoodCategoryRepository: ReportFoodCategoryRepository,
    private val imageService: ImageService,
    private val foodSpotsRepository: FoodSpotsRepository,
    private val reportOperationHoursRepository: ReportOperationHoursRepository,
    private val reviewRepository: FoodSpotsReviewRepository,
    private val userRepository: UserRepository,
    private val reviewPhotoRepository: FoodSpotsReviewPhotoRepository,
    private val executor: ExecutorService,
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
                val foodSpotsPhoto = foodSpotsPhotoRepository.findOneByFoodSpots(foodSpots.id)
                val (averageRating, reviewCount) = reviewRepository.getReviewAggregatedInfo(foodSpots)

                FoodSpotsSearchResponse(
                    id = foodSpots.id,
                    name = foodSpots.name,
                    longitude = foodSpots.point.x,
                    latitude = foodSpots.point.y,
                    open = openValue,
                    operationHours = getTodayOperationHoursResponse(foodSpots),
                    foodCategories = foodSpots.foodCategoryList.map { it.foodCategory.name },
                    foodTruck = foodSpots.foodTruck,
                    imageUrl = foodSpotsPhoto?.let { imageService.getDownloadUrl(it.fileName) },
                    averageRating = averageRating,
                    reviewCount = reviewCount,
                    createdDateTime = foodSpots.createdDateTime,
                )
            }

        return FoodSpotsSearchResponses(foodSpotsSearchResponses)
    }

    @Transactional(readOnly = true)
    fun getReportHistory(historyId: Long): ReportHistoryDetailResponse {
        val foodSpotsHistory = foodSpotsHistoryRepository.getByHistoryId(historyId)
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

        val photosFutures =
            foodSpotsPhotoRepository.getByHistoryId(historyId).map { photo ->
                CompletableFuture
                    .supplyAsync({
                        ReportPhotoResponse(photo.id, imageService.getDownloadUrl(photo.fileName))
                    }, executor)
            }
        val reportPhotoResponse = photosFutures.map { it.join() }
        return ReportHistoryDetailResponse(
            foodSpotsHistory,
            reportPhotoResponse,
            reportCategoryResponse,
            reportOperationHoursResponse,
        )
    }

    @Transactional(readOnly = true)
    fun getFoodSpotsReview(
        foodSpotsId: Long,
        size: Int,
        lastId: Long?,
        sortType: ReviewSortType,
        badge: Badge? = null,
    ): SliceResponse<FoodSpotsReviewResponse> {
        val response =
            reviewRepository.sliceByFoodSpots(foodSpotsId, size, lastId, sortType, badge).map {
                val user = userRepository.getByUserId(it.user.id)
                val url =
                    user.profile.profileImageName?.let { fileName ->
                        imageService.getDownloadUrl(fileName)
                    }
                val photosFutures =
                    reviewPhotoRepository.getByReview(it).map { photo ->
                        CompletableFuture
                            .supplyAsync({
                                ReviewPhotoResponse(
                                    photo.id,
                                    imageService.getDownloadUrl(photo.fileName),
                                )
                            }, executor)
                    }
                val photoResponseList = photosFutures.map { it.join() }
                FoodSpotsReviewResponse.of(
                    it,
                    ReviewerInfoResponse.of(user, url),
                    photoResponseList,
                )
            }
        return SliceResponse(response)
    }

    @Transactional(readOnly = true)
    fun getFoodSpotsDetail(foodSpotsId: Long): FoodSpotsDetailResponse =
        foodSpotsRepository.getByFoodSpotsId(foodSpotsId).let { foodSpots ->
            val reviewAggregatedInfoResponse = reviewRepository.getReviewAggregatedInfo(foodSpots)
            val ratingCountResponse = getRatingCount(reviewRepository.getRatingCount(foodSpotsId))
            val photosFutures =
                foodSpotsHistoryRepository.getByFoodSpots(foodSpots).let {
                    foodSpotsPhotoRepository.findByHistoryIn(it).map { photo ->
                        CompletableFuture
                            .supplyAsync({
                                ReportPhotoResponse(
                                    photo.id,
                                    imageService.getDownloadUrl(photo.fileName),
                                )
                            }, executor)
                    }
                }
            val foodSpotsPhotos = photosFutures.map { it.join() }
            FoodSpotsDetailResponse(
                foodSpots,
                determineOpenStatus(foodSpots),
                foodSpotsPhotos,
                reviewAggregatedInfoResponse,
                ratingCountResponse,
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

    private fun getTodayOperationHoursResponse(foodSpot: FoodSpots): FoodSpotsOperationHoursResponse {
        val today = LocalDate.now()
        val dayOfWeekValue = today.get(ChronoField.DAY_OF_WEEK) - 1
        val dayOfWeek = DayOfWeek.of(dayOfWeekValue)

        return foodSpot.operationHoursList
            .firstOrNull { it.dayOfWeek == dayOfWeek }
            ?.let { operationHours ->
                return FoodSpotsOperationHoursResponse(
                    operationHours,
                )
            } ?: FoodSpotsOperationHoursResponse(
            foodSpotsId = foodSpot.id,
            dayOfWeek = dayOfWeek,
            openingHours = "00:00",
            closingHours = "00:00",
        )
    }

    private fun getRatingCount(ratingCountResponses: MutableList<RatingCountResponse>): List<RatingCountResponse> {
        var index = 0
        if (ratingCountResponses.isEmpty()) {
            for (i in 10 downTo 2 step 2) {
                ratingCountResponses.add(RatingCountResponse(i, 0))
            }
        } else {
            for (i in 10 downTo 2 step 2) {
                if (index < ratingCountResponses.size) {
                    val rating = ratingCountResponses[index].rating
                    if (rating == i) {
                        index++
                        continue
                    }
                }
                ratingCountResponses.add(index, RatingCountResponse(i, 0))
                index++
            }
        }
        return ratingCountResponses
    }
}
