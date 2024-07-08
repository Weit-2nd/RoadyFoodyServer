package kr.weit.roadyfoody.search.foodSpots.application

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.weit.roadyfoody.foodSpots.fixture.MockTestFoodSpot
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodSpotsForDistance
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsRepository

class FoodSpotsSearchServiceTest :
    BehaviorSpec({
        val foodSpotsRepository = mockk<FoodSpotsRepository>()
        val foodSpotsSearchService = FoodSpotsSearchService(foodSpotsRepository)

        afterEach { clearAllMocks() }

        given("searchFoodSpots 테스트") {
            `when`("정상적으로 거리 이내 음식점 검색이 가능한 경우") {
                every {
                    foodSpotsRepository.findFoodSpotsByPointWithinRadius(
                        0.0,
                        0.0,
                        1000,
                        null,
                        emptyList(),
                    )
                } returns createTestFoodSpotsForDistance()
                then("거리 이내 음식점을 반환한다.") {
                    val foodSpotsSearchResponses = foodSpotsSearchService.searchFoodSpots(0.0, 0.0, 1000, null, emptyList())
                    foodSpotsSearchResponses.items.shouldHaveSize(5)
                    verify(exactly = 1) { foodSpotsRepository.findFoodSpotsByPointWithinRadius(0.0, 0.0, 1000, null, emptyList()) }
                }
            }
            `when`("정상적으로 카테고리 별 거리 이내 음식점을 반환한다.") {
                every {
                    foodSpotsRepository.findFoodSpotsByPointWithinRadius(
                        0.0,
                        0.0,
                        1000,
                        null,
                        listOf(1L, 2L),
                    )
                } returns listOf(MockTestFoodSpot())
                then("카테고리 별 거리 이내 음식점을 반환한다.") {
                    val foodSpotsSearchResponses = foodSpotsSearchService.searchFoodSpots(0.0, 0.0, 1000, null, listOf(1L, 2L))
                    foodSpotsSearchResponses.items.shouldHaveSize(1)
                    verify(exactly = 1) { foodSpotsRepository.findFoodSpotsByPointWithinRadius(0.0, 0.0, 1000, null, listOf(1L, 2L)) }
                }
            }
            `when`("반환할 가게가 없는 경우") {
                every {
                    foodSpotsRepository.findFoodSpotsByPointWithinRadius(
                        0.0,
                        0.0,
                        1000,
                        null,
                        emptyList(),
                    )
                } returns emptyList()
                then("빈 리스트를 반환한다.") {
                    val foodSpotsSearchResponses = foodSpotsSearchService.searchFoodSpots(0.0, 0.0, 1000, null, emptyList())
                    foodSpotsSearchResponses.items.shouldHaveSize(0)
                    verify(exactly = 1) { foodSpotsRepository.findFoodSpotsByPointWithinRadius(0.0, 0.0, 1000, null, emptyList()) }
                }
            }
        }
    })
