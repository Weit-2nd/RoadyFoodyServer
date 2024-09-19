package kr.weit.roadyfoody.global.circuitbreaker.alert

import io.github.resilience4j.circuitbreaker.CircuitBreaker.State
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnStateTransitionEvent
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify

class CircuitBreakerStatusAlertTest :
    BehaviorSpec({
        val circuitBreakerStatusAlert = spyk(CircuitBreakerStatusAlert(mockk<CircuitBreakerRegistry>(relaxed = true)))
        val event = mockk<CircuitBreakerOnStateTransitionEvent>(relaxed = true)

        afterEach { clearMocks(circuitBreakerStatusAlert) }

        given("handleStateTransition 테스트") {
            `when`("OPEN 상태로 변경될 시") {
                every { event.stateTransition.toState } returns State.OPEN

                circuitBreakerStatusAlert.handleStateTransition(event)

                then("Sentry 에 알림을 전송한다.") {
                    verify(exactly = 1) { circuitBreakerStatusAlert.sendSentryAlert(any(), State.OPEN, any()) }
                }
            }

            `when`("CLOSED 상태로 변경될 시") {
                every { event.stateTransition.toState } returns State.CLOSED

                circuitBreakerStatusAlert.handleStateTransition(event)

                then("Sentry 에 알림을 전송한다.") {
                    verify(exactly = 1) { circuitBreakerStatusAlert.sendSentryAlert(any(), State.CLOSED, any()) }
                }
            }

            `when`("그 외 상태로 변경될 시") {
                forAll(
                    row(State.DISABLED),
                    row(State.HALF_OPEN),
                    row(State.FORCED_OPEN),
                    row(State.DISABLED),
                ) { state ->
                    every { event.stateTransition.toState } returns state

                    circuitBreakerStatusAlert.handleStateTransition(event)

                    then("Sentry 에 알림을 전송하지 않는다.") {
                        verify(exactly = 0) { circuitBreakerStatusAlert.sendSentryAlert(any(), state, any()) }
                    }
                }
            }
        }
    })
