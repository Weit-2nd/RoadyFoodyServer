package kr.weit.roadyfoody.auth.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Pattern
import kr.weit.roadyfoody.global.validator.WebPImage
import kr.weit.roadyfoody.user.domain.SocialLoginType
import kr.weit.roadyfoody.user.utils.NICKNAME_REGEX_DESC
import kr.weit.roadyfoody.user.utils.NICKNAME_REGEX_STR
import org.springframework.web.multipart.MultipartFile

data class SignUpRequest(
    @Schema(
        description = "닉네임. $NICKNAME_REGEX_DESC",
        example = "테스트닉네임",
    )
    @field:Pattern(regexp = NICKNAME_REGEX_STR, message = NICKNAME_REGEX_DESC)
    val nickname: String = "",
    @field:WebPImage
    val profileImage: MultipartFile?,
    @Schema(
        description = "약관 동의 ID 목록",
        example = "[1, 2, 3]",
    )
    @JsonProperty("agreedTermIds")
    val agreedTermIdSet: Set<Long> = emptySet(),
    @Schema(
        description = "소셜 로그인 타입",
        example = "KAKAO",
    )
    val socialLoginType: SocialLoginType = SocialLoginType.KAKAO,
)
