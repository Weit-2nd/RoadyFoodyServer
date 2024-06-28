package kr.weit.roadyfoody.auth.application.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Pattern
import kr.weit.roadyfoody.user.domain.SocialLoginType
import kr.weit.roadyfoody.user.utils.NICKNAME_REGEX_DESC
import kr.weit.roadyfoody.user.utils.NICKNAME_REGEX_STR

data class SignUpRequest(
    @Schema(description = "닉네임. $NICKNAME_REGEX_DESC", example = "테스트닉네임123")
    @field:Pattern(regexp = NICKNAME_REGEX_STR, message = NICKNAME_REGEX_DESC)
    val nickname: String,
    @Schema(description = "동의한 약관 ID 목록", example = "[1, 2, 3]")
    val agreedTermIds: Set<Long>,
    @Schema(hidden = true)
    val socialLoginType: SocialLoginType = SocialLoginType.KAKAO,
)
