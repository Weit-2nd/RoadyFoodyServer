package kr.weit.roadyfoody.auth.presentation.spec

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import kr.weit.roadyfoody.auth.application.dto.DuplicatedNicknameResponse
import kr.weit.roadyfoody.auth.application.dto.SignUpRequest
import kr.weit.roadyfoody.common.exception.ErrorResponse
import kr.weit.roadyfoody.global.swagger.v1.SwaggerTag
import kr.weit.roadyfoody.global.validator.MaxFileSize
import kr.weit.roadyfoody.global.validator.WebPImage
import kr.weit.roadyfoody.user.utils.NICKNAME_REGEX_DESC
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
                description = "회원가입 실패",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "Invalid Social Access Token",
                                summary = "SocialAccessToken 미입력",
                                value = """
                        {
                            "code": -10000,
                            "errorMessage": "socialAccessToken 가 존재하지 않습니다."
                        }
                        """,
                            ),
                            ExampleObject(
                                name = "Invalid Image Input",
                                summary = "WEBP 이외의 이미지 입력",
                                value = """
                        {
                            "code": -10000,
                            "errorMessage": "profileImage: webp 형식이 아닙니다."
                        }
                        """,
                            ),
                            ExampleObject(
                                name = "Max Size Exceeded",
                                summary = "최대 사진 크기 초과",
                                value = """
                        {
                            "code": -10000,
                            "errorMessage": "파일 사이즈가 초과하였습니다."
                        }
                        """,
                            ),
                            ExampleObject(
                                name = "Invalid Nickname Input",
                                summary = "미충족 닉네임 입력",
                                value = """
                        {
                            "code": -10000,
                            "errorMessage": "$NICKNAME_REGEX_DESC"
                        }
                        """,
                            ),
                            ExampleObject(
                                name = "Not Enough Required TermIds",
                                summary = "필수약관 미동의",
                                value = """
                        {
                            "code": -10000,
                            "errorMessage": "$ERROR_MSG_PREFIX $ERROR_MSG_SUFFIX"
                        }
                        """,
                            ),
                        ],
                    ),
                ],
            ), ApiResponse(
                responseCode = "401",
                description = "유효하지 않는 토큰으로 요청",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                value = """
                        {
                            "code": -10001,
                            "errorMessage": "유효하지 않은 토큰입니다."
                        }
                        """,
                            ),
                        ],
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "409",
                description = "중복된 회원가입 요청",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                value = """
                        {
                            "code": -10005,
                            "errorMessage": "이미 존재하는 유저입니다."
                        }
                        """,
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    fun signUp(
        @Parameter(hidden = true)
        socialAccessToken: String?,
        @Valid
        signUpRequest: SignUpRequest,
        @Schema(
            description = "프로필 이미지. 최대 1MB, WEBP 형식만 가능합니다. 이미지가 없을 시 하단 체크박스는 해제해주세요.",
            required = false,
            type = "string",
            format = "binary",
        )
        @MaxFileSize
        @WebPImage
        profileImage: MultipartFile?,
    )

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
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = DuplicatedNicknameResponse::class),
                        examples = [
                            ExampleObject(
                                value = """
                        {
                            "isDuplicated": true
                        }
                        """,
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    fun checkDuplicatedNickname(nickname: String): DuplicatedNicknameResponse
}
