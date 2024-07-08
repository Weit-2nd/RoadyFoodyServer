package kr.weit.roadyfoody.foodSpots.repository

import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kr.weit.roadyfoody.foodSpots.domain.FoodCategory
import kr.weit.roadyfoody.foodSpots.domain.FoodSpots
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsFoodCategory
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOT_LATITUDE
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOT_LONGITUDE
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodCategories
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodSpotsFoodCategory
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodSpotsForDistance
import kr.weit.roadyfoody.support.annotation.RepositoryTest

@RepositoryTest
class FoodSpotsRepositoryTes(
    private val foodSpotsRepository: FoodSpotsRepository,
    private val foodSpotsFoodCategoryRepository: FoodSpotsFoodCategoryRepository,
    private val foodCategoryRepository: FoodCategoryRepository,
) : ExpectSpec({
        lateinit var foodSpots: List<FoodSpots>
        lateinit var foodSpotsFoodCategory: List<FoodSpotsFoodCategory>
        lateinit var foodCategory: List<FoodCategory>

        beforeEach {
            foodSpots = foodSpotsRepository.saveAll(createTestFoodSpotsForDistance())
        }

        context("현재 좌표를 기반으로 FoodSpots(가게) 조회") {
            expect("거리 이내 가게 조회한다.") {
                val expected =
                    foodSpotsRepository.findFoodSpotsByPointWithinRadius(
                        centerLatitude = TEST_FOOD_SPOT_LATITUDE,
                        centerLongitude = TEST_FOOD_SPOT_LONGITUDE,
                        radius = 500,
                        name = null,
                        categoryIds = emptyList(),
                    )

                expected.shouldHaveSize(3)
                expected.get(0).name shouldBe "Food Spot 1 - 100m"
                expected.get(1).name shouldBe "Food Spot 2 - 300m"
                expected.get(2).name shouldBe "Food Spot 3 - 500m"
            }
//            expect("거리 이내 사용자 검색 조건(가게 이름)에 맞는 가게 조회한다.") {
//                val expected =
//                    foodSpotsRepository.findFoodSpotsByPointWithinRadius(
//                        centerLatitude = TEST_FOOD_SPOT_LATITUDE,
//                        centerLongitude = TEST_FOOD_SPOT_LONGITUDE,
//                        radius = 500,
//                        name = "100m",
//                    )
//
//                expected.shouldHaveSize(1)
//                expected.get(0).name shouldBe "Food Spot 1 - 100m"
//            }
            expect("카테고리별로 거리 이내 가게를 조회한다.") {
                foodCategory = foodCategoryRepository.saveAll(createTestFoodCategories())
                foodSpotsFoodCategory =
                    foodSpotsFoodCategoryRepository.saveAll(
                        listOf(
                            createTestFoodSpotsFoodCategory(foodSpots = foodSpots[0], foodCategory = foodCategory[0]),
                            createTestFoodSpotsFoodCategory(foodSpots = foodSpots[0], foodCategory = foodCategory[1]),
                            createTestFoodSpotsFoodCategory(foodSpots = foodSpots[3], foodCategory = foodCategory[3]),
                            createTestFoodSpotsFoodCategory(foodSpots = foodSpots[3], foodCategory = foodCategory[1]),
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
    })
