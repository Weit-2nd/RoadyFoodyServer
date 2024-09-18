package kr.weit.roadyfoody.global.config

import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableCaching
class CacheConfig {
    @Bean
    fun cacheManager(): CaffeineCacheManager =
        CaffeineCacheManager().apply {
            isAllowNullValues = false
        }
}
