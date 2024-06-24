package kr.weit.roadyfoody.auth.presentation.spec

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.SchemaProperty
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import kr.weit.roadyfoody.auth.dto.SignUpRequest
import kr.weit.roadyfoody.common.exception.ErrorResponse
import kr.weit.roadyfoody.global.swagger.v1.SwaggerTag
import kr.weit.roadyfoody.user.utils.NICKNAME_REGEX_DESC
import kr.weit.roadyfoody.useragreedterm.exception.ERROR_MSG_PREFIX
import kr.weit.roadyfoody.useragreedterm.exception.ERROR_MSG_SUFFIX
import org.springframework.http.MediaType

@Tag(name = SwaggerTag.AUTH)
interface AuthControllerSpec {
    @Operation(
        summary = "회원가입 API",
        description = "소셜 로그인 토큰을 통해 회원가입을 진행합니다. 해당 토큰은 오른쪽 상단 자물쇠 아이콘을 통해 Authorization 을 넣어주세요.",
        requestBody =
            RequestBody(
                content = [
                    Content(
                        mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                        schemaProperties = [
                            SchemaProperty(
                                name = "nickname",
                                schema =
                                    Schema(
                                        type = "string",
                                        description = "닉네임. $NICKNAME_REGEX_DESC",
                                        example = "테스트닉네임",
                                    ),
                            ),
                            SchemaProperty(
                                name = "profileImage",
                                schema =
                                    Schema(
                                        type = "string",
                                        format = "binary",
                                        description = "WebP 형식만 가능합니다. 프로필 사진이 없을 시 'send empty value' 체크박스는 해제해주세요.",
                                    ),
                            ),
                            SchemaProperty(
                                name = "agreedTermIds",
                                schema =
                                    Schema(
                                        type = "array",
                                        description = "약관 동의 ID 목록",
                                        example = "[1, 2, 3]",
                                    ),
                            ),
                        ],
                    ),
                ],
            ),
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
                responseCode = "409",
                description = "중복 회원가입 요청",
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
        signUpRequest: SignUpRequest,
    )
}
