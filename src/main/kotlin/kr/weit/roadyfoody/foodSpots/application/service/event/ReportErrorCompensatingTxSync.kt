package kr.weit.roadyfoody.foodSpots.application.service.event

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.transaction.reactive.TransactionSynchronization.STATUS_ROLLED_BACK
import org.springframework.transaction.support.TransactionSynchronization

class ReportErrorCompensatingTxSync(
    private val key: String,
    private val redisTemplate: RedisTemplate<String, String>,
) : TransactionSynchronization {
    override fun afterCompletion(status: Int) {
        if (status == STATUS_ROLLED_BACK) {
            redisTemplate.opsForValue().decrement(key)
        }
    }
}
