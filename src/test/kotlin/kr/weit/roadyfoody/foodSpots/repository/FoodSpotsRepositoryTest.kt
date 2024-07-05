package kr.weit.roadyfoody.foodSpots.repository

import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kr.weit.roadyfoody.foodSpots.domain.FoodSpots
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOT_LATITUDE
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOT_LONGITUDE
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodSpotsForDistance
import kr.weit.roadyfoody.support.annotation.RepositoryTest

@RepositoryTest
class FoodSpotsRepositoryTest(
    private val foodSpotsRepository: FoodSpotsRepository,
) : ExpectSpec({
        lateinit var foodSpots: List<FoodSpots>
        beforeEach {
            foodSpots = foodSpotsRepository.saveAll(createTestFoodSpotsForDistance())
        }
        context("현재 좌표를 기반으로 FoodSpots(가게) 조회") {
            expect("거리 이내 가게 조회한다.") {
                val expected =
                    foodSpotsRepository.findFoodSpotsByPointWithinRadius(
                        TEST_FOOD_SPOT_LATITUDE,
                        TEST_FOOD_SPOT_LONGITUDE,
                        500,
                    )

                expected.shouldHaveSize(3)
                expected.get(0).name shouldBe "Food Spot 1 - 100m"
                expected.get(1).name shouldBe "Food Spot 2 - 300m"
                expected.get(2).name shouldBe "Food Spot 3 - 500m"
            }
        }
    })
