package kr.weit.roadyfoody.ranking.application.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsHistoryRepository
import kr.weit.roadyfoody.ranking.exception.RankingNotFoundException
import kr.weit.roadyfoody.ranking.fixture.createUserRankingResponse
import kr.weit.roadyfoody.review.repository.FoodSpotsReviewRepository
import org.springframework.data.redis.core.ListOperations
import org.springframework.data.redis.core.RedisTemplate

class RankingQueryServiceTest :
    BehaviorSpec(
        {

            val redisTemplate = mockk<RedisTemplate<String, String>>()
            val foodSpotsHistoryRepository = mockk<FoodSpotsHistoryRepository>()
            val reviewRepository = mockk<FoodSpotsReviewRepository>()
            val rankingCommandService = mockk<RankingCommandService>()
            val rankingQueryService =
                RankingQueryService(redisTemplate, foodSpotsHistoryRepository, reviewRepository, rankingCommandService)

            val listOperation = mockk<ListOperations<String, String>>()
            val list = listOf("user1:10", "user2:20", "user3:15")

            afterEach { clearMocks(reviewRepository) }
            afterEach { clearMocks(listOperation) }

            given("getReportRanking 테스트") {
                `when`("레디스의 데이터를 조회한 경우") {
                    every { redisTemplate.opsForList() } returns listOperation
                    every { listOperation.range(any(), any(), any()) } returns list

                    then("리포트 랭킹이 조회된다.") {
                        rankingQueryService.getReportRanking(10)
                        verify(exactly = 1) { listOperation.range(any(), any(), any()) }
                    }
                }

                `when`("레디스의 데이터가 조회가 안되는 경우") {
                    every { listOperation.range(any(), any(), any()) } returns listOf()
                    every { listOperation.rightPushAll(any(), any<List<String>>()) } returns 1L
                    every { foodSpotsHistoryRepository.findAllUserReportCount() } returns createUserRankingResponse()

                    then("예외가 발생한다.") {
                        shouldThrow<RankingNotFoundException> { rankingQueryService.getReportRanking(10) }
                    }
                }
            }

            given("getReviewRanking 테스트") {
                `when`("레디스의 데이터를 조회한 경우") {
                    every { redisTemplate.opsForList() } returns listOperation
                    every { listOperation.range(any(), any(), any()) } returns list

                    then("리뷰 랭킹이 조회된다.") {
                        rankingQueryService.getReviewRanking(10)
                        verify(exactly = 1) { listOperation.range(any(), any(), any()) }
                    }
                }

                `when`("레디스의 데이터가 조회가 안되는 경우") {
                    every { redisTemplate.opsForList() } returns listOperation
                    every { listOperation.range(any(), any(), any()) } returns listOf()
                    every { listOperation.rightPushAll(any(), any<List<String>>()) } returns 1L
                    every { reviewRepository.findAllUserReviewCount() } returns createUserRankingResponse()

                    then("예외가 발생한다.") {
                        shouldThrow<RankingNotFoundException> { rankingQueryService.getReviewRanking(10) }
                    }
                }
            }

            given("getLikeRanking 테스트") {
                `when`("레디스의 데이터를 조회한 경우") {
                    every { redisTemplate.opsForList() } returns listOperation
                    every { listOperation.range(any(), any(), any()) } returns list

                    then("리뷰 랭킹이 조회된다.") {
                        rankingQueryService.getLikeRanking(10)
                        verify(exactly = 1) { listOperation.range(any(), any(), any()) }
                    }
                }

                `when`("레디스의 데이터가 null인 경우") {
                    every { redisTemplate.opsForList() } returns listOperation
                    every { listOperation.range(any(), any(), any()) } returns null
                    every { listOperation.rightPushAll(any(), any<List<String>>()) } returns 1L
                    every { reviewRepository.findAllUserLikeCount() } returns createUserRankingResponse()

                    then("예외가 발생한다.") {
                        shouldThrow<RankingNotFoundException> { rankingQueryService.getLikeRanking(10) }
                    }
                }

                `when`("레디스의 데이터가 빈값인 경우") {
                    every { redisTemplate.opsForList() } returns listOperation
                    every { listOperation.range(any(), any(), any()) } returns listOf()
                    every { listOperation.rightPushAll(any(), any<List<String>>()) } returns 1L
                    every { reviewRepository.findAllUserLikeCount() } returns createUserRankingResponse()

                    then("예외가 발생한다.") {
                        shouldThrow<RankingNotFoundException> { rankingQueryService.getLikeRanking(10) }
                    }
                }
            }
        },
    )
