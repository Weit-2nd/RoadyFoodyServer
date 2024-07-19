package kr.weit.roadyfoody.admin.presentation.spec

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import kr.weit.roadyfoody.admin.dto.SimpleUserInfoResponses
import kr.weit.roadyfoody.admin.dto.UserAccessTokenResponse
import kr.weit.roadyfoody.common.exception.ErrorResponse
import kr.weit.roadyfoody.global.swagger.v1.SwaggerTag
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType

@Tag(name = SwaggerTag.ADMIN)
interface AdminControllerSpec {
    @Operation(
        summary = "유저 정보 조회",
        description = "유저 정보를 조회합니다.",
        parameters = [
            Parameter(
                name = "page",
                description = "페이지 정보",
                `in` = ParameterIn.QUERY,
                required = true,
                schema =
                    Schema(
                        type = "integer",
                        example = "0",
                    ),
            ),
            Parameter(
                name = "size",
                description = "페이지 사이즈",
                `in` = ParameterIn.QUERY,
                required = true,
                schema =
                    Schema(
                        type = "integer",
                        example = "10",
                    ),
            ),
        ],
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "유저 정보 조회 성공",
            ),
        ],
    )
    fun getUserInfoList(
        @Parameter(hidden = true)
        pageable: Pageable,
    ): SimpleUserInfoResponses

    @Operation(
        summary = "유저 AccessToken 조회",
        description = "유저 AccessToken 을 조회합니다. (유효기간 10일 이상)",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "유저 AccessToken 조회 성공",
            ),
            ApiResponse(
                responseCode = "404",
                description = "리포트 리스트 조회 실패",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "Not found user",
                                summary = "NOT_FOUND_USER",
                                value = """
                                {
                                    "code": -10009,
                                    "errorMessage": "10 ID 의 사용자는 존재하지 않습니다."
                                }
                            """,
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    fun getUserAccessToken(
        @Parameter(description = "유저 아이디", required = true, example = "1")
        userId: Long,
    ): UserAccessTokenResponse
}
