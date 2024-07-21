package kr.weit.roadyfoody.common.annotation

import org.aspectj.lang.ProceedingJoinPoint
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class AopForTransaction {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun proceed(joinPoint: ProceedingJoinPoint): Any? = joinPoint.proceed()
}
