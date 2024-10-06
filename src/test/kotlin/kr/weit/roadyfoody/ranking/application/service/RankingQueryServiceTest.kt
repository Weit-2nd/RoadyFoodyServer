package kr.weit.roadyfoody.ranking.application.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsHistoryRepository
import kr.weit.roadyfoody.global.TEST_SIZE
import kr.weit.roadyfoody.global.TEST_START_INDEX
import kr.weit.roadyfoody.global.service.ImageService
import kr.weit.roadyfoody.ranking.exception.RankingNotFoundException
import kr.weit.roadyfoody.ranking.fixture.createUserRanking
import kr.weit.roadyfoody.review.repository.FoodSpotsReviewRepository
import kr.weit.roadyfoody.user.fixture.TEST_USER_PROFILE_IMAGE_URL
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.data.redis.core.ListOperations
import org.springframework.data.redis.core.RedisTemplate
import java.util.concurrent.ExecutorService

class RankingQueryServiceTest :
    BehaviorSpec(
        {
            val redisTemplate = mockk<RedisTemplate<String, String>>()
            val foodSpotsHistoryRepository = mockk<FoodSpotsHistoryRepository>()
            val reviewRepository = mockk<FoodSpotsReviewRepository>()
            val rankingCommandService = mockk<RankingCommandService>()
            val executor = mockk<ExecutorService>()
            val cacheManager = mockk<CacheManager>()
            val imageService = mockk<ImageService>()
            val rankingQueryService =
                RankingQueryService(
                    redisTemplate,
                    foodSpotsHistoryRepository,
                    reviewRepository,
                    rankingCommandService,
                    executor,
                    cacheManager,
                    imageService,
                )

            val listOperation = mockk<ListOperations<String, String>>()
            val list = listOf("1:user2:2:null:20", "2:user3:3:test_image_name_3:15", "3:user1:1:test_image_name_1:10")
            val cache = mockk<Cache>()

            afterEach { clearMocks(reviewRepository) }
            afterEach { clearMocks(listOperation) }
            afterEach { clearMocks(rankingCommandService) }
            afterEach { clearMocks(imageService) }
            afterEach { clearMocks(imageService) }
            every { executor.execute(any()) } answers {
                firstArg<Runnable>().run()
            }

            given("getReportRanking 테스트") {
                `when`("로컬캐시의 데이터를 조회한 경우") {
                    every { cacheManager.getCache(any()) } returns cache
                    every { cache.get(any(), List::class.java) } returns list
                    every { imageService.getDownloadUrl(any()) } returns TEST_USER_PROFILE_IMAGE_URL

                    then("리포트 랭킹이 조회된다.") {
                        rankingQueryService.getReportRanking(TEST_SIZE, TEST_START_INDEX)
                        verify(exactly = 0) { listOperation.range(any(), any(), any()) }
                        verify(exactly = 2) { imageService.getDownloadUrl(any()) }
                    }
                }
                `when`("레디스의 데이터를 조회한 경우") {
                    every { cacheManager.getCache(any()) } returns cache
                    every { cache.get(any(), List::class.java) } returns null
                    every { redisTemplate.opsForList() } returns listOperation
                    every { listOperation.range(any(), any(), any()) } returns list
                    every { imageService.getDownloadUrl(any()) } returns TEST_USER_PROFILE_IMAGE_URL

                    then("리포트 랭킹이 조회된다.") {
                        rankingQueryService.getReportRanking(TEST_SIZE, TEST_START_INDEX)
                        verify(exactly = 1) { listOperation.range(any(), any(), any()) }
                        verify(exactly = 2) { imageService.getDownloadUrl(any()) }
                    }
                }

                `when`("레디스의 데이터가 조회가 안되는 경우") {
                    every { cacheManager.getCache(any()) } returns cache
                    every { cache.get(any(), List::class.java) } returns null
                    every { listOperation.range(any(), any(), any()) } returns listOf()
                    every { listOperation.rightPushAll(any(), any<List<String>>()) } returns 1L
                    every { foodSpotsHistoryRepository.findAllUserReportCount() } returns createUserRanking()

                    then("예외가 발생한다.") {
                        shouldThrow<RankingNotFoundException> { rankingQueryService.getReportRanking(TEST_SIZE, TEST_START_INDEX) }
                    }
                }
            }

            given("getReviewRanking 테스트") {
                `when`("로컬캐시의 데이터를 조회한 경우") {
                    every { cacheManager.getCache(any()) } returns cache
                    every { cache.get(any(), List::class.java) } returns list
                    every { imageService.getDownloadUrl(any()) } returns TEST_USER_PROFILE_IMAGE_URL

                    then("리뷰 랭킹이 조회된다.") {
                        rankingQueryService.getReviewRanking(TEST_SIZE, TEST_START_INDEX)
                        verify(exactly = 0) { listOperation.range(any(), any(), any()) }
                        verify(exactly = 2) { imageService.getDownloadUrl(any()) }
                    }
                }

                `when`("레디스의 데이터를 조회한 경우") {
                    every { cacheManager.getCache(any()) } returns cache
                    every { cache.get(any(), List::class.java) } returns null
                    every { redisTemplate.opsForList() } returns listOperation
                    every { listOperation.range(any(), any(), any()) } returns list
                    every { imageService.getDownloadUrl(any()) } returns TEST_USER_PROFILE_IMAGE_URL

                    then("리뷰 랭킹이 조회된다.") {
                        rankingQueryService.getReviewRanking(TEST_SIZE, TEST_START_INDEX)
                        verify(exactly = 1) { listOperation.range(any(), any(), any()) }
                        verify(exactly = 2) { imageService.getDownloadUrl(any()) }
                    }
                }

                `when`("레디스의 데이터가 조회가 안되는 경우") {
                    every { cacheManager.getCache(any()) } returns cache
                    every { cache.get(any(), List::class.java) } returns null
                    every { redisTemplate.opsForList() } returns listOperation
                    every { listOperation.range(any(), any(), any()) } returns listOf()
                    every { listOperation.rightPushAll(any(), any<List<String>>()) } returns 1L
                    every { reviewRepository.findAllUserReviewCount() } returns createUserRanking()

                    then("예외가 발생한다.") {
                        shouldThrow<RankingNotFoundException> { rankingQueryService.getReviewRanking(TEST_SIZE, TEST_START_INDEX) }
                    }
                }
            }

            given("getLikeRanking 테스트") {
                `when`("로컬캐시의 데이터를 조회한 경우") {
                    every { cacheManager.getCache(any()) } returns cache
                    every { cache.get(any(), List::class.java) } returns list
                    every { imageService.getDownloadUrl(any()) } returns TEST_USER_PROFILE_IMAGE_URL

                    then("좋아요 랭킹이 조회된다.") {
                        rankingQueryService.getLikeRanking(TEST_SIZE, TEST_START_INDEX)
                        verify(exactly = 0) { listOperation.range(any(), any(), any()) }
                        verify(exactly = 2) { imageService.getDownloadUrl(any()) }
                    }
                }
                `when`("레디스의 데이터를 조회한 경우") {
                    every { cacheManager.getCache(any()) } returns cache
                    every { cache.get(any(), List::class.java) } returns null
                    every { redisTemplate.opsForList() } returns listOperation
                    every { listOperation.range(any(), any(), any()) } returns list
                    every { imageService.getDownloadUrl(any()) } returns TEST_USER_PROFILE_IMAGE_URL

                    then("리뷰 랭킹이 조회된다.") {
                        rankingQueryService.getLikeRanking(TEST_SIZE, TEST_START_INDEX)
                        verify(exactly = 1) { listOperation.range(any(), any(), any()) }
                        verify(exactly = 2) { imageService.getDownloadUrl(any()) }
                    }
                }

                `when`("레디스의 데이터가 null인 경우") {
                    every { cacheManager.getCache(any()) } returns cache
                    every { cache.get(any(), List::class.java) } returns null
                    every { redisTemplate.opsForList() } returns listOperation
                    every { listOperation.range(any(), any(), any()) } returns null
                    every { listOperation.rightPushAll(any(), any<List<String>>()) } returns 1L
                    every { reviewRepository.findAllUserLikeCount() } returns createUserRanking()

                    then("예외가 발생한다.") {
                        shouldThrow<RankingNotFoundException> { rankingQueryService.getLikeRanking(TEST_SIZE, TEST_START_INDEX) }
                    }
                }

                `when`("레디스의 데이터가 빈값인 경우") {
                    every { cacheManager.getCache(any()) } returns cache
                    every { cache.get(any(), List::class.java) } returns null
                    every { redisTemplate.opsForList() } returns listOperation
                    every { listOperation.range(any(), any(), any()) } returns listOf()
                    every { listOperation.rightPushAll(any(), any<List<String>>()) } returns 1L
                    every { reviewRepository.findAllUserLikeCount() } returns createUserRanking()

                    then("예외가 발생한다.") {
                        shouldThrow<RankingNotFoundException> { rankingQueryService.getLikeRanking(TEST_SIZE, TEST_START_INDEX) }
                    }
                }
            }
            given("getTotalRanking 테스트") {
                `when`("로컬캐시의 데이터를 조회한 경우") {
                    every { cacheManager.getCache(any()) } returns cache
                    every { cache.get(any(), List::class.java) } returns list
                    every { imageService.getDownloadUrl(any()) } returns TEST_USER_PROFILE_IMAGE_URL

                    then("종합 랭킹이 조회된다.") {
                        rankingQueryService.getTotalRanking(TEST_SIZE, TEST_START_INDEX)
                        verify(exactly = 0) { listOperation.range(any(), any(), any()) }
                        verify(exactly = 2) { imageService.getDownloadUrl(any()) }
                    }
                }

                `when`("레디스의 데이터를 조회한 경우") {
                    every { cacheManager.getCache(any()) } returns cache
                    every { cache.get(any(), List::class.java) } returns null
                    every { redisTemplate.opsForList() } returns listOperation
                    every { listOperation.range(any(), any(), any()) } returns list
                    every { imageService.getDownloadUrl(any()) } returns TEST_USER_PROFILE_IMAGE_URL

                    then("종합 랭킹이 조회된다.") {
                        rankingQueryService.getTotalRanking(TEST_SIZE, TEST_START_INDEX)
                        verify(exactly = 1) { listOperation.range(any(), any(), any()) }
                        verify(exactly = 2) { imageService.getDownloadUrl(any()) }
                    }
                }

                `when`("레디스의 데이터가 null인 경우") {
                    every { cacheManager.getCache(any()) } returns cache
                    every { cache.get(any(), List::class.java) } returns null
                    every { redisTemplate.opsForList() } returns listOperation
                    every { listOperation.range(any(), any(), any()) } returns null
                    every { listOperation.rightPushAll(any(), any<List<String>>()) } returns 1L
                    every { reviewRepository.findAllUserTotalCount() } returns createUserRanking()
                    every {
                        rankingCommandService.updateRanking(
                            any(),
                            any(),
                            any(),
                        )
                    } just Runs
                    then("예외가 발생한다.") {

                        shouldThrow<RankingNotFoundException> { rankingQueryService.getTotalRanking(TEST_SIZE, TEST_START_INDEX) }

                        verify(exactly = 1) {
                            rankingCommandService.updateRanking(
                                any(),
                                any(),
                                any(),
                            )
                        }
                    }
                }

                `when`("레디스의 데이터가 빈값인 경우") {
                    every { cacheManager.getCache(any()) } returns cache
                    every { cache.get(any(), List::class.java) } returns null
                    every { redisTemplate.opsForList() } returns listOperation
                    every { listOperation.range(any(), any(), any()) } returns listOf()
                    every { listOperation.rightPushAll(any(), any<List<String>>()) } returns 1L
                    every { reviewRepository.findAllUserTotalCount() } returns createUserRanking()
                    every {
                        rankingCommandService.updateRanking(
                            any(),
                            any(),
                            any(),
                        )
                    } just Runs
                    then("예외가 발생한다.") {
                        shouldThrow<RankingNotFoundException> { rankingQueryService.getTotalRanking(TEST_SIZE, TEST_START_INDEX) }
                        verify(exactly = 1) {
                            rankingCommandService.updateRanking(
                                any(),
                                any(),
                                any(),
                            )
                        }
                    }
                }
            }
        },
    )
