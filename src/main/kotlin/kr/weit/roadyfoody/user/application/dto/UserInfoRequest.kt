@file:Suppress("ktlint:standard:filename")

package kr.weit.roadyfoody.user.application.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Pattern
import kr.weit.roadyfoody.user.utils.NICKNAME_REGEX_DESC
import kr.weit.roadyfoody.user.utils.NICKNAME_REGEX_STR

data class UserNicknameRequest(
    @Schema(description = "닉네임. $NICKNAME_REGEX_DESC", example = "테스트닉네임123")
    @field:Pattern(regexp = NICKNAME_REGEX_STR, message = NICKNAME_REGEX_DESC)
    val nickname: String,
)
