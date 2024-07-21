package kr.weit.roadyfoody.user.application

import kr.weit.roadyfoody.common.exception.ErrorCode
import kr.weit.roadyfoody.common.exception.RoadyFoodyBadRequestException
import kr.weit.roadyfoody.global.annotation.DistributedLock
import kr.weit.roadyfoody.user.repository.UserRepository
import kr.weit.roadyfoody.user.repository.getByUserId
import org.springframework.stereotype.Service

@Service
class UserCommandService(
    private val userRepository: UserRepository,
) {
    @DistributedLock(lockName = "COIN-LOCK", identifier = "userId")
    fun decreaseCoin(
        userId: Long,
        coin: Int,
    ) {
        val user = userRepository.getByUserId(userId)
        if (user.coin < coin)
            {
                throw RoadyFoodyBadRequestException(ErrorCode.COIN_NOT_ENOUGH)
            }
        user.decreaseCoin(coin)
    }

    @DistributedLock(lockName = "COIN-LOCK", identifier = "userId")
    fun increaseCoin(
        userId: Long,
        coin: Int,
    ) {
        val user = userRepository.getByUserId(userId)
        user.increaseCoin(coin)
    }
}
