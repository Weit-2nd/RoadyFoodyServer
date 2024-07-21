package kr.weit.roadyfoody.common.annotation

import kr.weit.roadyfoody.common.exception.ErrorCode
import kr.weit.roadyfoody.common.exception.RedisLockFailedException
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.redisson.api.RedissonClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.TransactionTimedOutException

@Aspect
@Component
class DistributedLockAop(
    private val redissonClient: RedissonClient,
    private val aopForTransaction: AopForTransaction,
) {
    val log: Logger = LoggerFactory.getLogger(DistributedLockAop::class.java)

    @Around("@annotation(kr.weit.roadyfoody.common.annotation.DistributedLock)")
    fun lock(joinPoint: ProceedingJoinPoint): Any? {
        val signature = joinPoint.signature as MethodSignature
        val method = signature.method
        val distributedLock = method.getAnnotation(DistributedLock::class.java)
        val dynamicKey = getDynamicKeyFromMethodArgs(signature.parameterNames, joinPoint.args, distributedLock.identifier)

        val key = "${distributedLock.lockName}:$dynamicKey"

        val rLock = redissonClient.getLock(key)

        try {
            val available =
                rLock.tryLock(
                    distributedLock.waitTime,
                    distributedLock.leaseTime,
                    distributedLock.timeUnit,
                )
            if (!available) {
                throw RedisLockFailedException(ErrorCode.REDISSON_LOCK_FAILED.errorMessage)
            }

            return aopForTransaction.proceed(joinPoint)
        } catch (e: InterruptedException) {
            log.error("Interrupted Exception: ${e.message}")
            throw RedisLockFailedException(ErrorCode.REDISSON_LOCK_FAILED.errorMessage)
        } catch (e: TransactionTimedOutException) {
            log.error("Transaction Timed Out Exception: ${e.message}")
            throw RedisLockFailedException(ErrorCode.REDISSON_LOCK_FAILED.errorMessage)
        } finally {
            try {
                rLock.unlock()
            } catch (e: IllegalMonitorStateException) {
                log.error("Redisson Lock is already unlocked: {} {}", method.name, key)
            }
        }
    }

    private fun getDynamicKeyFromMethodArgs(
        methodParameterNames: Array<String>,
        args: Array<Any>,
        paramName: String,
    ): String {
        for (i in methodParameterNames.indices) {
            if (methodParameterNames[i] == paramName) {
                return args[i].toString()
            }
        }
        throw RedisLockFailedException(ErrorCode.BAD_REDISSON_IDENTIFIER.errorMessage)
    }
}
