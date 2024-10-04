package kr.weit.roadyfoody.search.foodSpots.application

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kr.weit.roadyfoody.common.exception.RoadyFoodyBadRequestException
import kr.weit.roadyfoody.foodSpots.application.service.FoodSpotsQueryService
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOT_LATITUDE
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOT_LONGITUDE
import kr.weit.roadyfoody.foodSpots.fixture.createFoodSpotsSearchResponses
import kr.weit.roadyfoody.foodSpots.fixture.createMockSearchCoinCaches
import kr.weit.roadyfoody.rewards.application.service.RewardsCommandService
import kr.weit.roadyfoody.rewards.fixture.createTestRewards
import kr.weit.roadyfoody.search.foodSpots.application.service.FoodSpotsSearchCommandService
import kr.weit.roadyfoody.search.foodSpots.application.service.FoodSpotsSearchService
import kr.weit.roadyfoody.search.foodSpots.fixture.createFoodSpotsPopularSearchesResponse
import kr.weit.roadyfoody.search.foodSpots.fixture.createFoodSpotsSearchCondition
import kr.weit.roadyfoody.search.foodSpots.fixture.createFoodSpotsSearchKeywords
import kr.weit.roadyfoody.search.foodSpots.fixture.createRequiredCoinRequest
import kr.weit.roadyfoody.search.foodSpots.fixture.createRequiredCoinResponse
import kr.weit.roadyfoody.search.foodSpots.repository.FoodSpotsSearchHistoryRepository
import kr.weit.roadyfoody.search.foodSpots.repository.SearchCoinCacheRepository
import kr.weit.roadyfoody.user.application.service.UserCommandService
import kr.weit.roadyfoody.user.fixture.createTestUser
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.data.redis.core.RedisTemplate
import java.util.concurrent.ExecutorService

