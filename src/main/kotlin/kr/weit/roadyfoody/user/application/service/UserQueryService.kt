package kr.weit.roadyfoody.user.application.service

import kr.weit.roadyfoody.common.dto.SliceResponse
import kr.weit.roadyfoody.foodSpots.application.service.FoodSpotsCommandService.Companion.FOOD_SPOTS_REPORT_LIMIT_COUNT
import kr.weit.roadyfoody.foodSpots.application.service.FoodSpotsCommandService.Companion.getFoodSpotsReportCountKey
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsHistoryRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsPhotoRepository
import kr.weit.roadyfoody.foodSpots.repository.ReportFoodCategoryRepository
import kr.weit.roadyfoody.global.service.ImageService
import kr.weit.roadyfoody.ranking.utils.TOTAL_RANKING_KEY
import kr.weit.roadyfoody.review.application.dto.ReviewPhotoResponse
import kr.weit.roadyfoody.review.repository.FoodSpotsReviewPhotoRepository
import kr.weit.roadyfoody.review.repository.FoodSpotsReviewRepository
import kr.weit.roadyfoody.review.repository.ReviewLikeRepository
import kr.weit.roadyfoody.user.application.dto.UserInfoResponse
import kr.weit.roadyfoody.user.application.dto.UserLikedReviewResponse
import kr.weit.roadyfoody.user.application.dto.UserReportCategoryResponse
import kr.weit.roadyfoody.user.application.dto.UserReportHistoriesResponse
import kr.weit.roadyfoody.user.application.dto.UserReportPhotoResponse
import kr.weit.roadyfoody.user.application.dto.UserReviewResponse
import kr.weit.roadyfoody.user.application.dto.UserStatisticsResponse
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.repository.UserRepository
import kr.weit.roadyfoody.user.repository.getByUserId
import org.springframework.cache.CacheManager
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
    private val reviewLikeRepository: ReviewLikeRepository,
    private val redisTemplate: RedisTemplate<String, String>,
    private val executor: ExecutorService,
    private val cacheManager: CacheManager,
) {
    fun getUserInfo(user: User): UserInfoResponse {
        val profileImageUrl = user.profile.profileImageName?.let { imageService.getDownloadUrl(it) }

        val reportCountKey = getFoodSpotsReportCountKey(user.id)
        val dailyReportCreationCount = redisTemplate.opsForValue().get(reportCountKey)?.toInt() ?: 0
        val restDailyReportCreationCount = FOOD_SPOTS_REPORT_LIMIT_COUNT - dailyReportCreationCount
        val ranking = getRanking(user)

        return UserInfoResponse.of(
            user.profile.nickname,
            profileImageUrl,
            user.badge.description,
            user.coin,
            restDailyReportCreationCount,
            ranking,
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
            foodSpotsHistoryRepository.findSliceByUser(user, size, lastId).map {
                val reportCategoryResponse =
                    reportFoodCategoryRepository.findByFoodSpotsHistoryId(it.id).map { category ->
                        UserReportCategoryResponse(category)
                    }
                val photosFutures =
                    foodSpotsPhotoRepository.findByHistoryId(it.id).map { photo ->
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
                        reviewPhotoRepository.findByFoodSpotsReview(it).map { photo ->
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
    fun getLikeReviews(
        userId: Long,
        size: Int,
        lastId: Long?,
    ): SliceResponse<UserLikedReviewResponse> {
        val user = userRepository.getByUserId(userId)
        val response =
            reviewLikeRepository.sliceLikeReviews(user, size, lastId).map {
                val photosFutures =
                    reviewPhotoRepository.findByFoodSpotsReview(it.review).map { photo ->
                        CompletableFuture
                            .supplyAsync({
                                ReviewPhotoResponse(
                                    photo.id,
                                    imageService.getDownloadUrl(photo.fileName),
                                )
                            }, executor)
                    }
                val reviewPhotos = photosFutures.map { it.join() }
                val profileUrl =
                    it.user.profile.profileImageName?.let { fileName ->
                        imageService.getDownloadUrl(fileName)
                    }
                UserLikedReviewResponse(it, reviewPhotos, profileUrl)
            }
        return SliceResponse(response)
    }

    @Transactional(readOnly = true)
    fun getUserStatistics(userId: Long): UserStatisticsResponse {
        val user = userRepository.getByUserId(userId)
        return UserStatisticsResponse(
            foodSpotsHistoryRepository.countByUser(user),
            reviewRepository.countByUser(user),
            reviewLikeRepository.countByUser(user),
        )
    }

    private fun getRanking(user: User): Long {
        val cache = cacheManager.getCache(TOTAL_RANKING_KEY)
        val cacheData = cache?.get(TOTAL_RANKING_KEY, List::class.java) as? List<String>

        return cacheData?.firstNotNullOfOrNull { entry ->
            val (ranking, nickname, _) = entry.split(":")
            if (nickname == user.profile.nickname) ranking.toLong() else null
        } ?: 0L
    }
}
