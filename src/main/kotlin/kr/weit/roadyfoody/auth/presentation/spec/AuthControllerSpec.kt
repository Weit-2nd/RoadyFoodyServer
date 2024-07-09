package kr.weit.roadyfoody.auth.presentation.spec

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import kr.weit.roadyfoody.auth.application.dto.DuplicatedNicknameResponse
import kr.weit.roadyfoody.auth.application.dto.ServiceTokensResponse
import kr.weit.roadyfoody.auth.application.dto.SignUpRequest
import kr.weit.roadyfoody.common.exception.ErrorCode.AUTHENTICATED_USER_NOT_FOUND
import kr.weit.roadyfoody.common.exception.ErrorCode.INVALID_NICKNAME
import kr.weit.roadyfoody.common.exception.ErrorCode.INVALID_REFRESH_TOKEN
import kr.weit.roadyfoody.common.exception.ErrorCode.INVALID_TOKEN
import kr.weit.roadyfoody.common.exception.ErrorCode.INVALID_WEBP_IMAGE
import kr.weit.roadyfoody.common.exception.ErrorCode.MAX_FILE_SIZE_EXCEEDED
import kr.weit.roadyfoody.common.exception.ErrorCode.MISSING_REFRESH_TOKEN
import kr.weit.roadyfoody.common.exception.ErrorCode.MISSING_SOCIAL_ACCESS_TOKEN
import kr.weit.roadyfoody.common.exception.ErrorCode.UNAUTHENTICATED_ACCESS
import kr.weit.roadyfoody.common.exception.ErrorCode.USER_ALREADY_EXISTS
import kr.weit.roadyfoody.common.exception.ErrorCode.USER_NOT_REGISTERED
import kr.weit.roadyfoody.common.exception.ErrorResponse
import kr.weit.roadyfoody.global.swagger.ApiErrorCodeExamples
import kr.weit.roadyfoody.global.swagger.v1.SwaggerTag
import kr.weit.roadyfoody.global.validator.MaxFileSize
import kr.weit.roadyfoody.global.validator.WebPImage
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.useragreedterm.exception.ERROR_MSG_PREFIX
import kr.weit.roadyfoody.useragreedterm.exception.ERROR_MSG_SUFFIX
import org.springframework.http.MediaType
import org.springframework.web.multipart.MultipartFile

@Tag(name = SwaggerTag.AUTH)
interface AuthControllerSpec {
    @Operation(
        summary = "회원가입 API",
        description = "소셜 로그인 토큰을 통해 회원가입을 진행합니다. 해당 토큰은 오른쪽 상단 자물쇠 아이콘을 통해 Authorization 을 넣어주세요.",
        responses = [
            ApiResponse(
                responseCode = "201",
                description = "회원가입 성공",
            ),
            ApiResponse(
                responseCode = "400",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "Not Enough Required Term IDs",
                                summary = "NOT_ENOUGH_REQUIRED_TERM_IDS",
                                value = """
                        {
                            "code": -10000,
                            "errorMessage": "$ERROR_MSG_PREFIX 1, 2 $ERROR_MSG_SUFFIX"
                        }
                        """,
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            INVALID_WEBP_IMAGE,
            MAX_FILE_SIZE_EXCEEDED,
            INVALID_NICKNAME,
            MISSING_SOCIAL_ACCESS_TOKEN,
            INVALID_TOKEN,
            USER_ALREADY_EXISTS,
        ],
    )
    fun signUp(
        @Parameter(hidden = true)
        socialAccessToken: String?,
        @Valid
        signUpRequest: SignUpRequest,
        @Schema(
            description = "프로필 이미지. 최대 1MB, WEBP 형식만 가능합니다. 이미지가 없을 시 하단 체크박스는 해제해주세요.",
            type = "string",
            format = "binary",
        )
        @MaxFileSize
        @WebPImage
        profileImage: MultipartFile?,
    ): ServiceTokensResponse

    @Operation(
        summary = "닉네임 중복 검사 API",
        description = "회원가입 시 사용할 닉네임이 중복되는지 검사합니다.",
        parameters = [
            Parameter(name = "nickname", description = "닉네임", required = true, example = "테스트닉네임입니다"),
        ],
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "요청 성공",
            ),
        ],
    )
    fun checkDuplicatedNickname(nickname: String): DuplicatedNicknameResponse

    @Operation(
        summary = "로그인 API",
        description = "Authorization 헤더에 socialAccessToken(Bearer)을 넣어 로그인을 진행합니다.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "로그인 성공",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            MISSING_SOCIAL_ACCESS_TOKEN,
            INVALID_TOKEN,
            USER_NOT_REGISTERED,
        ],
    )
    fun signIn(
        @Parameter(hidden = true)
        socialAccessToken: String?,
    ): ServiceTokensResponse

    @Operation(
        summary = "서비스 AccessToken 갱신 API",
        description = "Refresh Token 을 통해 AccessToken 을 갱신합니다.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "요청 성공",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            MISSING_REFRESH_TOKEN,
            INVALID_REFRESH_TOKEN,
        ],
    )
    fun refresh(
        @Parameter(
            name = "token",
            description = "리프레시 토큰 (Bearer 같은 형식 표기없이 전달해주세요. 예: eyJhbGciOiJ9.eyJzdWIiOiJ0ZXN0QGdtYWlsLmNvbSIsImV4cCI6MTY0NzQwNjQwNn0.1",
            required = true,
            `in` = ParameterIn.QUERY,
        )
        refreshToken: String?,
    ): ServiceTokensResponse

    @Operation(
        summary = "로그아웃 API",
        description = "Authorization 헤더에 AccessToken(Bearer)을 넣어 로그아웃을 진행합니다.",
        responses = [
            ApiResponse(
                responseCode = "204",
                description = "로그아웃 성공",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            UNAUTHENTICATED_ACCESS,
            AUTHENTICATED_USER_NOT_FOUND,
        ],
    )
    fun signOut(user: User)

    @Operation(
        summary = "회원탈퇴 API",
        description = "Authorization 헤더에 AccessToken(Bearer)을 넣어 회원탈퇴를 진행합니다.",
        responses = [
            ApiResponse(
                responseCode = "204",
                description = "회원탈퇴 성공",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            UNAUTHENTICATED_ACCESS,
            AUTHENTICATED_USER_NOT_FOUND,
        ],
    )
    fun withdraw(user: User)
}
