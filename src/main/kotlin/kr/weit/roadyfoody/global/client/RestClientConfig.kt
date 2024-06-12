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
    @Bean
    fun testClientInterface(): TestClientInterface {
        return creatClient("https://jsonplaceholder.typicode.com", TestClientInterface::class.java)
    }

    // Todo RestClient에 가상 쓰레드 지정
    private fun <T> creatClient(
        baseUrl: String,
        clientClass: Class<T>,
    ): T {
        val requestSettings: ClientHttpRequestFactorySettings =
            ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(1L))
                .withReadTimeout(Duration.ofSeconds(5L))

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
