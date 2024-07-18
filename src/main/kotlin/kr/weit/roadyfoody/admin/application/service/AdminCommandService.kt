package kr.weit.roadyfoody.admin.application.service

import kr.weit.roadyfoody.admin.dto.UserAccessTokenResponse
import kr.weit.roadyfoody.auth.security.jwt.JwtUtil
import kr.weit.roadyfoody.user.repository.UserRepository
import kr.weit.roadyfoody.user.repository.getByUserId
import org.springframework.stereotype.Service

@Service
class AdminCommandService(
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil,
) {
    companion object {
        private const val USER_ACCESS_TOKEN_EXPIRE_TIME = 1_000_000_000L
    }

    fun getUserAccessToken(userId: Long): UserAccessTokenResponse {
        val user = userRepository.getByUserId(userId)
        val accessToken = jwtUtil.generateAccessToken(user.id, USER_ACCESS_TOKEN_EXPIRE_TIME)
        return UserAccessTokenResponse(accessToken)
    }
}
