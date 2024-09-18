package kr.weit.roadyfoody.global.cache

import org.springframework.cache.CacheManager
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class CacheSubscriber(
    private val redisTemplate: RedisTemplate<String, String>,
    private val cacheManager: CacheManager,
) : MessageListener {
    override fun onMessage(
        message: Message,
        pattern: ByteArray?,
    ) {
        val key = redisTemplate.stringSerializer.deserialize(message.body) ?: return
        val value = redisTemplate.opsForList().range(key, 0, -1)
        cacheManager.getCache(key)?.put(key, value)
    }
}
