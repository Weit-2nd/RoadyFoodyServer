package kr.weit.roadyfoody.review.repository

import createTestFoodSpotsReview
import createTestReviewFlag
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.ints.shouldBeZero
import io.kotest.matchers.shouldBe
import kr.weit.roadyfoody.foodSpots.domain.FoodSpots
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodSpots
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsRepository
import kr.weit.roadyfoody.review.domain.FoodSpotsReview
import kr.weit.roadyfoody.review.domain.FoodSpotsReviewFlag
import kr.weit.roadyfoody.support.annotation.RepositoryTest
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.fixture.createTestUsers
import kr.weit.roadyfoody.user.repository.UserRepository

@RepositoryTest
class ReviewFlagRepositoryTest(
    private val userRepository: UserRepository,
    private val foodSpotsRepository: FoodSpotsRepository,
    private val reviewRepository: FoodSpotsReviewRepository,
    private val reviewFlagRepository: ReviewFlagRepository,
) : DescribeSpec(
        {
            lateinit var users: List<User>
            lateinit var foodSpots: FoodSpots
            lateinit var review: FoodSpotsReview
            lateinit var reviewFlags: List<FoodSpotsReviewFlag>
            beforeEach {
                users = userRepository.saveAll(createTestUsers(3))
                foodSpots = foodSpotsRepository.save(createTestFoodSpots())
                review = reviewRepository.save(createTestFoodSpotsReview(foodSpots = foodSpots, user = users[0]))
                reviewFlags = reviewFlagRepository.saveAll(users.map { createTestReviewFlag(review, it) })
            }

            describe("countByReviewId") {
                it("리뷰 ID로 리뷰 신고 수를 조회한다.") {
                    val count = reviewFlagRepository.countByReviewId(review.id)

                    count shouldBe reviewFlags.size
                }
            }

            describe("existsByReviewIdAndUserId") {
                it("리뷰 ID와 사용자 ID로 리뷰 신고가 존재하는지 확인한다.") {
                    val exists = reviewFlagRepository.existsByReviewIdAndUserId(review.id, users[0].id)

                    exists.shouldBeTrue()
                }
            }

            describe("deleteByReviewId") {
                it("리뷰 ID로 리뷰 신고를 삭제한다.") {
                    reviewFlagRepository.deleteByReviewId(review.id)

                    val count = reviewFlagRepository.countByReviewId(review.id)
                    count.shouldBeZero()
                }
            }
        },
    )
