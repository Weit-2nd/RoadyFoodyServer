package kr.weit.roadyfoody.global.client

import kr.weit.roadyfoody.common.exception.RestClientException
import kr.weit.roadyfoody.tourism.presentation.client.TourismClientInterface
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
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
import java.net.http.HttpClient
import java.time.Duration
import java.util.concurrent.Executors

@Configuration
class RestClientConfig {
    companion object {
        private const val CONNECT_TIME = 1L
        private const val READ_TIME = 5L
        private const val TOURISM_URL = "http://apis.data.go.kr/B551011/KorService1"
    }

    private val log: Logger = LoggerFactory.getLogger(RestClientConfig::class.java)

    @Value("\${spring.threads.virtual.enabled}")
    private val virtualThreadEnabled: Boolean = false

    @Bean
    fun tourismClientInterface(): TourismClientInterface {
        return createClient(TOURISM_URL, TourismClientInterface::class.java)
    }

    private fun <T> createClient(
        baseUrl: String,
        clientClass: Class<T>,
    ): T {
        val restClientBuilder =
            RestClient.builder()
                .uriBuilderFactory(defaultUriBuilderFactory(baseUrl))

        log.info("virtualThreadEnabled={}", virtualThreadEnabled)

        if (virtualThreadEnabled) {
            restClientBuilder.requestFactory(
                JdkClientHttpRequestFactory(
                    HttpClient.newBuilder()
                        .executor(Executors.newVirtualThreadPerTaskExecutor())
                        .build(),
                ),
            )
        }

        restClientBuilder
            .requestInterceptor(RetryClientHttpRequestInterceptor())
            .requestFactory(clientHttpRequestFactory())
            .defaultStatusHandler(HttpStatusCode::is4xxClientError) { _, response ->
                log.error("Client Error Code={}", response.statusCode)
                log.error("Client Error Message={}", String(response.body.readAllBytes()))
                throw RestClientException()
            }
            .defaultStatusHandler(HttpStatusCode::is5xxServerError) { _, response ->
                log.error("Server Error Code={}", response.statusCode)
                log.error("Server Error Message={}", String(response.body.readAllBytes()))
                throw RestClientException()
            }
            .build()

        val restClient = restClientBuilder.build()
        val adapter = RestClientAdapter.create(restClient)
        val factory = HttpServiceProxyFactory.builderFor(adapter).build()

        return factory.createClient(clientClass)
    }

    private fun clientHttpRequestFactory(): JdkClientHttpRequestFactory {
        val requestSettings =
            ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(CONNECT_TIME))
                .withReadTimeout(Duration.ofSeconds(READ_TIME))

        return ClientHttpRequestFactories.get(JdkClientHttpRequestFactory::class.java, requestSettings)
            ?: JdkClientHttpRequestFactory(
                HttpClient.newBuilder()
                    .executor(Executors.newVirtualThreadPerTaskExecutor())
                    .build(),
            )
    }

    private fun defaultUriBuilderFactory(baseUrl: String): DefaultUriBuilderFactory {
        return DefaultUriBuilderFactory(baseUrl).apply {
            setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE)
        }
    }
}
