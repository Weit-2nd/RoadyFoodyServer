package kr.weit.roadyfoody.ranking.application.service

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsHistoryRepository
import kr.weit.roadyfoody.ranking.fixture.createUserRankingResponse
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
            val rankingCommandService =
                RankingCommandService(
                    redisTemplate,
                    redissonClient,
                    foodSpotsHistoryRepository,
                    reviewRepository,
                )

            val lock = mockk<RLock>()
            val list = mockk<ListOperations<String, String>>()

            given("updateReportRanking 테스트") {
                every { redissonClient.getLock(any<String>()) } returns lock
                afterEach { clearMocks(foodSpotsHistoryRepository) }

                `when`("Lock을 획득한 경우") {
                    every { lock.tryLock(0, 10, TimeUnit.MINUTES) } returns true

                    every { redisTemplate.delete("rofo:user-report-ranking") } returns true
                    every { foodSpotsHistoryRepository.findAllUserReportCount() } returns createUserRankingResponse()
                    every { redisTemplate.opsForList() } returns list
                    every { list.rightPushAll(any(), *anyVararg()) } returns 1L

                    then("레디스의 데이터가 정상적으로 업데이트된다.") {
                        rankingCommandService.updateReportRanking()
                        verify(exactly = 1) { foodSpotsHistoryRepository.findAllUserReportCount() }
                    }
                }

                `when`("Lock을 획득하지 못한 경우") {
                    every { lock.tryLock(0, 10, TimeUnit.MINUTES) } returns false

                    then("레디스의 데이터가 업데이트되지 않는다.") {
                        rankingCommandService.updateReportRanking()
                        verify(exactly = 0) { foodSpotsHistoryRepository.findAllUserReportCount() }
                    }
                }
            }

            given("updateReviewRanking 테스트") {
                every { redissonClient.getLock(any<String>()) } returns lock
                afterEach { clearMocks(reviewRepository) }

                `when`("Lock을 획득한 경우") {
                    every { lock.tryLock(0, 10, TimeUnit.MINUTES) } returns true

                    every { redisTemplate.delete("rofo:user-review-ranking") } returns true
                    every { reviewRepository.findAllUserReviewCount() } returns createUserRankingResponse()
                    every { redisTemplate.opsForList() } returns list
                    every { list.rightPushAll(any(), *anyVararg()) } returns 1L

                    then("레디스의 데이터가 정상적으로 업데이트된다.") {
                        rankingCommandService.updateReviewRanking()
                        verify(exactly = 1) { reviewRepository.findAllUserReviewCount() }
                    }
                }

                `when`("Lock을 획득하지 못한 경우") {
                    every { lock.tryLock(0, 10, TimeUnit.MINUTES) } returns false

                    then("레디스의 데이터가 업데이트되지 않는다.") {
                        rankingCommandService.updateReviewRanking()
                        verify(exactly = 0) { reviewRepository.findAllUserReviewCount() }
                    }
                }
            }

            given("updateLikeRanking 테스트") {
                every { redissonClient.getLock(any<String>()) } returns lock
                afterEach { clearMocks(reviewRepository) }

                `when`("Lock을 획득한 경우") {
                    every { lock.tryLock(0, 10, TimeUnit.MINUTES) } returns true

                    every { redisTemplate.delete("rofo:user-like-ranking") } returns true
                    every { reviewRepository.findAllUserLikeCount() } returns createUserRankingResponse()
                    every { redisTemplate.opsForList() } returns list
                    every { list.rightPushAll(any(), *anyVararg()) } returns 1L

                    then("레디스의 데이터가 정상적으로 업데이트된다.") {
                        rankingCommandService.updateLikeRanking()
                        verify(exactly = 1) { reviewRepository.findAllUserLikeCount() }
                    }
                }

                `when`("Lock을 획득하지 못한 경우") {
                    every { lock.tryLock(0, 10, TimeUnit.MINUTES) } returns false

                    then("레디스의 데이터가 업데이트되지 않는다.") {
                        rankingCommandService.updateLikeRanking()
                        verify(exactly = 0) { reviewRepository.findAllUserLikeCount() }
                    }
                }
            }
        },
    )
