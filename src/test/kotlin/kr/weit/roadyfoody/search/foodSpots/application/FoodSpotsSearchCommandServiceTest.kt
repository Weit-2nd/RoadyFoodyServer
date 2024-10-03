package kr.weit.roadyfoody.search.foodSpots.application

import POPULAR_SEARCH_KEY
import POPULAR_SEARCH_TOPIC
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kr.weit.roadyfoody.global.cache.CachePublisher
import kr.weit.roadyfoody.search.foodSpots.application.service.FoodSpotsSearchCommandService
import kr.weit.roadyfoody.search.foodSpots.fixture.createFoodSpotsSearchKeywords
import kr.weit.roadyfoody.search.foodSpots.repository.FoodSpotsSearchHistoryRepository
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import java.time.Duration

class FoodSpotsSearchCommandServiceTest :
    BehaviorSpec({
        val foodSpotsSearchHistoryRepository = mockk<FoodSpotsSearchHistoryRepository>()
        val redisTemplate = mockk<RedisTemplate<String, String>>()
        val cachePublisher = mockk<CachePublisher>()
        val foodSpotsSearchCommandService =
            FoodSpotsSearchCommandService(
                foodSpotsSearchHistoryRepository,
                redisTemplate,
                cachePublisher,
            )

        afterEach { clearAllMocks() }

        given("updatePopularSearchesCache") {
            `when`("인기 검색어가 존재할 때") {
                val popularSearches = createFoodSpotsSearchKeywords().joinToString(":")
                every { foodSpotsSearchHistoryRepository.getRecentPopularSearches() } returns createFoodSpotsSearchKeywords()
                every {
                    redisTemplate.opsForValue().set(
                        POPULAR_SEARCH_KEY,
                        popularSearches,
                        Duration.ofHours(1).plusMinutes(1),
                    )
                } just runs
                every {
                    cachePublisher.publishCacheUpdate(
                        ChannelTopic.of(POPULAR_SEARCH_TOPIC),
                        POPULAR_SEARCH_KEY,
                    )
                } just runs
                then("인기 검색어를 업데이트한다.") {
                    foodSpotsSearchCommandService.updatePopularSearchesCache()
                    verify(exactly = 1) {
                        foodSpotsSearchHistoryRepository.getRecentPopularSearches()
                        redisTemplate.opsForValue().set(
                            POPULAR_SEARCH_KEY,
                            popularSearches,
                            Duration.ofHours(1).plusMinutes(1),
                        )
                        cachePublisher.publishCacheUpdate(
                            ChannelTopic.of(POPULAR_SEARCH_TOPIC),
                            POPULAR_SEARCH_KEY,
                        )
                    }
                }
            }

            `when`("인기 검색어가 존재하지 않을 때") {
                every { foodSpotsSearchHistoryRepository.getRecentPopularSearches() } returns emptyList()
                then("인기 검색어를 업데이트하지 않는다.") {
                    foodSpotsSearchCommandService.updatePopularSearchesCache()
                    verify(exactly = 1) { foodSpotsSearchHistoryRepository.getRecentPopularSearches() }
                    verify(exactly = 0) {
                        cachePublisher.publishCacheUpdate(
                            ChannelTopic.of(
                                POPULAR_SEARCH_TOPIC,
                            ),
                            POPULAR_SEARCH_KEY,
                        )
                    }
                }
            }
        }
    })
