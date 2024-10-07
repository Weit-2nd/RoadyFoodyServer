package kr.weit.roadyfoody.global.service

import io.awspring.cloud.s3.S3Template
import kr.weit.roadyfoody.global.config.S3Properties
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.io.InputStream
import java.time.Duration

@Service
class StorageService(
    private val s3Properties: S3Properties,
    private val s3Template: S3Template,
    private val redisTemplate: StringRedisTemplate,
) {
    fun upload(
        key: String,
        stream: InputStream,
    ): String {
        val result = s3Template.upload(s3Properties.bucket, key, stream)
        return result.url.toString()
    }

    fun downloadUrl(key: String): String {
        val cacheKey = getObjectStorageCacheKey(key)
        if (redisTemplate.hasKey(cacheKey)) {
            return redisTemplate.opsForValue().get(cacheKey)!!.split("?")[0]
        } else {
            val url = s3Template.createSignedGetURL(s3Properties.bucket, key, CACHE_DURATION).toString()
            // 캐싱시간은 실제 파일의 생명주기보다 살짝 짧아야한다
            redisTemplate.opsForValue().set(cacheKey, url, CACHE_DURATION.minusMinutes(30))
            return url.split("?")[0]
        }
    }

    fun delete(key: String) {
        s3Template.deleteObject(s3Properties.bucket, key)
        redisTemplate.delete(getObjectStorageCacheKey(key))
    }

    companion object {
        @JvmStatic
        private val CACHE_DURATION = Duration.ofDays(1)
        private const val OBJECT_STORAGE_CACHE_KEY = "rofo:object-url-cache:"

        fun getObjectStorageCacheKey(key: String): String = "$OBJECT_STORAGE_CACHE_KEY$key"
    }
}
