package kr.weit.roadyfoody.auth.application.service

import kr.weit.roadyfoody.auth.dto.SignUpRequest
import kr.weit.roadyfoody.auth.exception.UserAlreadyExistsException
import kr.weit.roadyfoody.global.service.ImageService
import kr.weit.roadyfoody.term.service.TermCommandService
import kr.weit.roadyfoody.user.domain.SocialLoginType
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.repository.UserRepository
import kr.weit.roadyfoody.useragreedterm.service.UserAgreedTermCommandService
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
) {
    @Transactional
    fun register(
        socialAccessToken: String,
        signUpRequest: SignUpRequest,
        profileImage: MultipartFile?,
    ) {
        val socialId = obtainSocialId(signUpRequest.socialLoginType, socialAccessToken)

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
    }

    private fun obtainSocialId(
        socialLoginType: SocialLoginType,
        socialAccessToken: String,
    ): String = "$socialLoginType ${authQueryService.requestKakaoUserInfo(socialAccessToken).id}"
}
