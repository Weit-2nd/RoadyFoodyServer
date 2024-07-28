package kr.weit.roadyfoody.foodSpots.repository

import TEST_INVALID_FOOD_SPOT_ID
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kr.weit.roadyfoody.foodSpots.domain.FoodCategory
import kr.weit.roadyfoody.foodSpots.domain.FoodSpots
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOT_LATITUDE
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOT_LONGITUDE
import kr.weit.roadyfoody.foodSpots.fixture.createFoodSpotsForDistance
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodCategories
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodSpotsFoodCategory
import kr.weit.roadyfoody.review.exception.FoodSpotsNotFoundException
import kr.weit.roadyfoody.support.annotation.RepositoryTest

@RepositoryTest
class FoodSpotsRepositoryTest(
    private val foodSpotsRepository: FoodSpotsRepository,
    private val foodSpotsFoodCategoryRepository: FoodSpotsFoodCategoryRepository,
    private val foodCategoryRepository: FoodCategoryRepository,
) : DescribeSpec({
        lateinit var foodSpots: List<FoodSpots>
        lateinit var foodCategory: List<FoodCategory>

        beforeSpec {
            foodSpots = foodSpotsRepository.saveAllAndFlush(createFoodSpotsForDistance())
        }

        describe("getHistoriesByUser 메소드는") {
            context("현재 좌표를 기반으로 FoodSpots(가게) 조회") {
                it("거리 이내 가게 조회한다.") {
                    val expected =
                        foodSpotsRepository.findFoodSpotsByPointWithinRadius(
                            centerLatitude = TEST_FOOD_SPOT_LATITUDE,
                            centerLongitude = TEST_FOOD_SPOT_LONGITUDE,
                            radius = 500,
                            name = null,
                            categoryIds = emptyList(),
                        )

                    expected.shouldHaveSize(3)
                    expected[0].name shouldBe "Food Spot 1 - 100m"
                    expected[1].name shouldBe "Food Spot 2 - 300m"
                    expected[2].name shouldBe "Food Spot 3 - 500m"
                }
            }
            context("거리 이내 사용자 검색 조건(가게 이름)에 맞는 가게 조회") {
                it("이름 조건에 맞는 가게를 반환한다..") {
                    val expected =
                        foodSpotsRepository.findFoodSpotsByPointWithinRadius(
                            centerLatitude = TEST_FOOD_SPOT_LATITUDE,
                            centerLongitude = TEST_FOOD_SPOT_LONGITUDE,
                            radius = 500,
                            name = "100",
                            categoryIds = emptyList(),
                        )

                    expected.shouldHaveSize(1)
                    expected[0].name shouldBe "Food Spot 1 - 100m"
                }
            }
            context("거리 이내 사용자 검색 조건(카테고리)에 맞는 가게 조회") {
                it("카테고리별로 거리 이내 가게를 조회한다.") {
                    foodCategory = foodCategoryRepository.saveAll(createTestFoodCategories())
                    val foodSpotsFoodCategory =
                        foodSpotsFoodCategoryRepository.saveAll(
                            listOf(
                                createTestFoodSpotsFoodCategory(
                                    foodSpots = foodSpots[0],
                                    foodCategory = foodCategory[0],
                                ),
                                createTestFoodSpotsFoodCategory(
                                    foodSpots = foodSpots[0],
                                    foodCategory = foodCategory[1],
                                ),
                                createTestFoodSpotsFoodCategory(
                                    foodSpots = foodSpots[3],
                                    foodCategory = foodCategory[3],
                                ),
                                createTestFoodSpotsFoodCategory(
                                    foodSpots = foodSpots[3],
                                    foodCategory = foodCategory[1],
                                ),
                            ),
                        )
                    val expected =
                        foodSpotsRepository.findFoodSpotsByPointWithinRadius(
                            centerLatitude = TEST_FOOD_SPOT_LATITUDE,
                            centerLongitude = TEST_FOOD_SPOT_LONGITUDE,
                            radius = 500,
                            name = null,
                            categoryIds = listOf(foodCategory[0].id, foodCategory[1].id),
                        )

                    expected.shouldHaveSize(1)
                    expected.get(0).name shouldBe foodSpots[0].name
                }
            }
        }
        describe("getByFoodSpotsId") {
            context("존재하는 id를 받는 경우") {
                it("해당 id의 음식점를 반환") {
                    val foodSpot = foodSpotsRepository.getByFoodSpotsId(foodSpots[0].id)
                    foodSpot.name shouldBe foodSpots[0].name
                }
            }

            context("존재하지 않는 id를 받는 경우") {
                it("FoodSpotsNotFoundException 반환") {
                    shouldThrow<FoodSpotsNotFoundException> {
                        foodSpotsRepository.getByFoodSpotsId(
                            TEST_INVALID_FOOD_SPOT_ID,
                        )
                    }
                }
            }
        }

        describe("updateAllOpen") {
            context("닫은 가게가 있는 경우") {
                it("open 컬럼을 true로 업데이트한다.") {
                    val updatedCount = foodSpotsRepository.updateOpeningStatus()
                    foodSpotsRepository.findAll().forEach {
                        it.open shouldBe true
                    }
                    updatedCount shouldBe 2
                }
            }
            context("영업중인 가게가 없는 경우") {
                it("업데이트하지 않는다.") {
                    foodSpotsRepository.updateOpeningStatus()
                    foodSpotsRepository.updateOpeningStatus() shouldBe 0
            }
        }
    }
    })
