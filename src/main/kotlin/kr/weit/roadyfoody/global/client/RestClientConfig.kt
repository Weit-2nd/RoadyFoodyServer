package kr.weit.roadyfoody.global.client

import kr.weit.roadyfoody.common.exception.RestClientException
import kr.weit.roadyfoody.test.application.client.TestClientInterface
import kr.weit.roadyfoody.tourism.presentation.client.TourismClientInterface
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.web.client.ClientHttpRequestFactories
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatusCode
import org.springframework.http.client.JdkClientHttpRequestFactory
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import org.springframework.web.util.DefaultUriBuilderFactory
import java.time.Duration

@Configuration
class RestClientConfig {
    companion object {
        private const val CONNECT_TIME = 1L
        private const val READ_TIME = 5L
        private const val TEST_URL = "https://jsonplaceholder.typicode.com"
        private const val TOURISM_URL = "http://apis.data.go.kr/B551011/KorService1"
    }

    private val log: Logger = LoggerFactory.getLogger(RestClientConfig::class.java)

    @Bean
    fun testClientInterface(): TestClientInterface {
        return creatClient(TEST_URL, TestClientInterface::class.java)
    }

    @Bean
    fun tourismClientInterface(): TourismClientInterface {
        return creatClient(TOURISM_URL, TourismClientInterface::class.java)
    }

    private fun <T> creatClient(
        baseUrl: String,
        clientClass: Class<T>,
    ): T {
        // Todo. 가상 쓰레드를 사용하기 위해 API 비동기화를 위한 설정 추가 필요
        val restClient =
            RestClient.builder()
                .uriBuilderFactory(defaultUriBuilderFactory(baseUrl))
                .requestFactory(clientHttpRequestFactory())
                .requestInterceptor(RetryClientHttpRequestInterceptor())
                .defaultStatusHandler(
                    HttpStatusCode::is4xxClientError,
                ) { _, response ->
                    log.error("Client Error Code={}", response.statusCode)
                    log.error("Client Error Message={}", String(response.body.readAllBytes()))

                    throw RestClientException()
                }
                .defaultStatusHandler(
                    HttpStatusCode::is5xxServerError,
                ) { _, response ->
                    log.error("Server Error Code={}", response.statusCode)
                    log.error("Server Error Message={}", String(response.body.readAllBytes()))

                    throw RestClientException()
                }
                .build()

        val adapter = RestClientAdapter.create(restClient)
        val factory = HttpServiceProxyFactory.builderFor(adapter).build()

        return factory.createClient(clientClass)
    }

    private fun clientHttpRequestFactory(): JdkClientHttpRequestFactory {
        val requestSettings: ClientHttpRequestFactorySettings =
            ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(CONNECT_TIME))
                .withReadTimeout(Duration.ofSeconds(READ_TIME))
        val jdkClientHttpRequestFactory: JdkClientHttpRequestFactory =
            ClientHttpRequestFactories.get(
                JdkClientHttpRequestFactory::class.java,
                requestSettings,
            )
        return jdkClientHttpRequestFactory
    }

    private fun defaultUriBuilderFactory(baseUrl: String): DefaultUriBuilderFactory {
        val uriBuilderFactory =
            DefaultUriBuilderFactory(baseUrl)
        uriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE)
        return uriBuilderFactory
    }
}
