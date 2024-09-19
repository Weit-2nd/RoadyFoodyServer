package kr.weit.roadyfoody.global.cache

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.stereotype.Service

@Service
class CachePublisher(
    private val redisTemplate: RedisTemplate<String, String>,
) {
    fun publishCacheUpdate(
        topic: ChannelTopic,
        key: String,
    ) {
        redisTemplate.convertAndSend(topic.topic, key)
    }
}
