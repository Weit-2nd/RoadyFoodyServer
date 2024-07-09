package kr.weit.roadyfoody.foodSpots.repository

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kr.weit.roadyfoody.foodSpots.domain.FoodCategory
import kr.weit.roadyfoody.foodSpots.domain.FoodSpots
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsHistory
import kr.weit.roadyfoody.foodSpots.domain.ReportFoodCategory
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodCategory
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodHistory
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodSpots
import kr.weit.roadyfoody.foodSpots.fixture.createTestReportFoodCategory
import kr.weit.roadyfoody.support.annotation.RepositoryTest
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.fixture.createTestUser
import kr.weit.roadyfoody.user.repository.UserRepository

@RepositoryTest
class ReportFoodCategoryRepositoryTest(
    private val userRepository: UserRepository,
    private val reportFoodCategoryRepository: ReportFoodCategoryRepository,
    private val foodSpotsRepository: FoodSpotsRepository,
    private val foodSpotsHistoryRepository: FoodSpotsHistoryRepository,
    private val foodCategoryRepository: FoodCategoryRepository,
) : DescribeSpec({
        lateinit var givenReportCategories: List<ReportFoodCategory>
        lateinit var user: User
        lateinit var foodSpots: FoodSpots
        lateinit var foodSpotsHistory: FoodSpotsHistory
        lateinit var categories: List<FoodCategory>
        beforeEach {
            user = userRepository.save(createTestUser(0L))
            foodSpots = foodSpotsRepository.save(createTestFoodSpots())
            foodSpotsHistory = foodSpotsHistoryRepository.save(createTestFoodHistory(user = user, foodSpots = foodSpots))
            categories =
                foodCategoryRepository.saveAll(
                    listOf(
                        createTestFoodCategory("떡볶이"),
                        createTestFoodCategory("붕어빵"),
                        createTestFoodCategory("타코야끼"),
                    ),
                )
            givenReportCategories =
                reportFoodCategoryRepository.saveAll(
                    mutableListOf(
                        createTestReportFoodCategory(foodSpotsHistory, categories[0]),
                        createTestReportFoodCategory(foodSpotsHistory, categories[1]),
                        createTestReportFoodCategory(foodSpotsHistory, categories[2]),
                    ),
                )
        }

        describe("getByHistoryId 메소드는") {
            context("존재하는 historyId 를 받는 경우") {
                it("일치하는 ReportFoodCategory 리스트를 반환한다.") {
                    val result = reportFoodCategoryRepository.getByHistoryId(foodSpotsHistory.id)
                    result.map { it.id }.sorted() shouldBe givenReportCategories.map { it.id }
                }
            }
        }
    })
