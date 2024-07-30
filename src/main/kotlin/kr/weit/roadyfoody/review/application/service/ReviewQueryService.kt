package kr.weit.roadyfoody.review.application.service

import kr.weit.roadyfoody.global.service.ImageService
import kr.weit.roadyfoody.review.application.dto.ReviewPhotoResponse
import kr.weit.roadyfoody.review.application.dto.ReviewResponse
import kr.weit.roadyfoody.review.repository.FoodSpotsReviewPhotoRepository
import kr.weit.roadyfoody.review.repository.FoodSpotsReviewRepository
import kr.weit.roadyfoody.review.repository.getByReview
import kr.weit.roadyfoody.review.repository.getReviewByReviewId
import kr.weit.roadyfoody.user.application.dto.ReviewerInfoResponse
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
    fun getReviewDetail(reviewId: Long): ReviewResponse {
        val review = reviewRepository.getReviewByReviewId(reviewId)
        val user = userRepository.getByUserId(review.user.id)
        val reviewInfo =
            ReviewerInfoResponse.of(
                user,
                user.profile.profileImageName?.let { imageService.getDownloadUrl(it) }
            )
        val photoResponses =
            reviewPhotoRepository.getByReview(review).map {
                ReviewPhotoResponse(it.id, imageService.getDownloadUrl(it.fileName))
            }
        return ReviewResponse.of(review, reviewInfo, photoResponses)
    }
}
