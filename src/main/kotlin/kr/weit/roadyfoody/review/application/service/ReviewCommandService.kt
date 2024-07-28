package kr.weit.roadyfoody.review.application.service

import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsRepository
import kr.weit.roadyfoody.foodSpots.repository.getByFoodSpotsId
import kr.weit.roadyfoody.global.service.ImageService
import kr.weit.roadyfoody.review.application.dto.ReviewRequest
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
) {
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

    @Transactional
    fun deleteReview(
        user: User,
        reviewId: Long,
    ) {
        val review = reviewRepository.getReviewByReviewId(reviewId)
        if (review.user != user) {
            throw NotFoodSpotsReviewOwnerException("Not review owner")
        }
        deleteReviewPhoto(listOf(review))
        reviewRepository.delete(review)
    }

    private fun deleteReviewPhoto(reviews: List<FoodSpotsReview>) {
        reviewPhotoRepository
            .findByFoodSpotsReviewIn(reviews)
            .onEach { photo ->
                imageService.remove(photo.fileName)
            }.also { photoList -> reviewPhotoRepository.deleteAll(photoList) }
    }
}
