package kr.weit.roadyfoody.foodSpots.repository

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kr.weit.roadyfoody.foodSpots.domain.FoodSpots
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsHistory
import kr.weit.roadyfoody.foodSpots.exception.FoodSpotsHistoryNotFoundException
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOTS_SIZE
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodHistory
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodSpots
import kr.weit.roadyfoody.support.annotation.RepositoryTest
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.fixture.createTestUser
import kr.weit.roadyfoody.user.repository.UserRepository

@RepositoryTest
class FoodSpotsHistoryRepositoryTest(
    private val foodSpotsHistoryRepository: FoodSpotsHistoryRepository,
    private val userRepository: UserRepository,
    private val foodSpotsRepository: FoodSpotsRepository,
) : DescribeSpec({
        lateinit var user: User
        lateinit var otherUser: User
        lateinit var user2: User
        lateinit var user3: User
        lateinit var user4: User
        lateinit var foodSpots: FoodSpots
        lateinit var otherFoodSpots: FoodSpots
        lateinit var notExistFoodSpots: FoodSpots
        lateinit var foodSpotForRanking: FoodSpots
        lateinit var foodSpotsHistories: List<FoodSpotsHistory>
        beforeEach {
            user = userRepository.save(createTestUser(0L))
            otherUser = userRepository.save(createTestUser(0L, nickname = "otherUser"))
            user2 = userRepository.save(createTestUser(2L, "existentNick2"))
            user3 = userRepository.save(createTestUser(3L, "existentNick3"))
            user4 = userRepository.save(createTestUser(4L, "existentNick4"))
            foodSpots = foodSpotsRepository.save(createTestFoodSpots())
            otherFoodSpots = foodSpotsRepository.save(createTestFoodSpots())
            notExistFoodSpots = foodSpotsRepository.save(createTestFoodSpots())
            foodSpotForRanking = foodSpotsRepository.save(createTestFoodSpots())
            foodSpotsHistories =
                foodSpotsHistoryRepository.saveAll(
                    listOf(
                        createTestFoodHistory(user = user, foodSpots = foodSpots),
                        createTestFoodHistory(user = user, foodSpots = otherFoodSpots),
                        createTestFoodHistory(user = user2, foodSpots = foodSpotForRanking),
                        createTestFoodHistory(user = user2, foodSpots = foodSpotForRanking),
                        createTestFoodHistory(user = user2, foodSpots = foodSpotForRanking),
                        createTestFoodHistory(user = user3, foodSpots = foodSpotForRanking),
                        createTestFoodHistory(user = user3, foodSpots = foodSpotForRanking),
                        createTestFoodHistory(user = user4, foodSpots = foodSpotForRanking),
                    ),
                )
        }

        describe("getHistoriesByUser 메소드는") {
            context("존재하는 user 와 size, lastId 를 받는 경우") {
                it("해당 user 의 size 만큼의 FoodSpotsHistory 리스트를 반환한다.") {
                    val histories = foodSpotsHistoryRepository.findSliceByUser(user, TEST_FOOD_SPOTS_SIZE, null)
                    histories.map { it.foodSpots.id }.content shouldBe
                        listOf(
                            otherFoodSpots.id,
                            foodSpots.id,
                        )
                    histories.content.size shouldBe 2
                }
            }
        }

        describe("findByUser 메소드는") {
            context("리포트 이력이 존재하는 user 를 받는 경우") {
                it("해당 user 의 FoodSpotsHistory 리스트를 반환한다.") {
                    val histories = foodSpotsHistoryRepository.findByUser(user)
                    histories.size shouldBe 2
                }
            }

            context("리포트 이력이 없는 user 를 받는 경우") {
                it("빈 리스트를 반환한다.") {
                    val histories = foodSpotsHistoryRepository.findByUser(otherUser)
                    histories shouldBe emptyList()
                }
            }
        }

        describe("deleteAll 메소드는") {
            context("history list를 받는 경우") {
                it("해당 history 리스트 모두 삭제한다.") {
                    val histories = foodSpotsHistoryRepository.findByUser(user)
                    foodSpotsHistoryRepository.deleteAll(histories)
                    val historiesAfterDelete = foodSpotsHistoryRepository.findByUser(user)
                    historiesAfterDelete.size shouldBe 0
                }
            }
        }

        describe("getByHistoryId 메소드는") {
            context("존재하는 historyId 를 받는 경우") {
                it("해당 historyId 의 FoodSpotsHistory 를 반환한다.") {
                    val history = foodSpotsHistoryRepository.getByHistoryId(foodSpotsHistories[0].id)
                    history shouldBe foodSpotsHistories[0]
                }
            }

            context("존재하지 않는 historyId 를 받는 경우") {
                it("에러가 발생한다") {
                    shouldThrow<FoodSpotsHistoryNotFoundException> {
                        foodSpotsHistoryRepository.getByHistoryId(0L)
                    }
                }
            }
        }

        describe("getByFoodSpots 메소드는") {
            context("존재하는 FoodSpots 를 받는 경우") {
                it("해당 FoodSpots 에 대한 FoodSpotsHistory 리스트를 반환한다.") {
                    val histories = foodSpotsHistoryRepository.findByFoodSpots(foodSpots)
                    histories.size shouldBe 1
                    histories[0].foodSpots shouldBe foodSpots
                }
            }

            context("존재하지 않는 FoodSpots 를 받는 경우") {
                it("빈 리스트를 반환한다.") {
                    val histories = foodSpotsHistoryRepository.findByFoodSpots(notExistFoodSpots)
                    histories shouldBe emptyList()
                }
            }
        }

        describe("findAllUserReportCount 메소드는") {
            it("전체 회원의 닉네임과 리포트 개수를 정렬하여 리스트로 반환한다") {
                val userReportCounts = foodSpotsHistoryRepository.findAllUserReportCount()
                userReportCounts.size shouldBe 4
                userReportCounts[0].userNickname shouldBe "existentNick2"
                userReportCounts[0].total shouldBe 3
                userReportCounts[0].profileImageUrl shouldBe "test_image_name_2"

                userReportCounts[1].userNickname shouldBe "existentNick"
                userReportCounts[1].total shouldBe 2
                userReportCounts[1].profileImageUrl shouldBe "test_image_name_0"

                userReportCounts[2].userNickname shouldBe "existentNick3"
                userReportCounts[2].total shouldBe 2
                userReportCounts[2].profileImageUrl shouldBe "test_image_name_3"

                userReportCounts[3].userNickname shouldBe "existentNick4"
                userReportCounts[3].total shouldBe 1
                userReportCounts[3].profileImageUrl shouldBe "test_image_name_4"
            }
        }

        describe("countByUser 메소드는") {
            context("가게 리포트를 작성한 user 를 받는 경우") {
                it("해당 user 의 가게 리포트 이력 개수를 반환한다.") {
                    val count = foodSpotsHistoryRepository.countByUser(user)
                    count shouldBe 2
                }
            }

            context("리포트를 작성하지 않은 user 를 받는 경우") {
                it("0을 반환한다.") {
                    val count = foodSpotsHistoryRepository.countByUser(otherUser)
                    count shouldBe 0
                }
            }
        }
    })
