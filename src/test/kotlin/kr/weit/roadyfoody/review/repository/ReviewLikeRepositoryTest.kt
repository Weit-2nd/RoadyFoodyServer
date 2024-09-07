package kr.weit.roadyfoody.review.repository

import createTestFoodSpotsReview
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kr.weit.roadyfoody.foodSpots.domain.FoodSpots
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodSpots
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsRepository
import kr.weit.roadyfoody.review.domain.FoodSpotsReview
import kr.weit.roadyfoody.review.domain.ReviewLike
import kr.weit.roadyfoody.review.domain.ReviewLikeId
import kr.weit.roadyfoody.support.annotation.RepositoryTest
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.fixture.createTestUser
import kr.weit.roadyfoody.user.repository.UserRepository

@RepositoryTest
class ReviewLikeRepositoryTest(
    private val userRepository: UserRepository,
    private val foodSpotsRepository: FoodSpotsRepository,
    private val reviewRepository: FoodSpotsReviewRepository,
    private val reviewLikeRepository: ReviewLikeRepository,
) : DescribeSpec(
        {
            lateinit var user: User
            lateinit var otherUser: User
            lateinit var foodSpots: FoodSpots
            lateinit var review: FoodSpotsReview
            lateinit var reviewLike: ReviewLike
            lateinit var reviewLikeId: ReviewLikeId
            beforeEach {
                user = userRepository.save(createTestUser(0L))
                otherUser = userRepository.save(createTestUser(0L, "otherUser"))
                foodSpots = foodSpotsRepository.save(createTestFoodSpots())
                review = reviewRepository.save(createTestFoodSpotsReview(user, foodSpots))
                reviewLike = ReviewLike(review, user)
                reviewLikeId = ReviewLikeId(reviewLike.review, reviewLike.user)
                reviewLikeRepository.saveAll(
                    listOf(
                        reviewLike,
                    ),
                )
            }

            describe("existsById 메소드는") {
                context("존재하는 리뷰 좋아요 ID를 받는 경우") {
                    it("true를 반환한다.") {
                        reviewLikeRepository.existsById(reviewLikeId) shouldBe true
                    }
                }

                context("존재하지 않는 리뷰 좋아요 ID를 받는 경우") {
                    it("false를 반환한다.") {
                        reviewLikeRepository.existsById(reviewLikeId.copy(user = otherUser)) shouldBe false
                    }
                }
            }

            describe("deleteById 메소드는") {
                context("존재하는 리뷰 좋아요 ID를 받는 경우") {
                    it("리뷰 좋아요 이력이 삭제된다.") {
                        reviewLikeRepository.existsById(reviewLikeId) shouldBe true
                        reviewLikeRepository.deleteById(reviewLikeId)
                        reviewLikeRepository.existsById(reviewLikeId) shouldBe false
                    }
                }
            }

            describe("getByUser 메소드는") {
                context("유저를 받는 경우") {
                    it("유저가 좋아요한 리뷰를 반환한다.") {
                        reviewLikeRepository.getByUser(user) shouldBe listOf(review)
                    }
                }
            }

            describe("deleteByUser 메소드는") {
                context("유저를 받는 경우") {
                    it("유저가 좋아요한 리뷰를 삭제한다.") {
                        reviewLikeRepository.findByUser(user) shouldBe listOf(reviewLike)
                        reviewLikeRepository.deleteByUser(user)
                        reviewLikeRepository.findByUser(user) shouldBe emptyList()
                    }
                }
            }
        },
    )
