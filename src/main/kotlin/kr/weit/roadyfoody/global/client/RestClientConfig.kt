package kr.weit.roadyfoody.global.client

import kr.weit.roadyfoody.test.application.client.TestClientInterface
import org.springframework.boot.web.client.ClientHttpRequestFactories
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.JdkClientHttpRequestFactory
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import java.time.Duration

@Configuration
class RestClientConfig {
    companion object {
        private const val CONNECT_TIME = 1L
        private const val READ_TIME = 5L
        private const val TEST_URL = "https://jsonplaceholder.typicode.com"
    }

    @Bean
    fun testClientInterface(): TestClientInterface {
        return creatClient(TEST_URL, TestClientInterface::class.java)
    }

    private fun <T> creatClient(
        baseUrl: String,
        clientClass: Class<T>,
    ): T {
        // Todo. 가상 쓰레드를 사용하기 위해 API 비동기화를 위한 설정 추가 필요
        val requestSettings: ClientHttpRequestFactorySettings =
            ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(CONNECT_TIME))
                .withReadTimeout(Duration.ofSeconds(READ_TIME))

        val jdkClientHttpRequestFactory: JdkClientHttpRequestFactory =
            ClientHttpRequestFactories.get(
                JdkClientHttpRequestFactory::class.java,
                requestSettings,
            )

        val restClient =
            RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(jdkClientHttpRequestFactory)
                .requestInterceptor(RetryClientHttpRequestInterceptor())
                .build()

        val adapter = RestClientAdapter.create(restClient)
        val factory = HttpServiceProxyFactory.builderFor(adapter).build()

        return factory.createClient(clientClass)
    }
}
