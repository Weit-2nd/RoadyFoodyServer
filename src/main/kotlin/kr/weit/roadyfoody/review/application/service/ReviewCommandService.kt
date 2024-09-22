package kr.weit.roadyfoody.review.application.service

import USER_ENTITY_LOCK_KEY
import kr.weit.roadyfoody.badge.service.BadgeCommandService
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsRepository
import kr.weit.roadyfoody.foodSpots.repository.getByFoodSpotsId
import kr.weit.roadyfoody.global.annotation.DistributedLock
import kr.weit.roadyfoody.global.service.ImageService
import kr.weit.roadyfoody.review.application.dto.ReviewRequest
import kr.weit.roadyfoody.review.application.dto.ReviewUpdateRequest
import kr.weit.roadyfoody.review.domain.FoodSpotsReview
import kr.weit.roadyfoody.review.domain.FoodSpotsReviewPhoto
import kr.weit.roadyfoody.review.exception.NotFoodSpotsReviewOwnerException
import kr.weit.roadyfoody.review.repository.FoodSpotsReviewPhotoRepository
import kr.weit.roadyfoody.review.repository.FoodSpotsReviewRepository
import kr.weit.roadyfoody.review.repository.getReviewByReviewId
import kr.weit.roadyfoody.user.domain.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService

@Service
class ReviewCommandService(
    private val reviewRepository: FoodSpotsReviewRepository,
    private val reviewPhotoRepository: FoodSpotsReviewPhotoRepository,
    private val foodSpotsRepository: FoodSpotsRepository,
    private val imageService: ImageService,
    private val executor: ExecutorService,
    private val badgeCommandService: BadgeCommandService,
) {
    @DistributedLock(lockName = USER_ENTITY_LOCK_KEY, identifier = "user")
    @Transactional
    fun createReview(
        user: User,
        reviewRequest: ReviewRequest,
        photos: List<MultipartFile>?,
    ) {
        val foodSpot = foodSpotsRepository.getByFoodSpotsId(reviewRequest.foodSpotId)
        val review = reviewRepository.save(reviewRequest.toEntity(user, foodSpot))
        photos?.let {
            val generatorPhotoNameMap = photos.associateBy { imageService.generateImageName(it) }
            generatorPhotoNameMap
                .map {
                    FoodSpotsReviewPhoto(review, it.key)
                }.also { reviewPhotoRepository.saveAll(it) }
            generatorPhotoNameMap
                .map {
                    CompletableFuture.supplyAsync({
                        imageService.upload(
                            it.key,
                            it.value,
                        )
                    }, executor)
                }.forEach { it.join() }
        }
        badgeCommandService.tryChangeBadgeAndIfPromotedGiveBonus(user.id)
    }

    @Transactional
    fun deleteWithdrewUserReview(user: User) {
        reviewRepository.findByUser(user).also {
            if (it.isNotEmpty()) {
                deleteReviewPhoto(it)
                reviewRepository.deleteAll(it)
            }
        }
    }

    @DistributedLock(lockName = USER_ENTITY_LOCK_KEY, identifier = "user")
    @Transactional
    fun deleteReview(
        user: User,
        reviewId: Long,
    ) {
        val review = reviewRepository.getReviewByReviewId(reviewId)
        if (review.user.id != user.id) {
            throw NotFoodSpotsReviewOwnerException("해당 리뷰의 소유자가 아닙니다.")
        }
        deleteReviewPhoto(listOf(review))
        reviewRepository.delete(review)
        badgeCommandService.tryChangeBadgeAndIfPromotedGiveBonus(user.id)
    }

    fun updateReview(
        user: User,
        reviewId: Long,
        reviewRequest: ReviewUpdateRequest?,
        photos: List<MultipartFile>?,
    ) {
        val review = reviewRepository.getReviewByReviewId(reviewId)
        if (review.user.id != user.id) {
            throw NotFoodSpotsReviewOwnerException("해당 리뷰의 소유자가 아닙니다.")
        }

        reviewRequest?.let {
            var change = false
            it.contents?.let { contents ->
                review.contents = contents
                change = true
            }
            it.rating?.let { rating ->
                review.rate = rating
                change = true
            }

            if (change) reviewRepository.save(review)
        }
        val deletePhotos =
            reviewRequest?.deletePhotoIds?.let {
                reviewPhotoRepository.findByFoodSpotsReviewAndIdIn(
                    review,
                    reviewRequest.deletePhotoIds,
                )
            }
        val deleteFileName = deletePhotos?.map { it.fileName } ?: emptyList()
        deletePhotos?.let { reviewPhotoRepository.deleteAll(it) }
        val generatorPhotoNameMap =
            photos?.associateBy { imageService.generateImageName(it) } ?: emptyMap()
        validatePhotoSize(review, generatorPhotoNameMap.size)
        generatorPhotoNameMap
            .map { FoodSpotsReviewPhoto(review, it.key) }
            .also { reviewPhotoRepository.saveAll(it) }
        (
            generatorPhotoNameMap
                .map {
                    CompletableFuture.supplyAsync({
                        imageService.upload(it.key, it.value)
                    }, executor)
                } +
                deleteFileName
                    .map {
                        CompletableFuture.supplyAsync({
                            imageService.remove(it)
                        }, executor)
                    }
        ).forEach { it.join() }
    }

    private fun deleteReviewPhoto(reviews: List<FoodSpotsReview>) {
        reviewPhotoRepository
            .findByFoodSpotsReviewIn(reviews)
            .onEach { photo ->
                imageService.remove(photo.fileName)
            }.also { photoList -> reviewPhotoRepository.deleteAll(photoList) }
    }

    private fun validatePhotoSize(
        review: FoodSpotsReview,
        photosCount: Int,
    ) {
        reviewPhotoRepository.findByFoodSpotsReview(review).size.also { currentPhotoSize ->
            if (currentPhotoSize + photosCount > 3) {
                throw IllegalArgumentException("이미지는 최대 3개까지 업로드할 수 있습니다.")
            }
        }
    }
}
