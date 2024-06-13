package kr.weit.roadyfoody.global.client

import kr.weit.roadyfoody.common.exception.RetriesExceededException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse

class RetryClientHttpRequestInterceptor : ClientHttpRequestInterceptor {
    companion object {
        private const val ATTEMPTS = 3
        private const val ZERO = 0
    }

    private val log: Logger = LoggerFactory.getLogger(RetryClientHttpRequestInterceptor::class.java)

    private val retryableStatus = setOf(HttpStatus.TOO_MANY_REQUESTS)

    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution,
    ): ClientHttpResponse {
        for (i in ZERO until ATTEMPTS) {
            val response = execution.execute(request, body)
            if (!retryableStatus.contains(response.statusCode)) {
                // Todo. ES에 사용하기 위해 json 형태 필요
                log.info("Successful attempt: $i")
                return response
            }
        }
        log.error("Retries exceeded")
        throw RetriesExceededException()
    }
}
