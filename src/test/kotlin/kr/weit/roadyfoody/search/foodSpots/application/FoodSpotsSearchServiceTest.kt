package kr.weit.roadyfoody.search.foodSpots.application

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import kr.weit.roadyfoody.foodSpots.application.service.FoodSpotsQueryService
import kr.weit.roadyfoody.foodSpots.fixture.createFoodSpotsSearchResponses
import kr.weit.roadyfoody.search.foodSpots.application.service.FoodSpotsSearchService
import kr.weit.roadyfoody.search.foodSpots.dto.FoodSpotsSearchCondition
import kr.weit.roadyfoody.user.application.UserCommandService
import kr.weit.roadyfoody.user.fixture.createTestUser

class FoodSpotsSearchServiceTest :
    BehaviorSpec({
        val foodSpotsQueryService = mockk<FoodSpotsQueryService>()
        val userCommandService = mockk<UserCommandService>()

        val foodSpotsSearchService = FoodSpotsSearchService(foodSpotsQueryService, userCommandService)

        afterEach { clearAllMocks() }

        given("searchFoodSpots 테스트") {
            val query1000m =
                FoodSpotsSearchCondition(
                    centerLongitude = 0.0,
                    centerLatitude = 0.0,
                    radius = 1000,
                    name = null,
                    categoryIds = emptyList(),
                )
            `when`("코인이 있고 1000m이내 가게 검색을 요청하면") {

                val user = createTestUser(coin = 1000)
                every { userCommandService.decreaseCoin(any(), any()) } returns Unit
                every { foodSpotsQueryService.searchFoodSpots(query1000m) } returns createFoodSpotsSearchResponses()

                then("정상적으로 검색되어야 한다.") {
                    val result = foodSpotsSearchService.searchFoodSpots(user, query1000m)
                    result shouldBe createFoodSpotsSearchResponses()
                }
            }
        }
    })
