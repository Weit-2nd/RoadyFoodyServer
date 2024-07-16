package kr.weit.roadyfoody.search.foodSpots.application

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.weit.roadyfoody.common.exception.ErrorCode
import kr.weit.roadyfoody.common.exception.RoadyFoodyBadRequestException
import kr.weit.roadyfoody.foodSpots.fixture.MockTestFoodSpot
import kr.weit.roadyfoody.foodSpots.fixture.createMockTestFoodSpotList
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodSpotsForDistance
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsRepository
import kr.weit.roadyfoody.global.redisson.RedisLockUtil
import kr.weit.roadyfoody.search.foodSpots.dto.FoodSpotsSearchCondition
import kr.weit.roadyfoody.user.fixture.createTestUser
import kr.weit.roadyfoody.user.repository.UserRepository

class FoodSpotsSearchServiceTest :
    BehaviorSpec({
        val foodSpotsRepository = mockk<FoodSpotsRepository>()
        val userRepository = mockk<UserRepository>()
        val redisLockUtil = mockk<RedisLockUtil>()
        val foodSpotsSearchService = FoodSpotsSearchService(foodSpotsRepository, userRepository, redisLockUtil)
        val user = createTestUser()
        afterEach { clearAllMocks() }

        given("searchFoodSpots 테스트") {
            val query500m =
                FoodSpotsSearchCondition(
                    centerLongitude = 0.0,
                    centerLatitude = 0.0,
                    radius = 500,
                    name = null,
                    categoryIds = emptyList(),
                )
            `when`("정상적으로 500m 거리 이내 음식점 검색이 가능한 경우") {
                every {
                    foodSpotsRepository.findFoodSpotsByPointWithinRadius(
                        0.0,
                        0.0,
                        500,
                        null,
                        emptyList(),
                    )
                } returns createMockTestFoodSpotList()
                then("500m 거리 이내 음식점을 반환한다.") {

                    val foodSpotsSearchResponses = foodSpotsSearchService.searchFoodSpots(user, query500m)
                    foodSpotsSearchResponses.items.shouldHaveSize(3)
                    verify(exactly = 1) { foodSpotsRepository.findFoodSpotsByPointWithinRadius(0.0, 0.0, 500, null, emptyList()) }
                }
            }
            val query1000m =
                FoodSpotsSearchCondition(
                    centerLongitude = 0.0,
                    centerLatitude = 0.0,
                    radius = 1000,
                    name = null,
                    categoryIds = emptyList(),
                )
            `when`("정상적으로 10000m 거리 이내 음식점 검색이 가능한 경우") {
                every {
                    foodSpotsRepository.findFoodSpotsByPointWithinRadius(
                        0.0,
                        0.0,
                        1000,
                        null,
                        emptyList(),
                    )
                } returns createTestFoodSpotsForDistance()

                every {
                    redisLockUtil.executeWithLock(any<String>(), any<() -> Unit>())
                } answers {
                    val callback = it.invocation.args[1] as () -> Unit
                    callback()
                    true
                }
                val userWithCoin = createTestUser(coin = 1000)
                every {
                    userRepository.save(any())
                } returns createTestUser(coin = 800)

                then("1000m 거리 이내 음식점을 반환한다.") {
                    val foodSpotsSearchResponses = foodSpotsSearchService.searchFoodSpots(userWithCoin, query1000m)
                    foodSpotsSearchResponses.items.shouldHaveSize(5)
                    verify(exactly = 1) { foodSpotsRepository.findFoodSpotsByPointWithinRadius(0.0, 0.0, 1000, null, emptyList()) }
                }
            }

            `when`("코인이 없어 10000m 거리 이내 음식점 검색이 불가능한 경우") {
                every {
                    foodSpotsRepository.findFoodSpotsByPointWithinRadius(
                        0.0,
                        0.0,
                        1000,
                        null,
                        emptyList(),
                    )
                } returns createTestFoodSpotsForDistance()

                every {
                    redisLockUtil.executeWithLock(any<String>(), any<() -> Unit>())
                } answers {
                    val callback = it.invocation.args[1] as () -> Unit
                    callback()
                    true
                }

                val userWithoutCoin = createTestUser(coin = 0)

                then("${ErrorCode.COIN_NOT_ENOUGH.errorMessage}를 던진다") {
                    val ex =
                        shouldThrow<RoadyFoodyBadRequestException> {
                            foodSpotsSearchService.searchFoodSpots(userWithoutCoin, query1000m)
                        }
                    ex.message shouldBe ErrorCode.COIN_NOT_ENOUGH.errorMessage
                }
            }

            `when`("정상적으로 카테고리 별 500m 거리 이내 음식점을 반환한다.") {
                val categoryQuery =
                    FoodSpotsSearchCondition(
                        centerLongitude = 0.0,
                        centerLatitude = 0.0,
                        radius = 500,
                        name = null,
                        categoryIds = listOf(1L, 2L),
                    )
                every {
                    foodSpotsRepository.findFoodSpotsByPointWithinRadius(
                        0.0,
                        0.0,
                        500,
                        null,
                        listOf(1L, 2L),
                    )
                } returns listOf(MockTestFoodSpot())

                then("카테고리 별 500m 거리 이내 음식점을 반환한다.") {
                    val foodSpotsSearchResponses = foodSpotsSearchService.searchFoodSpots(user, categoryQuery)
                    foodSpotsSearchResponses.items.shouldHaveSize(1)
                    verify(exactly = 1) { foodSpotsRepository.findFoodSpotsByPointWithinRadius(0.0, 0.0, 500, null, listOf(1L, 2L)) }
                }
            }
            `when`("반환할 가게가 없는 경우") {
                every {
                    foodSpotsRepository.findFoodSpotsByPointWithinRadius(
                        0.0,
                        0.0,
                        500,
                        null,
                        emptyList(),
                    )
                } returns emptyList()
                then("빈 리스트를 반환한다.") {
                    val foodSpotsSearchResponses = foodSpotsSearchService.searchFoodSpots(user, query500m)
                    foodSpotsSearchResponses.items.shouldBeEmpty()
                    verify(exactly = 1) { foodSpotsRepository.findFoodSpotsByPointWithinRadius(0.0, 0.0, 500, null, emptyList()) }
                }
            }
        }
    })
