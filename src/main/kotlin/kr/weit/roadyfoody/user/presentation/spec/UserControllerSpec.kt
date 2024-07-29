package kr.weit.roadyfoody.user.presentation.spec

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import kr.weit.roadyfoody.global.swagger.v1.SwaggerTag
import kr.weit.roadyfoody.user.application.dto.UserInfoResponse
import kr.weit.roadyfoody.user.domain.User
import org.springframework.http.MediaType

@Tag(name = SwaggerTag.USER)
interface UserControllerSpec {
    @Operation(
        summary = "로그인 유저 정보 조회 API",
        description = "로그인한 유저 정보(닉네임, 프로필 사진 URL, 보유 코인) 를 조회합니다.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "로그인 유저 정보 조회 성공",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema =
                            Schema(implementation = UserInfoResponse::class),
                    ),
                ],
            ),
        ],
    )
    fun getLoginUserInfo(user: User): UserInfoResponse
}
