package kr.weit.roadyfoody.global.cache

import kr.weit.roadyfoody.ranking.utils.LIKE_RANKING_KEY
import kr.weit.roadyfoody.ranking.utils.REPORT_RANKING_KEY
import kr.weit.roadyfoody.ranking.utils.REVIEW_RANKING_KEY
import kr.weit.roadyfoody.ranking.utils.TOTAL_RANKING_KEY
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.cache.CacheManager
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class CacheInitializer(
    private val cacheManager: CacheManager,
    private val redisTemplate: RedisTemplate<String, String>,
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        rankingInitializer(REPORT_RANKING_KEY)
        rankingInitializer(REVIEW_RANKING_KEY)
        rankingInitializer(LIKE_RANKING_KEY)
        rankingInitializer(TOTAL_RANKING_KEY)
    }

    private fun rankingInitializer(key: String) {
        val value = redisTemplate.opsForList().range(key, 0, -1)
        cacheManager.getCache(key)?.put(key, value)
    }
}
