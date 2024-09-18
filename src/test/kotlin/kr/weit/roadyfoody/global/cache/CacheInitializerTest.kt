package kr.weit.roadyfoody.global.cache

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.weit.roadyfoody.ranking.utils.LIKE_RANKING_KEY
import kr.weit.roadyfoody.ranking.utils.REPORT_RANKING_KEY
import kr.weit.roadyfoody.ranking.utils.REVIEW_RANKING_KEY
import kr.weit.roadyfoody.ranking.utils.TOTAL_RANKING_KEY
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.data.redis.core.ListOperations
import org.springframework.data.redis.core.RedisTemplate

class CacheInitializerTest :
    BehaviorSpec({
        val cacheManager: CacheManager = mockk()
        val redisTemplate: RedisTemplate<String, String> = mockk()
        val cacheInitializer = CacheInitializer(cacheManager, redisTemplate)

        given("CacheInitializer") {
            val keys = listOf(REPORT_RANKING_KEY, REVIEW_RANKING_KEY, LIKE_RANKING_KEY, TOTAL_RANKING_KEY)
            val values = listOf("user1:10", "user2:20", "user3:15")
            val listOperations = mockk<ListOperations<String, String>>()
            val cache = mockk<Cache>()

            `when`("애플리케이션이 시작된 후") {
                every { redisTemplate.opsForList() } returns listOperations
                keys.forEach { key ->
                    every { listOperations.range(key, 0, -1) } returns values
                    every { cacheManager.getCache(key) } returns cache
                    every { cache.put(key, values) } returns Unit
                }

                then("비워있던 로컬 캐시가 저장된다.") {
                    cacheInitializer.run(null)

                    verify(exactly = 4) { redisTemplate.opsForList() }
                    keys.forEach { key ->
                        verify(exactly = 1) { listOperations.range(key, 0, -1) }
                        verify(exactly = 1) { cacheManager.getCache(key) }
                        verify(exactly = 1) { cache.put(key, values) }
                    }
                }
            }
        }
    })
