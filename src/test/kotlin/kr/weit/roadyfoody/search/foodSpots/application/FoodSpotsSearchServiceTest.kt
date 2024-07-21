package kr.weit.roadyfoody.search.foodSpots.application

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.mockk
import kr.weit.roadyfoody.common.exception.ErrorCode
import kr.weit.roadyfoody.common.exception.RoadyFoodyBadRequestException
import kr.weit.roadyfoody.foodSpots.application.service.FoodSpotsQueryService
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
            `when`("코인이 없어 10000m 거리 이내 음식점 검색이 불가능한 경우") {

                val userWithoutCoin = createTestUser(coin = 0)

                then("${ErrorCode.COIN_NOT_ENOUGH.errorMessage}를 던진다") {
                    val ex =
                        shouldThrow<RoadyFoodyBadRequestException> {
                            foodSpotsSearchService.searchFoodSpots(userWithoutCoin, query1000m)
                        }
                    ex.message shouldBe ErrorCode.COIN_NOT_ENOUGH.errorMessage
                }
            }
        }
    })
