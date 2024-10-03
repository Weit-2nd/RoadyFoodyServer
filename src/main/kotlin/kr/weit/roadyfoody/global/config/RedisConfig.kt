package kr.weit.roadyfoody.global.config

import POPULAR_SEARCH_TOPIC
import kr.weit.roadyfoody.global.cache.CacheSubscriber
import kr.weit.roadyfoody.ranking.utils.RANKING_TOPIC
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisKeyValueAdapter
import org.springframework.data.redis.listener.PatternTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories

@Configuration
@EnableRedisRepositories(enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)
class RedisConfig {
    @Bean
    fun redisMessageListener(
        connectionFactory: RedisConnectionFactory,
        cacheSubscriber: CacheSubscriber,
    ): RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer()
        container.setConnectionFactory(connectionFactory)
        container.addMessageListener(cacheSubscriber, PatternTopic(RANKING_TOPIC))
        container.addMessageListener(cacheSubscriber, PatternTopic(POPULAR_SEARCH_TOPIC))
        return container
    }
}
