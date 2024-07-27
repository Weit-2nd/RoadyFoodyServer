package kr.weit.roadyfoody.review.application.service

import kr.weit.roadyfoody.common.dto.SliceResponse
import kr.weit.roadyfoody.global.service.ImageService
import kr.weit.roadyfoody.review.application.dto.FoodSpotsReviewResponse
import kr.weit.roadyfoody.review.application.dto.ReviewDetailResponse
import kr.weit.roadyfoody.review.application.dto.ReviewPhotoResponse
import kr.weit.roadyfoody.review.application.dto.UserReviewResponse
import kr.weit.roadyfoody.review.repository.FoodSpotsReviewPhotoRepository
import kr.weit.roadyfoody.review.repository.FoodSpotsReviewRepository
import kr.weit.roadyfoody.review.repository.ReviewSortType
import kr.weit.roadyfoody.review.repository.getByReview
import kr.weit.roadyfoody.user.dto.UserSimpleInfoResponse
import kr.weit.roadyfoody.user.repository.UserRepository
import kr.weit.roadyfoody.user.repository.getByUserId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReviewQueryService(
    private val userRepository: UserRepository,
    private val reviewRepository: FoodSpotsReviewRepository,
    private val reviewPhotoRepository: FoodSpotsReviewPhotoRepository,
    private val imageService: ImageService,
) {
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
                .map { UserReviewResponse(it) }
        return SliceResponse(response)
    }

    @Transactional(readOnly = true)
    fun getFoodSpotsReview(
        foodSpotsId: Long,
        size: Int,
        lastId: Long?,
        sortType: ReviewSortType,
    ): SliceResponse<FoodSpotsReviewResponse> {
        val response =
            reviewRepository.sliceByFoodSpots(foodSpotsId, size, lastId, sortType).map {
                val user = userRepository.getByUserId(it.user.id)
                val url =
                    user.profile.profileImageName?.let { fileName ->
                        imageService.getDownloadUrl(fileName)
                    }
                val photoResponses =
                    reviewPhotoRepository.getByReview(it).map { photo ->
                        ReviewPhotoResponse(photo.id, imageService.getDownloadUrl(photo.fileName))
                    }
                FoodSpotsReviewResponse(it, UserSimpleInfoResponse.from(user, url), photoResponses)
            }
        return SliceResponse(response)
    }

    @Transactional(readOnly = true)
    fun getReviewDetail(reviewId: Long): ReviewDetailResponse {
        val review = reviewRepository.getByReview(reviewId)
        val photoResponses =
            reviewPhotoRepository.getByReview(review).map {
                ReviewPhotoResponse(it.id, imageService.getDownloadUrl(it.fileName))
            }
        return ReviewDetailResponse(review, photoResponses)
    }
}
