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
        lateinit var foodSpots: FoodSpots
        lateinit var otherFoodSpots: FoodSpots
        lateinit var foodSpotsHistories: List<FoodSpotsHistory>
        beforeEach {
            user = userRepository.save(createTestUser(0L))
            otherUser = userRepository.save(createTestUser(0L, nickname = "otherUser"))
            foodSpots = foodSpotsRepository.save(createTestFoodSpots())
            otherFoodSpots = foodSpotsRepository.save(createTestFoodSpots())
            foodSpotsHistories =
                foodSpotsHistoryRepository.saveAll(
                    listOf(
                        createTestFoodHistory(user = user, foodSpots = foodSpots),
                        createTestFoodHistory(user = user, foodSpots = otherFoodSpots),
                    ),
                )
        }

        describe("getHistoriesByUser 메소드는") {
            context("존재하는 user 와 size, lastId 를 받는 경우") {
                it("해당 user 의 size 만큼의 FoodSpotsHistory 리스트를 반환한다.") {
                    val histories = foodSpotsHistoryRepository.getHistoriesByUser(user, TEST_FOOD_SPOTS_SIZE, null)
                    histories.map { it.id }.content shouldBe listOf(otherFoodSpots.id, foodSpots.id)
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
    })
