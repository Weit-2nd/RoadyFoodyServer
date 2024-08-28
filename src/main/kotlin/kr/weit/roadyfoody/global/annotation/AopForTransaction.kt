package kr.weit.roadyfoody.global.annotation

import org.aspectj.lang.ProceedingJoinPoint
import org.springframework.stereotype.Component

@Component
class AopForTransaction {
    fun proceed(joinPoint: ProceedingJoinPoint): Any? = joinPoint.proceed()
}
