package kr.weit.roadyfoody.global.circuitbreaker

import com.ninjasquad.springmockk.SpykBean
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import io.sentry.Sentry
import kr.weit.roadyfoody.ranking.application.service.RankingQueryService
import kr.weit.roadyfoody.support.annotation.ServiceIntegrateTest
import org.redisson.client.RedisException
import org.springframework.data.redis.core.RedisTemplate

@ServiceIntegrateTest
class RedisCircuitBreakerTest(
    private val sut: RankingQueryService,
    @SpykBean private val redisTemplate: RedisTemplate<String, String>,
    private val circuitBreakerRegistry: CircuitBreakerRegistry,
) : BehaviorSpec({

        val circuitBreaker = circuitBreakerRegistry.circuitBreaker("redisCircuitBreaker")

        given("Circuit Breaker 가 닫혀있을 때") {
            beforeContainer {
                circuitBreaker.reset()
                mockkStatic(Sentry::class)
                every { Sentry.captureMessage(any<String>()) } returns mockk()
            }

            afterContainer {
                unmockkStatic(Sentry::class)
            }

            `when`("Redis 관련 예외 발생 시") {
                every { redisTemplate.opsForList() } throws RedisException()

                repeatFailingCalls(sut, times = 20)

                then("Circuit Breaker 의 상태는 OPEN 로 변경된다.") {
                    circuitBreaker.state shouldBe CircuitBreaker.State.OPEN
                }

                then("fallback 정책대로 반환합니다.") {
                    shouldNotThrow<RedisException> { sut.getReportRanking(5) }

                    sut.getReportRanking(5) shouldBe emptyList()
                }

                then("Sentry 에 알림을 전송한다.") {
                    verify(exactly = 1) { Sentry.captureMessage(any<String>()) }
                }
            }

            `when`("Redis 비관련 예외 발생 시") {
                every { redisTemplate.opsForList() } throws Exception()

                repeatFailingCalls(sut, times = 20)

                then("Circuit Breaker 의 상태는 CLOSED 이다.") {
                    circuitBreaker.state shouldBe CircuitBreaker.State.CLOSED
                }

                then("예외를 다시 던집니다.") {
                    shouldThrow<Exception> { sut.getReportRanking(5) }
                }

                then("Sentry 에 알림을 전송하지 않는다.") {
                    verify(exactly = 0) { Sentry.captureMessage(any<String>()) }
                }
            }
        }
    })

fun repeatFailingCalls(
    sut: RankingQueryService,
    times: Int,
) {
    repeat(times) {
        runCatching {
            sut.getReportRanking(5)
        }
    }
}
