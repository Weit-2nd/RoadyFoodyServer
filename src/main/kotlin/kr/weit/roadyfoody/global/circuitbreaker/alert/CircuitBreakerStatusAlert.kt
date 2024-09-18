package kr.weit.roadyfoody.global.circuitbreaker.alert

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnStateTransitionEvent
import io.sentry.Sentry
import io.sentry.SentryLevel
import org.springframework.stereotype.Component

@Component
class CircuitBreakerStatusAlert(
    circuitBreakerRegistry: CircuitBreakerRegistry,
) {
    init {
        val circuitBreaker = circuitBreakerRegistry.circuitBreaker("redisCircuitBreaker")

        circuitBreaker.eventPublisher
            .onStateTransition { event: CircuitBreakerOnStateTransitionEvent ->
                handleStateTransition(event)
            }
    }

    fun handleStateTransition(event: CircuitBreakerOnStateTransitionEvent) {
        val state = event.stateTransition.toState

        if (state != CircuitBreaker.State.CLOSED && state != CircuitBreaker.State.OPEN) {
            return
        }

        val circuitBreakerName = event.circuitBreakerName
        val sentryLevel =
            if (state == CircuitBreaker.State.OPEN) {
                SentryLevel.ERROR
            } else {
                SentryLevel.INFO
            }

        sendSentryAlert(circuitBreakerName, state, sentryLevel)
    }

    fun sendSentryAlert(
        circuitBreakerName: String,
        state: CircuitBreaker.State,
        level: SentryLevel,
    ) {
        Sentry.withScope { scope ->
            scope.level = level
            scope.setTag("CircuitBreaker", circuitBreakerName)
            Sentry.captureMessage("$circuitBreakerName 의 상태가 $state 로 변경되었습니다.")
        }
    }
}
