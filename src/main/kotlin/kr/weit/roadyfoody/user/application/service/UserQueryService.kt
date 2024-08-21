package kr.weit.roadyfoody.user.application.service

import kr.weit.roadyfoody.common.dto.SliceResponse
import kr.weit.roadyfoody.foodSpots.application.service.FoodSpotsCommandService.Companion.FOOD_SPOTS_REPORT_LIMIT_COUNT
import kr.weit.roadyfoody.foodSpots.application.service.FoodSpotsCommandService.Companion.getFoodSpotsReportCountKey
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsHistoryRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsPhotoRepository
import kr.weit.roadyfoody.foodSpots.repository.ReportFoodCategoryRepository
import kr.weit.roadyfoody.foodSpots.repository.getByHistoryId
import kr.weit.roadyfoody.foodSpots.repository.getHistoriesByUser
import kr.weit.roadyfoody.global.service.ImageService
import kr.weit.roadyfoody.review.application.dto.ReviewPhotoResponse
import kr.weit.roadyfoody.review.repository.FoodSpotsReviewPhotoRepository
import kr.weit.roadyfoody.review.repository.FoodSpotsReviewRepository
import kr.weit.roadyfoody.review.repository.getByReview
import kr.weit.roadyfoody.user.application.dto.UserCoinBalance
import kr.weit.roadyfoody.user.application.dto.UserInfoResponse
import kr.weit.roadyfoody.user.application.dto.UserReportCategoryResponse
import kr.weit.roadyfoody.user.application.dto.UserReportHistoriesResponse
import kr.weit.roadyfoody.user.application.dto.UserReportPhotoResponse
import kr.weit.roadyfoody.user.application.dto.UserReviewResponse
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.repository.UserRepository
import kr.weit.roadyfoody.user.repository.getByUserId
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService

@Service
class UserQueryService(
    private val userRepository: UserRepository,
    private val imageService: ImageService,
    private val foodSpotsHistoryRepository: FoodSpotsHistoryRepository,
    private val foodSpotsPhotoRepository: FoodSpotsPhotoRepository,
    private val reportFoodCategoryRepository: ReportFoodCategoryRepository,
    private val reviewRepository: FoodSpotsReviewRepository,
    private val reviewPhotoRepository: FoodSpotsReviewPhotoRepository,
    private val redisTemplate: RedisTemplate<String, String>,
    private val executor: ExecutorService,
) {
    fun getUserInfo(user: User): UserInfoResponse {
        val user = userRepository.getByUserId(user.id)
        val profileImageUrl = user.profile.profileImageName?.let { imageService.getDownloadUrl(it) }

        val reportCountKey = getFoodSpotsReportCountKey(user.id)
        val dailyReportCreationCount = redisTemplate.opsForValue().get(reportCountKey)?.toInt() ?: 0
        val restDailyReportCreationCount = FOOD_SPOTS_REPORT_LIMIT_COUNT - dailyReportCreationCount

        return UserInfoResponse.of(
            user.profile.nickname,
            profileImageUrl,
            user.coin,
            restDailyReportCreationCount,
        )
    }

    @Transactional(readOnly = true)
    fun getReportHistories(
        userId: Long,
        size: Int,
        lastId: Long?,
    ): SliceResponse<UserReportHistoriesResponse> {
        val user = userRepository.getByUserId(userId)
        val reportResponse =
            foodSpotsHistoryRepository.getHistoriesByUser(user, size, lastId).map {
                val reportCategoryResponse =
                    reportFoodCategoryRepository.getByHistoryId(it.id).map { category ->
                        UserReportCategoryResponse(category)
                    }
                val photosFutures =
                    foodSpotsPhotoRepository.getByHistoryId(it.id).map { photo ->
                        CompletableFuture
                            .supplyAsync({
                                UserReportPhotoResponse(
                                    photo,
                                    imageService.getDownloadUrl(photo.fileName),
                                )
                            }, executor)
                    }
                val reportPhotoResponse = photosFutures.map { it.join() }
                UserReportHistoriesResponse(it, reportPhotoResponse, reportCategoryResponse)
            }
        return SliceResponse(reportResponse)
    }

    @Transactional(readOnly = true)
    fun getUserReviews(
        userId: Long,
        size: Int,
        lastId: Long?,
    ): SliceResponse<UserReviewResponse> {
        val user = userRepository.getByUserId(userId)
        val response =
            reviewRepository
                .sliceByUser(user, size, lastId)
                .map {
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
                    val reviewPhotos = photosFutures.map { it.join() }
                    UserReviewResponse(it, reviewPhotos)
                }
        return SliceResponse(response)
    }

    @Transactional(readOnly = true)
    fun getCoinBalance(user: User): UserCoinBalance {
        val userFromDb = userRepository.getByUserId(user.id)
        return UserCoinBalance(userFromDb.coin)
    }
}
