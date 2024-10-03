package kr.weit.roadyfoody.global.cache

import POPULAR_SEARCH_KEY
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
class CacheLoader(
    private val cacheManager: CacheManager,
    private val redisTemplate: RedisTemplate<String, String>,
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        rankingLoader(REPORT_RANKING_KEY)
        rankingLoader(REVIEW_RANKING_KEY)
        rankingLoader(LIKE_RANKING_KEY)
        rankingLoader(TOTAL_RANKING_KEY)
        popularSearchesLoader()
    }

    private fun rankingLoader(key: String) {
        val value = redisTemplate.opsForList().range(key, 0, -1)
        cacheManager.getCache(key)?.put(key, value)
    }

    private fun popularSearchesLoader() {
        val popularSearches = redisTemplate.opsForValue().get(POPULAR_SEARCH_KEY)
        if (popularSearches != null) {
            cacheManager.getCache(POPULAR_SEARCH_KEY)?.put(POPULAR_SEARCH_KEY, popularSearches)
        }
    }
}
