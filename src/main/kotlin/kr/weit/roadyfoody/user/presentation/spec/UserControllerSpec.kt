package kr.weit.roadyfoody.user.presentation.spec

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Positive
import kr.weit.roadyfoody.common.dto.SliceResponse
import kr.weit.roadyfoody.common.exception.ErrorCode
import kr.weit.roadyfoody.common.exception.ErrorResponse
import kr.weit.roadyfoody.global.swagger.ApiErrorCodeExamples
import kr.weit.roadyfoody.global.swagger.v1.SwaggerTag
import kr.weit.roadyfoody.user.application.dto.UserInfoResponse
import kr.weit.roadyfoody.user.application.dto.UserReportHistoriesResponse
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.utils.SliceReportHistories
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

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

    @Operation(
        description = "음식점 정보 리스트 조회 API",
        parameters = [
            Parameter(name = "userId", description = "유저 ID", required = true, example = "1"),
            Parameter(name = "size", description = "조회할 개수", required = false, example = "10"),
            Parameter(name = "lastId", description = "마지막으로 조회된 ID", required = false, example = "1"),
        ],
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "리포트 리스트 조회 성공",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema =
                            Schema(
                                implementation = SliceReportHistories::class,
                            ),
                    ),
                ],
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
    @ApiErrorCodeExamples(
        [
            ErrorCode.SIZE_NON_POSITIVE,
            ErrorCode.LAST_ID_NON_POSITIVE,
        ],
    )
    fun getReportHistories(
        @PathVariable("userId")
        userId: Long,
        @Positive(message = "조회할 개수는 양수여야 합니다.")
        @RequestParam(defaultValue = "10", required = false)
        size: Int,
        @Positive(message = "마지막 ID는 양수여야 합니다.")
        @RequestParam(required = false)
        lastId: Long?,
    ): SliceResponse<UserReportHistoriesResponse>
}
