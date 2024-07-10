package kr.weit.roadyfoody.auth.application.service

import kr.weit.roadyfoody.auth.application.dto.ServiceTokensResponse
import kr.weit.roadyfoody.auth.application.dto.SignUpRequest
import kr.weit.roadyfoody.auth.exception.UserAlreadyExistsException
import kr.weit.roadyfoody.auth.exception.UserNotRegisteredException
import kr.weit.roadyfoody.auth.security.jwt.JwtUtil
import kr.weit.roadyfoody.global.service.ImageService
import kr.weit.roadyfoody.term.application.service.TermCommandService
import kr.weit.roadyfoody.user.domain.SocialLoginType
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.repository.UserRepository
import kr.weit.roadyfoody.user.repository.getByUserId
import kr.weit.roadyfoody.useragreedterm.application.service.UserAgreedTermCommandService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class AuthCommandService(
    private val authQueryService: AuthQueryService,
    private val termCommandService: TermCommandService,
    private val userAgreedTermCommandService: UserAgreedTermCommandService,
    private val userRepository: UserRepository,
    private val imageService: ImageService,
    private val jwtUtil: JwtUtil,
) {
    @Transactional
    fun register(
        socialAccessToken: String,
        signUpRequest: SignUpRequest,
        profileImage: MultipartFile?,
    ): ServiceTokensResponse {
        val socialId = obtainUserSocialId(signUpRequest.socialLoginType, socialAccessToken)

        if (userRepository.existsBySocialId(socialId) ||
            userRepository.existsByProfileNickname(signUpRequest.nickname)
        ) {
            throw UserAlreadyExistsException()
        }
        termCommandService.checkRequiredTermsOrThrow(signUpRequest.agreedTermIds)

        val user = User.of(socialId, signUpRequest.nickname)
        userRepository.save(user)
        userAgreedTermCommandService.storeUserAgreedTerms(user, signUpRequest.agreedTermIds)

        if (profileImage != null) {
            val imageName = imageService.generateImageName(profileImage)
            user.profile.changeProfileImageName(imageName)
            imageService.upload(imageName, profileImage)
        }

        return makeTokens(user.id)
    }

    fun login(socialAccessToken: String): ServiceTokensResponse {
        val userSocialId = obtainUserSocialId(SocialLoginType.KAKAO, socialAccessToken)
        val user =
            userRepository.findBySocialId(userSocialId)
                ?: throw UserNotRegisteredException()

        return makeTokens(user.id)
    }

    private fun obtainUserSocialId(
        socialLoginType: SocialLoginType,
        socialAccessToken: String,
    ): String = "$socialLoginType ${authQueryService.requestKakaoUserInfo(socialAccessToken).id}"

    fun reissueTokens(refreshToken: String): ServiceTokensResponse {
        require(
            jwtUtil.validateToken(jwtUtil.refreshKey, refreshToken) &&
                jwtUtil.validateCachedRefreshTokenRotateId(refreshToken),
        ) {
            "RefreshToken 이 유효하지 않습니다."
        }
        val userId = jwtUtil.getUserId(jwtUtil.refreshKey, refreshToken)
        val user = userRepository.getByUserId(userId)
        return makeTokens(user.id)
    }

    private fun makeTokens(userId: Long): ServiceTokensResponse {
        val accessToken = jwtUtil.generateAccessToken(userId)
        val rotateId = jwtUtil.generateRotateId()
        val refreshToken = jwtUtil.generateRefreshToken(userId, rotateId)
        jwtUtil.storeCachedRefreshTokenRotateId(userId, rotateId)
        return ServiceTokensResponse(accessToken, refreshToken)
    }

    fun logout(user: User) {
        jwtUtil.removeCachedRefreshToken(user.id)
    }
}
