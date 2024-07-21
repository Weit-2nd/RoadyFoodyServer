package kr.weit.roadyfoody.common.annotation

import java.util.concurrent.TimeUnit

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class DistributedLock(
    val lockName: String,
    val identifier: String,
    val waitTime: Long = 10L,
    val leaseTime: Long = 30L,
    val timeUnit: TimeUnit = TimeUnit.SECONDS,
)
