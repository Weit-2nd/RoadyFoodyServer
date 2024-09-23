package kr.weit.roadyfoody.review.repository

import createTestFoodSpotsReview
import createTestReviewPhoto
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kr.weit.roadyfoody.foodSpots.domain.FoodSpots
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodSpots
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsRepository
import kr.weit.roadyfoody.review.domain.FoodSpotsReview
import kr.weit.roadyfoody.review.domain.FoodSpotsReviewPhoto
import kr.weit.roadyfoody.support.annotation.RepositoryTest
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.fixture.createTestUser
import kr.weit.roadyfoody.user.repository.UserRepository

@RepositoryTest
class FoodSpotsReviewPhotoRepositoryTest(
    private val userRepository: UserRepository,
    private val foodSpotsRepository: FoodSpotsRepository,
    private val reviewRepository: FoodSpotsReviewRepository,
    private val reviewPhotoRepository: FoodSpotsReviewPhotoRepository,
) : DescribeSpec(
        {
            lateinit var user: User
            lateinit var foodSpots: FoodSpots
            lateinit var otherFoodSpots: FoodSpots
            lateinit var review: FoodSpotsReview
            lateinit var otherReview: FoodSpotsReview
            lateinit var reviewPhoto: FoodSpotsReviewPhoto
            lateinit var reviewPhoto2: FoodSpotsReviewPhoto
            lateinit var otherReviewPhoto: FoodSpotsReviewPhoto
            beforeEach {
                user = userRepository.save(createTestUser(0L))
                foodSpots = foodSpotsRepository.save(createTestFoodSpots())
                otherFoodSpots = foodSpotsRepository.save(createTestFoodSpots())
                review = reviewRepository.save(createTestFoodSpotsReview(user, foodSpots))
                otherReview = reviewRepository.save(createTestFoodSpotsReview(user, otherFoodSpots))
                reviewPhoto = reviewPhotoRepository.save(createTestReviewPhoto(review))
                reviewPhoto2 = reviewPhotoRepository.save(createTestReviewPhoto(review))
                otherReviewPhoto = reviewPhotoRepository.save(createTestReviewPhoto(otherReview))
            }

            describe("findByUser 메소드는") {
                context("리뷰를 작성한 사용자를 받는 경우") {
                    it("해당 사용자의 리뷰 리스트를 반환한다.") {
                        val result =
                            reviewPhotoRepository.findByFoodSpotsReviewIn(listOf(review, otherReview))
                        result.map { it.foodSpotsReview }.size shouldBe 3
                    }
                }
            }

            describe("deleteAll 메소드는") {
                context("삭제할 리스트를 받는 경우") {
                    it("해당 리스트 모두 삭제한다.") {
                        val reviewPhotos =
                            reviewPhotoRepository.findByFoodSpotsReviewIn(listOf(review, otherReview))
                        reviewPhotoRepository.deleteAll(reviewPhotos)
                        reviewPhotoRepository.findByFoodSpotsReviewIn(
                            listOf(
                                review,
                                otherReview,
                            ),
                        ) shouldBe emptyList()
                    }
                }
            }

            describe("getByReview 메소드는") {
                context("리뷰를 받는 경우") {
                    it("해당 리뷰의 사진 리스트를 반환한다.") {
                        val result = reviewPhotoRepository.getByReview(review)
                        result shouldBe listOf(reviewPhoto, reviewPhoto2)
                    }
                }
            }

            describe("findByFoodSpotsReviewAndIdIn") {
                context("리뷰와 삭제할 사진 id 리스트를 받는 경우") {
                    it("해당 리뷰의 사진 리스트를 반환한다.") {
                        val reviewPhotos =
                            reviewPhotoRepository.findByFoodSpotsReviewAndIdIn(
                                review,
                                setOf(reviewPhoto.id, reviewPhoto2.id),
                            )
                        reviewPhotos shouldBe listOf(reviewPhoto, reviewPhoto2)
                    }
                }

                context("리뷰와 해당 리뷰에 없는 사진 id가 리스트에 존재하는 경우") {
                    it("해당 리뷰의 사진 리스트를 반환한다.") {
                        val reviewPhotos =
                            reviewPhotoRepository.findByFoodSpotsReviewAndIdIn(
                                review,
                                setOf(reviewPhoto.id, otherReviewPhoto.id),
                            )
                        reviewPhotos shouldBe listOf(reviewPhoto)
                    }
                }
                context("리뷰와 전부 리뷰에 없는 사진 id 리스트를 받는 경우") {
                    it("해당 리뷰의 사진 리스트를 반환한다.") {
                        val reviewPhotos =
                            reviewPhotoRepository.findByFoodSpotsReviewAndIdIn(
                                otherReview,
                                setOf(reviewPhoto.id),
                            )
                        reviewPhotos shouldBe emptyList()
                    }
                }
            }
        },
    )
