package kr.weit.roadyfoody.review.repository

import createTestFoodSpotsReview
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kr.weit.roadyfoody.foodSpots.domain.FoodSpots
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodSpots
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsRepository
import kr.weit.roadyfoody.review.exception.FoodSpotsReviewNotFoundException
import kr.weit.roadyfoody.support.annotation.RepositoryTest
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.fixture.createTestUser
import kr.weit.roadyfoody.user.repository.UserRepository

@RepositoryTest
class FoodSpotsReviewRepositoryTest(
    private val userRepository: UserRepository,
    private val foodSpotsRepository: FoodSpotsRepository,
    private val reviewRepository: FoodSpotsReviewRepository,
) : DescribeSpec(
        {
            lateinit var user: User
            lateinit var otherUser: User
            lateinit var foodSpots: FoodSpots
            lateinit var otherFoodSpots: FoodSpots
            beforeEach {
                user = userRepository.save(createTestUser(0L))
                otherUser = userRepository.save(createTestUser(0L, "otherUser"))
                foodSpots = foodSpotsRepository.save(createTestFoodSpots())
                otherFoodSpots = foodSpotsRepository.save(createTestFoodSpots())
                reviewRepository.saveAll(
                    listOf(
                        createTestFoodSpotsReview(user, foodSpots),
                        createTestFoodSpotsReview(user, otherFoodSpots),
                        createTestFoodSpotsReview(otherUser, foodSpots),
                    ),
                )
            }

            describe("findByUser 메소드는") {
                context("리뷰를 작성한 사용자를 받는 경우") {
                    it("해당 사용자의 리뷰 리스트를 반환한다.") {
                        reviewRepository.findByUser(user).size shouldBe 2
                    }
                }
            }

            describe("deleteAll 메소드는") {
                context("삭제할 리스트를 받는 경우") {
                    it("해당 리스트 모두 삭제한다.") {
                        val reviews = reviewRepository.findByUser(user)
                        reviewRepository.deleteAll(reviews)
                        reviewRepository.findAll().size shouldBe 1
                    }
                }
            }
            describe("getReviewByReviewId 메소드는") {
                context("리뷰 ID를 받는 경우") {
                    it("해당 ID의 리뷰를 반환한다.") {
                        val review = reviewRepository.findByUser(user).first()
                        reviewRepository.getReviewByReviewId(review.id) shouldBe review
                    }
                }

                context("존재하지 않는 리뷰 ID 를 받는 경우") {
                    it("에러가 발생한다") {
                        shouldThrow<FoodSpotsReviewNotFoundException> {
                            reviewRepository.getReviewByReviewId(0L)
                        }
                    }
                }
            }
        },
    )
