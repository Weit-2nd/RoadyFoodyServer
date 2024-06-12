package kr.weit.roadyfoody.global.client

import kr.weit.roadyfoody.common.exception.RetriesExceededException
import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import java.net.http.HttpRequest

class RetryClientHttpRequestInterceptor : ClientHttpRequestInterceptor {
    private val attempts = 3
    private val retryableStatus = setOf(HttpStatus.TOO_MANY_REQUESTS)

    override fun intercept(
        request: org.springframework.http.HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution,
    ): ClientHttpResponse {
        for (i in 0 until attempts) {
            val response = execution.execute(request, body)
            if (!retryableStatus.contains(response.statusCode)) {
                // Todo 로그 추가 필요할까요?
                return response
            }
        }
        throw RetriesExceededException()
    }
}
