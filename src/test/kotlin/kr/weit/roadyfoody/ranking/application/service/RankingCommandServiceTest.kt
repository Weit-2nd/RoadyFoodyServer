package kr.weit.roadyfoody.ranking.application.service

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsHistoryRepository
import kr.weit.roadyfoody.global.cache.CachePublisher
import kr.weit.roadyfoody.ranking.fixture.createUserRanking
import kr.weit.roadyfoody.ranking.utils.LIKE_RANKING_KEY
import kr.weit.roadyfoody.ranking.utils.REPORT_RANKING_KEY
import kr.weit.roadyfoody.ranking.utils.REVIEW_RANKING_KEY
import kr.weit.roadyfoody.ranking.utils.TOTAL_RANKING_KEY
import kr.weit.roadyfoody.review.repository.FoodSpotsReviewRepository
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.data.redis.core.ListOperations
import org.springframework.data.redis.core.RedisTemplate
import java.util.concurrent.TimeUnit

class RankingCommandServiceTest :
    BehaviorSpec(
        {
            val redisTemplate = mockk<RedisTemplate<String, String>>()
            val redissonClient = mockk<RedissonClient>()
            val foodSpotsHistoryRepository = mockk<FoodSpotsHistoryRepository>()
            val reviewRepository = mockk<FoodSpotsReviewRepository>()
            val cachePublisher = mockk<CachePublisher>()
            val rankingCommandService =
                RankingCommandService(
                    redisTemplate,
                    redissonClient,
                    foodSpotsHistoryRepository,
                    reviewRepository,
                    cachePublisher,
                )

            val lock = mockk<RLock>()
            val list = mockk<ListOperations<String, String>>()

            given("updateReportRanking 테스트") {
                every { redissonClient.getLock(any<String>()) } returns lock
                afterEach { clearMocks(foodSpotsHistoryRepository) }
                afterEach { clearMocks(cachePublisher) }

                `when`("Lock을 획득한 경우") {
                    every { lock.tryLock(0, 10, TimeUnit.MINUTES) } returns true

                    every { redisTemplate.delete(REPORT_RANKING_KEY) } returns true
                    every { foodSpotsHistoryRepository.findAllUserReportCount() } returns createUserRanking()
                    every { redisTemplate.opsForList() } returns list
                    every { list.rightPushAll(any(), any<List<String>>()) } returns 1L
                    every { cachePublisher.publishCacheUpdate(any(), any()) } returns Unit

                    then("레디스의 데이터가 정상적으로 업데이트된다.") {
                        rankingCommandService.updateReportRanking()
                        verify(exactly = 1) { foodSpotsHistoryRepository.findAllUserReportCount() }
                        verify(exactly = 1) { cachePublisher.publishCacheUpdate(any(), any()) }
                    }
                }

                `when`("Lock을 획득하지 못한 경우") {
                    every { lock.tryLock(0, 10, TimeUnit.MINUTES) } returns false

                    then("레디스의 데이터가 업데이트되지 않는다.") {
                        rankingCommandService.updateReportRanking()
                        verify(exactly = 0) { foodSpotsHistoryRepository.findAllUserReportCount() }
                        verify(exactly = 0) { cachePublisher.publishCacheUpdate(any(), any()) }
                    }
                }
            }

            given("updateReviewRanking 테스트") {
                every { redissonClient.getLock(any<String>()) } returns lock
                afterEach { clearMocks(reviewRepository) }
                afterEach { clearMocks(cachePublisher) }

                `when`("Lock을 획득한 경우") {
                    every { lock.tryLock(0, 10, TimeUnit.MINUTES) } returns true

                    every { redisTemplate.delete(REVIEW_RANKING_KEY) } returns true
                    every { reviewRepository.findAllUserReviewCount() } returns createUserRanking()
                    every { redisTemplate.opsForList() } returns list
                    every { list.rightPushAll(any(), any<List<String>>()) } returns 1L
                    every { cachePublisher.publishCacheUpdate(any(), any()) } returns Unit

                    then("레디스의 데이터가 정상적으로 업데이트된다.") {
                        rankingCommandService.updateReviewRanking()
                        verify(exactly = 1) { reviewRepository.findAllUserReviewCount() }
                        verify(exactly = 1) { cachePublisher.publishCacheUpdate(any(), any()) }
                    }
                }

                `when`("Lock을 획득하지 못한 경우") {
                    every { lock.tryLock(0, 10, TimeUnit.MINUTES) } returns false

                    then("레디스의 데이터가 업데이트되지 않는다.") {
                        rankingCommandService.updateReviewRanking()
                        verify(exactly = 0) { reviewRepository.findAllUserReviewCount() }
                        verify(exactly = 0) { cachePublisher.publishCacheUpdate(any(), any()) }
                    }
                }
            }

            given("updateLikeRanking 테스트") {
                every { redissonClient.getLock(any<String>()) } returns lock
                afterEach { clearMocks(reviewRepository) }
                afterEach { clearMocks(cachePublisher) }

                `when`("Lock을 획득한 경우") {
                    every { lock.tryLock(0, 10, TimeUnit.MINUTES) } returns true

                    every { redisTemplate.delete(LIKE_RANKING_KEY) } returns true
                    every { reviewRepository.findAllUserLikeCount() } returns createUserRanking()
                    every { redisTemplate.opsForList() } returns list
                    every { list.rightPushAll(any(), any<List<String>>()) } returns 1L
                    every { cachePublisher.publishCacheUpdate(any(), any()) } returns Unit

                    then("레디스의 데이터가 정상적으로 업데이트된다.") {
                        rankingCommandService.updateLikeRanking()
                        verify(exactly = 1) { reviewRepository.findAllUserLikeCount() }
                        verify(exactly = 1) { cachePublisher.publishCacheUpdate(any(), any()) }
                    }
                }

                `when`("Lock을 획득하지 못한 경우") {
                    every { lock.tryLock(0, 10, TimeUnit.MINUTES) } returns false

                    then("레디스의 데이터가 업데이트되지 않는다.") {
                        rankingCommandService.updateLikeRanking()
                        verify(exactly = 0) { reviewRepository.findAllUserLikeCount() }
                        verify(exactly = 0) { cachePublisher.publishCacheUpdate(any(), any()) }
                    }
                }
            }

            given("updateTotalRanking 테스트") {
                every { redissonClient.getLock(any<String>()) } returns lock
                afterEach { clearMocks(reviewRepository) }
                afterEach { clearMocks(cachePublisher) }

                `when`("Lock을 획득한 경우") {
                    every { lock.tryLock(0, 10, TimeUnit.MINUTES) } returns true

                    every { redisTemplate.delete(TOTAL_RANKING_KEY) } returns true
                    every { reviewRepository.findAllUserTotalCount() } returns createUserRanking()
                    every { redisTemplate.opsForList() } returns list
                    every { list.rightPushAll(any(), any<List<String>>()) } returns 1L
                    every { cachePublisher.publishCacheUpdate(any(), any()) } returns Unit

                    then("레디스의 데이터가 정상적으로 업데이트된다.") {
                        rankingCommandService.updateTotalRanking()
                        verify(exactly = 1) { reviewRepository.findAllUserTotalCount() }
                        verify(exactly = 1) { cachePublisher.publishCacheUpdate(any(), any()) }
                    }
                }

                `when`("Lock을 획득하지 못한 경우") {
                    every { lock.tryLock(0, 10, TimeUnit.MINUTES) } returns false

                    then("레디스의 데이터가 업데이트되지 않는다.") {
                        rankingCommandService.updateTotalRanking()
                        verify(exactly = 0) { reviewRepository.findAllUserTotalCount() }
                        verify(exactly = 0) { cachePublisher.publishCacheUpdate(any(), any()) }
                    }
                }
            }
        },
    )
