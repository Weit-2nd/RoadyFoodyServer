package kr.weit.roadyfoody.admin.application.service

import kr.weit.roadyfoody.admin.dto.UserAccessTokenResponse
import kr.weit.roadyfoody.auth.security.jwt.JwtUtil
import kr.weit.roadyfoody.foodSpots.application.service.FoodSpotsCommandService.Companion.getFoodSpotsReportCountKey
import kr.weit.roadyfoody.user.exception.UserNotFoundException
import kr.weit.roadyfoody.user.repository.UserRepository
import kr.weit.roadyfoody.user.repository.getByUserId
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Profile("!stable")
@Service
class AdminCommandService(
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil,
    private val redisTemplate: RedisTemplate<String, String>,
) {
    companion object {
        private const val USER_ACCESS_TOKEN_EXPIRE_TIME = 1_000_000_000L
    }

    fun getUserAccessToken(userId: Long): UserAccessTokenResponse {
        val user = userRepository.getByUserId(userId)
        val accessToken = jwtUtil.generateAccessToken(user.id, USER_ACCESS_TOKEN_EXPIRE_TIME)
        return UserAccessTokenResponse(accessToken)
    }

    fun updateUserDailyReportCount(
        userId: Long,
        dailyReportCount: Int,
    ) {
        if (userRepository.existsById(userId).not()) {
            throw UserNotFoundException("$userId ID 의 사용자는 존재하지 않습니다.")
        }
        redisTemplate.opsForValue().set(getFoodSpotsReportCountKey(userId), dailyReportCount.toString())
    }
}
