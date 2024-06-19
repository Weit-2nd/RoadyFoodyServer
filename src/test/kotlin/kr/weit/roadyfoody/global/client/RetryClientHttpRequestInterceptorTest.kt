package kr.weit.roadyfoody.global.client

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.weit.roadyfoody.common.exception.RetriesExceededException
import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.mock.http.client.MockClientHttpResponse

class RetryClientHttpRequestInterceptorTest : BehaviorSpec({
    given("RetryClientHttpRequestInterceptor 테스트") {
        val interceptor = RetryClientHttpRequestInterceptor()
        `when`("첫 번째 시도만에 설공하는 경우") {
            val request = mockk<HttpRequest>()
            val body = ByteArray(0)
            val execution = mockk<ClientHttpRequestExecution>()
            val response = MockClientHttpResponse(ByteArray(0), HttpStatus.OK)

            every { execution.execute(request, body) } returns response

            then("다시 시도하지 않고 첫번째 결과를 반환한다.") {
                val result = interceptor.intercept(request, body, execution)
                result shouldBe response
                verify(exactly = 1) { execution.execute(request, body) }
            }
        }

        `when`("두 번째 시도만에 설공하는 경우") {
            val request = mockk<HttpRequest>()
            val body = ByteArray(0)
            val execution = mockk<ClientHttpRequestExecution>()
            val successResponse = MockClientHttpResponse(ByteArray(0), HttpStatus.OK)
            val retryableResponse = MockClientHttpResponse(ByteArray(0), HttpStatus.TOO_MANY_REQUESTS)
            every { execution.execute(request, body) } returnsMany listOf(retryableResponse, successResponse)

            then("두 번째 시도에서 성공하면 성공한다.") {
                val result = interceptor.intercept(request, body, execution)
                result shouldBe successResponse
                verify(exactly = 2) { execution.execute(request, body) }
            }
        }

        `when`("세 번째 시도까지 실패하는 경우") {
            val request = mockk<HttpRequest>()
            val body = ByteArray(0)
            val execution = mockk<ClientHttpRequestExecution>()
            val retryableResponse = MockClientHttpResponse(ByteArray(0), HttpStatus.TOO_MANY_REQUESTS)
            every { execution.execute(request, body) } returns retryableResponse

            then("세 번째 시도에서 실패하면 RetriesExceededException 던진다") {
                val exception =
                    shouldThrow<RetriesExceededException> {
                        interceptor.intercept(request, body, execution)
                    }
                exception shouldBe RetriesExceededException()
                verify(exactly = 3) { execution.execute(request, body) }
            }
        }
    }
})
