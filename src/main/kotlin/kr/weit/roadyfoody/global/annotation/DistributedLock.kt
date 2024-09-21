package kr.weit.roadyfoody.global.annotation

import java.util.concurrent.TimeUnit

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Repeatable
annotation class DistributedLock(
    val lockName: String,
    val identifier: String,
    val waitTime: Long = 30L,
    val leaseTime: Long = 10L,
    val timeUnit: TimeUnit = TimeUnit.SECONDS,
)
