package kr.weit.roadyfoody.auth.application.service

import kr.weit.roadyfoody.auth.domain.SocialAccessToken
import kr.weit.roadyfoody.auth.dto.SignUpRequest
import kr.weit.roadyfoody.auth.exception.UserAlreadyExistsException
import kr.weit.roadyfoody.global.service.ImageService
import kr.weit.roadyfoody.term.service.TermCommandService
import kr.weit.roadyfoody.user.domain.SocialLoginType
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.repository.UserRepository
import kr.weit.roadyfoody.useragreedterm.service.UserAgreedTermCommandService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthCommandService(
    private val authQueryService: AuthQueryService,
    private val termCommandService: TermCommandService,
    private val userAgreedTermCommandService: UserAgreedTermCommandService,
    private val userRepository: UserRepository,
    private val imageService: ImageService,
) {
    private val log: Logger = LoggerFactory.getLogger(AuthCommandService::class.java)

    @Transactional
    fun register(
        socialAccessToken: SocialAccessToken,
        signUpRequest: SignUpRequest,
    ) {
        val socialId = generateSocialId(signUpRequest.socialLoginType, socialAccessToken)

        if (userRepository.existsBySocialId(socialId)) {
            log.error("UserAlreadyExistsException={}", socialId)
            throw UserAlreadyExistsException()
        }
        termCommandService.checkRequiredTermsOrThrow(signUpRequest.agreedTermIdSet)

        if (signUpRequest.profileImage == null) {
            val user = User.of(socialId, signUpRequest.nickname)
            userRepository.save(user)
            userAgreedTermCommandService.storeUserAgreedTerms(user, signUpRequest.agreedTermIdSet)
        } else {
            val imageName = imageService.generateImageName(signUpRequest.profileImage.originalFilename)
            val user = User.of(socialId, signUpRequest.nickname, imageName)
            userRepository.save(user)
            userAgreedTermCommandService.storeUserAgreedTerms(user, signUpRequest.agreedTermIdSet)
            imageService.upload(imageName, signUpRequest.profileImage) // 외부 저장소 롤백 어려움을 고려해 가장 아래에 배치
        }
    }

    private fun generateSocialId(
        socialLoginType: SocialLoginType,
        socialAccessToken: SocialAccessToken,
    ): String = "$socialLoginType ${authQueryService.requestKakaoUserInfo(socialAccessToken).id}"
}
