package kr.weit.roadyfoody.auth.dto

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import kr.weit.roadyfoody.global.validator.NOT_NULL_MSG
import kr.weit.roadyfoody.global.validator.WebPImage
import kr.weit.roadyfoody.user.domain.SocialLoginType
import kr.weit.roadyfoody.user.utils.NICKNAME_REGEX_DESC
import kr.weit.roadyfoody.user.utils.NICKNAME_REGEX_STR
import org.springframework.web.multipart.MultipartFile

data class SignUpRequest(
    @field:NotNull(message = NOT_NULL_MSG)
    @field:Pattern(regexp = NICKNAME_REGEX_STR, message = NICKNAME_REGEX_DESC)
    val nickname: String?,
    @field:WebPImage
    val profileImage: MultipartFile?,
    @field:NotNull(message = NOT_NULL_MSG)
    val agreedTermIds: Set<Long>?,
    val socialLoginType: SocialLoginType = SocialLoginType.KAKAO,
)