class FoodSpotsSearchServiceTest :
    BehaviorSpec({
        val foodSpotsQueryService = mockk<FoodSpotsQueryService>()
        val userCommandService = mockk<UserCommandService>()
        val searchCoinCacheRepository = mockk<SearchCoinCacheRepository>()
        val rewardsCommandService = mockk<RewardsCommandService>()
        val foodSpotsSearchHistoryRepository = mockk<FoodSpotsSearchHistoryRepository>()
        val foodSpotsSearchCommandService = mockk<FoodSpotsSearchCommandService>()
        val redisTemplate = mockk<RedisTemplate<String, String>>()
        val cacheManager = mockk<CacheManager>()
        val executor = mockk<ExecutorService>()
        val foodSpotsSearchService =
            FoodSpotsSearchService(
                foodSpotsQueryService,
                userCommandService,
                searchCoinCacheRepository,
                rewardsCommandService,
                foodSpotsSearchHistoryRepository,
                foodSpotsSearchCommandService,
                redisTemplate,
                cacheManager,
                executor,
            )

        afterEach { clearAllMocks() }

        given("getRequiredCoin 테스트") {
            val query1000m =
                createRequiredCoinRequest(
                    0.0,
                    0.0,
                    1000,
                )

            `when`("1000m이내 가게 검색 요청시 - 코인 캐싱 x") {
                val user = createTestUser()
                every { searchCoinCacheRepository.findByUserId(user.id) } returns emptyList()

                then("소모될 예정인 코인을 리턴한다") {
                    val result = foodSpotsSearchService.getRequiredCoin(user, query1000m)
                    result shouldBe createRequiredCoinResponse(200)
                }
            }
            val query1000mWithCache =
                createRequiredCoinRequest(
                    TEST_FOOD_SPOT_LONGITUDE,
                    TEST_FOOD_SPOT_LATITUDE,
                    1000,
                )

            `when`("1000m이내 가게 검색 요청시 - 코인 캐싱 o") {
                val user = createTestUser()
                every { searchCoinCacheRepository.findByUserId(user.id) } returns createMockSearchCoinCaches(user.id)

                then("소모될 코인이 없기 때문에 0을 리턴한다") {
                    val result = foodSpotsSearchService.getRequiredCoin(user, query1000mWithCache)
                    result shouldBe createRequiredCoinResponse(0)
                }
            }

            val query500m =
                createRequiredCoinRequest(
                    0.0,
                    0.0,
                    500,
                )

            `when`("500m 이내에 가게 검색시") {
                val user = createTestUser()

                then("소모될 코인이 없기 때문에 0을 리턴한다") {
                    val result = foodSpotsSearchService.getRequiredCoin(user, query500m)
                    result shouldBe createRequiredCoinResponse(0)
                }
            }
        }

        given("searchFoodSpots 테스트") {
            val query1000m =
                createFoodSpotsSearchCondition(
                    0.0,
                    0.0,
                    1000,
                )
            `when`("코인이 있고 1000m이내 가게 검색을 요청하면 - 코인 캐싱 x") {

                val user = createTestUser(coin = 1000)
                every { searchCoinCacheRepository.findByUserId(user.id) } returns emptyList()
                every { searchCoinCacheRepository.save(any()) } returns mockk()
                every { userCommandService.decreaseCoin(any(), any()) } just runs
                every { rewardsCommandService.createRewards(any()) } just runs
                every { foodSpotsQueryService.searchFoodSpots(query1000m) } returns createFoodSpotsSearchResponses()

                then("정상적으로 검색되어야 한다.") {
                    val result = foodSpotsSearchService.searchFoodSpots(user, query1000m)
                    verify(exactly = 1) { userCommandService.decreaseCoin(user.id, 200) }
                    verify(exactly = 1) { searchCoinCacheRepository.save(any()) }
                    verify(exactly = 1) { rewardsCommandService.createRewards(any()) }
                    result shouldBe createFoodSpotsSearchResponses()
                }
            }
            val query1000mWithCache =
                createFoodSpotsSearchCondition(
                    TEST_FOOD_SPOT_LONGITUDE,
                    TEST_FOOD_SPOT_LATITUDE,
                    1000,
                )
            `when`("코인이 있고 1000m이내 가게 검색을 요청하면 - 코인 캐싱 o") {
                val user = createTestUser(coin = 1000)
                every { searchCoinCacheRepository.findByUserId(user.id) } returns createMockSearchCoinCaches(user.id)
                every { foodSpotsQueryService.searchFoodSpots(query1000mWithCache) } returns createFoodSpotsSearchResponses()

                then("정상적으로 검색되어야 한다.") {
                    val result = foodSpotsSearchService.searchFoodSpots(user, query1000mWithCache)
                    verify(exactly = 0) { rewardsCommandService.createRewards(createTestRewards(user = user, rewardPoint = 200)) }
                    verify(exactly = 0) { userCommandService.decreaseCoin(user.id, 200) }
                    verify(exactly = 0) { searchCoinCacheRepository.save(any()) }
                    result shouldBe createFoodSpotsSearchResponses()
                }
            }

            `when`("500m 거리 이내 가게 검색 조회를 하면") {
                val query500m =
                    createFoodSpotsSearchCondition(
                        0.0,
                        0.0,
                        500,
                    )
                val user = createTestUser(coin = 1000)
                every { userCommandService.decreaseCoin(any(), any()) } returns Unit
                every { foodSpotsQueryService.searchFoodSpots(query500m) } returns createFoodSpotsSearchResponses()
                then("코인 소모와 캐싱 없이 결과를 반환한다.") {
                    val result = foodSpotsSearchService.searchFoodSpots(user, query500m)
                    result shouldBe createFoodSpotsSearchResponses()
                    verify(exactly = 0) { searchCoinCacheRepository.save(any()) }
                    verify(exactly = 0) { rewardsCommandService.createRewards(createTestRewards(user = user, rewardPoint = 200)) }
                    verify(exactly = 0) { userCommandService.decreaseCoin(any(), any()) }
                }
            }
        }

        given("getPopularSearches 테스트") {
            val keywords = createFoodSpotsSearchKeywords().joinToString(":")
            val cache = mockk<Cache>()
            every { cacheManager.getCache(any()) } returns cache
            every { cache.get(any(), String::class.java) } returns keywords
            `when`("로컬 캐시의 인기 검색어 조회 요청시") {
                then("로컬에 캐시된 인기 검색어 리스트를 반환한다.") {
                    val result = foodSpotsSearchService.getPopularSearches()
                    result shouldBe createFoodSpotsPopularSearchesResponse()
                }
            }

            `when`("레디스의 인기 검색어 조회 요청시") {
                every { cacheManager.getCache(any()) } returns null
                every {
                    redisTemplate.opsForValue().get(any())
                } returns keywords
                then("레디스에 캐시된 인기 검색어 리스트를 반환한다.") {
                    val result = foodSpotsSearchService.getPopularSearches()
                    result shouldBe createFoodSpotsPopularSearchesResponse()
                }
            }

            `when`("인기 검색어 조회 요청시 레디스에 캐시된 값이 없으면") {
                every { cacheManager.getCache(any()) } returns null
                every { redisTemplate.opsForValue().get(any()) } returns null
                every { executor.execute(any()) } just runs
                then("캐시를 하고 예외를 발생시킨다") {
                    shouldThrow<RoadyFoodyBadRequestException> { foodSpotsSearchService.getPopularSearches() }
                }
            }
        }
    })
