package kr.weit.roadyfoody.global.redisson

import kr.weit.roadyfoody.common.exception.ErrorCode
import kr.weit.roadyfoody.common.exception.RedisLockFailedException
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class RedisLockUtil(
    private val redissonClient: RedissonClient,
) {
    companion object {
        private const val LOCK_WAIT_TIME_MS: Long = 10000
        private const val LOCK_LEASE_TIME_MS: Long = 3000
        private val TIME_UNIT = TimeUnit.MILLISECONDS
    }

    fun <T> executeWithLock(
        lockKey: String,
        action: () -> T,
    ): T {
        val lock = redissonClient.getLock(lockKey)
        val available = lock.tryLock(LOCK_WAIT_TIME_MS, LOCK_LEASE_TIME_MS, TIME_UNIT)

        try {
            if (!available)
                {
                    throw RedisLockFailedException(ErrorCode.REDISSON_LOCK_TOO_MANY_REQUEST.errorMessage)
                }
            return action()
        } finally {
            if (available)
                {
                    lock.unlock()
                }
        }
    }
}
