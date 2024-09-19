package kr.weit.roadyfoody.global.circuitbreaker.targetexception

import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import org.redisson.client.RedisException
import org.springframework.data.redis.ClusterRedirectException
import org.springframework.data.redis.ClusterStateFailureException
import org.springframework.data.redis.RedisConnectionFailureException
import org.springframework.data.redis.RedisSystemException
import org.springframework.data.redis.TooManyClusterRedirectionsException

val REDIS_CIRCUIT_BREAKER_TARGET_EXCEPTIONS =
    listOf(
        CallNotPermittedException::class.java,
        RedisSystemException::class.java,
        RedisConnectionFailureException::class.java,
        TooManyClusterRedirectionsException::class.java,
        ClusterRedirectException::class.java,
        ClusterStateFailureException::class.java,
        RedisException::class.java,
    )
