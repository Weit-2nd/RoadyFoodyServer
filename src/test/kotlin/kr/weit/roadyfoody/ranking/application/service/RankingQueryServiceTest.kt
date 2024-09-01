package kr.weit.roadyfoody.ranking.application.service

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations

class RankingQueryServiceTest :
    BehaviorSpec({

        val redisTemplate = mockk<RedisTemplate<String, String>>()
        val rankingQueryService = RankingQueryService(redisTemplate)

        given("getReportRanking 테스트") {
            val zSetOperations = mockk<ZSetOperations<String, String>>()
            val typedTupleSet =
                setOf(
                    ZSetOperations.TypedTuple.of("user1", 10.0),
                    ZSetOperations.TypedTuple.of("user2", 20.0),
                )

            `when`("레디스의 데이터를 조회한 경우") {
                every { redisTemplate.opsForZSet() } returns zSetOperations
                every { zSetOperations.reverseRangeWithScores(any(), any(), any()) } returns typedTupleSet

                then("리포트 랭킹이 조회된다.") {
                    rankingQueryService.getReportRanking(10)
                    verify(exactly = 1) { zSetOperations.reverseRangeWithScores(any(), any(), any()) }
                }
            }
        }

        given("getReviewRanking 테스트") {
            val zSetOperations = mockk<ZSetOperations<String, String>>()
            val typedTupleSet =
                setOf(
                    ZSetOperations.TypedTuple.of("user1", 10.0),
                    ZSetOperations.TypedTuple.of("user2", 20.0),
                )

            `when`("레디스의 데이터를 조회한 경우") {
                every { redisTemplate.opsForZSet() } returns zSetOperations
                every { zSetOperations.reverseRangeWithScores(any(), any(), any()) } returns typedTupleSet

                then("리뷰 랭킹이 조회된다.") {
                    rankingQueryService.getReviewRanking(10)
                    verify(exactly = 1) { zSetOperations.reverseRangeWithScores(any(), any(), any()) }
                }
            }
        }
    })
