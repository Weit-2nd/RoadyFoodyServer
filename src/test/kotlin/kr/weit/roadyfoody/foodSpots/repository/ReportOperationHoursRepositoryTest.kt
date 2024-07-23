package kr.weit.roadyfoody.foodSpots.repository

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kr.weit.roadyfoody.foodSpots.domain.FoodSpots
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsHistory
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodHistory
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodSpots
import kr.weit.roadyfoody.foodSpots.fixture.createTestReportOperationHours
import kr.weit.roadyfoody.support.annotation.RepositoryTest
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.fixture.createTestUser
import kr.weit.roadyfoody.user.repository.UserRepository

@RepositoryTest
class ReportOperationHoursRepositoryTest(
    private val userRepository: UserRepository,
    private val foodSpotsRepository: FoodSpotsRepository,
    private val foodSpotsHistoryRepository: FoodSpotsHistoryRepository,
    private val reportOperationHoursRepository: ReportOperationHoursRepository,
) : DescribeSpec({
        lateinit var user: User
        lateinit var foodSpots: FoodSpots
        lateinit var foodSpotsHistory: FoodSpotsHistory
        beforeEach {
            user = userRepository.save(createTestUser(0L))
            foodSpots = foodSpotsRepository.save(createTestFoodSpots())
            foodSpotsHistory =
                foodSpotsHistoryRepository.save(
                    createTestFoodHistory(
                        user = user,
                        foodSpots = foodSpots,
                    ),
                )
            reportOperationHoursRepository.saveAll(
                listOf(
                    createTestReportOperationHours(foodSpotsHistory),
                    createTestReportOperationHours(foodSpotsHistory),
                ),
            )
        }

        describe("deleteByFoodSpotsHistoryIn 메소드는") {
            context("카테고리를 등록한 history 리스트를 받는 경우") {
                it("해당 history 리스트 모두 삭제한다.") {
                    reportOperationHoursRepository.deleteByFoodSpotsHistoryIn(listOf(foodSpotsHistory))
                    reportOperationHoursRepository.findAll().size shouldBe 0
                }
            }
        }
    })
