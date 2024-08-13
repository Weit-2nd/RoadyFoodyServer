package kr.weit.roadyfoody.review.repository

import createTestFoodSpotsReview
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kr.weit.roadyfoody.foodSpots.domain.FoodSpots
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodSpots
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsRepository
import kr.weit.roadyfoody.global.TEST_PAGE_SIZE
import kr.weit.roadyfoody.review.domain.FoodSpotsReview
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
            lateinit var noReviewsFoodSpots: FoodSpots
            lateinit var reviewList: List<FoodSpotsReview>
            beforeEach {
                user = userRepository.save(createTestUser(0L))
                otherUser = userRepository.save(createTestUser(0L, "otherUser"))
                foodSpots = foodSpotsRepository.save(createTestFoodSpots())
                otherFoodSpots = foodSpotsRepository.save(createTestFoodSpots())
                noReviewsFoodSpots = foodSpotsRepository.save(createTestFoodSpots())
                reviewList =
                    reviewRepository.saveAll(
                        listOf(
                            createTestFoodSpotsReview(user, foodSpots, 10),
                            createTestFoodSpotsReview(user, otherFoodSpots, 10),
                            createTestFoodSpotsReview(otherUser, foodSpots, 4),
                            createTestFoodSpotsReview(user, otherFoodSpots, 10),
                        ),
                    )
            }

            describe("findByUser 메소드는") {
                context("리뷰를 작성한 사용자를 받는 경우") {
                    it("해당 사용자의 리뷰 리스트를 반환한다.") {
                        reviewRepository.findByUser(user).size shouldBe 3
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
                        reviewRepository.getReviewByReviewId(reviewList.first().id) shouldBe reviewList.first()
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

            describe("sliceByUser 메소드는") {
                context("유저 ID, 사이즈, 마지막 ID을 받는 경우") {
                    it("해당 유저의 리뷰 리스트를 반환한다.") {
                        reviewRepository
                            .sliceByUser(
                                user,
                                TEST_PAGE_SIZE,
                                reviewList[1].id,
                            ).content.size shouldBe 1
                    }
                }

                context("유저 ID, 사이즈만 받는 경우") {
                    it("해당 유저의 리뷰 리스트를 반환한다.") {
                        reviewRepository
                            .sliceByUser(
                                user,
                                TEST_PAGE_SIZE,
                                null,
                            ).content.size shouldBe 3
                    }
                }
            }

            describe("sliceByFoodSpots 메소드는") {
                context("음식점 ID, 사이즈, 별점 높은 순 정렬 타입을 받는 경우") {
                    it("별점 높은 순으로 음식점의 리뷰 리스트를 반환한다.") {
                        val contents =
                            reviewRepository
                                .sliceByFoodSpots(
                                    foodSpots.id,
                                    TEST_PAGE_SIZE,
                                    null,
                                    ReviewSortType.HIGHEST,
                                )
                        contents.content shouldBe listOf(reviewList[0], reviewList[2])
                    }
                }

                context("별점 높은 순 정렬타입에 별점이 똑같은 경우") {
                    it("최신순으로 나온다") {
                        val contents =
                            reviewRepository
                                .sliceByFoodSpots(
                                    otherFoodSpots.id,
                                    TEST_PAGE_SIZE,
                                    null,
                                    ReviewSortType.HIGHEST,
                                )
                        contents.content shouldBe listOf(reviewList[3], reviewList[1])
                    }
                }

                context("음식점 ID, 사이즈, 최신순 정렬 타입을 받는 경우") {
                    it("최신 순으로 해당 음식점의 리뷰 리스트를 반환한다.") {
                        val contents =
                            reviewRepository
                                .sliceByFoodSpots(
                                    foodSpots.id,
                                    TEST_PAGE_SIZE,
                                    null,
                                    ReviewSortType.LATEST,
                                )
                        contents.content shouldBe listOf(reviewList[2], reviewList[0])
                    }
                }

                context("음식점 ID, 사이즈, 마지막 ID, 정렬 타입을 받는 경우") {
                    it("해당 음식점의 리뷰 리스트를 반환한다.") {
                        val contents =
                            reviewRepository
                                .sliceByFoodSpots(
                                    foodSpots.id,
                                    TEST_PAGE_SIZE,
                                    reviewList[0].id,
                                    ReviewSortType.HIGHEST,
                                )
                        contents.content shouldBe listOf(reviewList[2])
                    }
                }
            }
            describe("getFoodSpotsAvgRate 메소드는") {
                context("음식점을 받는 경우") {
                    it("해당 음식점의 평균 별점을 반환한다.") {
                        reviewRepository.getFoodSpotsAvgRate(foodSpots) shouldBe 7.0
                    }
                }

                context("리뷰가 없는 음식점을 받는 경우") {
                    it("0.0을 반환한다.") {
                        reviewRepository.getFoodSpotsAvgRate(noReviewsFoodSpots) shouldBe 0.0
                    }
                }
            }

            describe("countFoodSpotsReview 메소드는") {
                context("음식점을 받는 경우") {
                    it("해당 음식점의 리뷰 개수를 반환한다.") {
                        reviewRepository.countFoodSpotsReview(otherFoodSpots) shouldBe 2
                    }
                }

                context("리뷰가 없는 음식점을 받는 경우") {
                    it("0을 반환한다.") {
                        reviewRepository.countFoodSpotsReview(noReviewsFoodSpots) shouldBe 0
                    }
                }
            }
        },
    )
