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
            lateinit var notLikeUser: User
            lateinit var foodSpots: FoodSpots
            lateinit var review: FoodSpotsReview
            lateinit var otherReview: FoodSpotsReview
            lateinit var reviewLike: ReviewLike
            lateinit var otherReviewLike: ReviewLike
            lateinit var reviewLikeId: ReviewLikeId
            beforeEach {
                user = userRepository.save(createTestUser(0L))
                otherUser = userRepository.save(createTestUser(0L, "otherUser"))
                notLikeUser = userRepository.save(createTestUser(0L, "notLikeUser"))
                foodSpots = foodSpotsRepository.save(createTestFoodSpots())
                review = reviewRepository.save(createTestFoodSpotsReview(user, foodSpots))
                otherReview = reviewRepository.save(createTestFoodSpotsReview(otherUser, foodSpots))
                reviewLike = reviewLikeRepository.save(ReviewLike(review, user))
                otherReviewLike = reviewLikeRepository.save(ReviewLike(otherReview, user))
                reviewLikeId = ReviewLikeId(reviewLike.review, reviewLike.user)
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

            describe("getLikedReviewByUser 메소드는") {
                context("유저를 받는 경우") {
                    it("유저가 좋아요한 리뷰를 반환한다.") {
                        reviewLikeRepository.getLikedReviewByUser(user) shouldBe
                            listOf(
                                review,
                                otherReview,
                            )
                    }
                }
            }

            describe("deleteByUser 메소드는") {
                context("유저를 받는 경우") {
                    it("유저가 좋아요한 리뷰를 삭제한다.") {
                        reviewLikeRepository.findByUser(user) shouldBe
                            listOf(
                                reviewLike,
                                otherReviewLike,
                            )
                        reviewLikeRepository.deleteByUser(user)
                        reviewLikeRepository.findByUser(user) shouldBe emptyList()
                    }
                }
            }

            describe("sliceLikeReviews 메소드는") {
                context("유저와 사이즈를 받는 경우") {
                    it("유저가 좋아요한 리뷰를 slice하여 반환한다.") {
                        reviewLikeRepository
                            .sliceLikeReviews(
                                user,
                                1,
                                null,
                            ).content shouldBe listOf(otherReviewLike)
                    }
                }

                context("유저와 사이즈, 마지막 시간을 받는 경우") {
                    it("유저가 좋아요한 리뷰를 slice하여 반환한다.") {
                        reviewLikeRepository
                            .sliceLikeReviews(
                                user,
                                10,
                                otherReviewLike.createdDateTime,
                            ).content shouldBe listOf(reviewLike)
                    }
                }
            }

            describe("countByUser 메소드는") {
                context("유저를 받는 경우") {
                    it("유저가 좋아요한 리뷰의 개수를 반환한다.") {
                        reviewLikeRepository.countByUser(user) shouldBe 2
                    }
                }

                context("좋아요를 누르지 않은 유저를 받는 경우") {
                    it("0을 반환한다.") {
                        reviewLikeRepository.countByUser(notLikeUser) shouldBe 0
                    }
                }
            }
        },
    )
