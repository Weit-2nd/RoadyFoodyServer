package kr.weit.roadyfoody.user.presentation.spec

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Past
import jakarta.validation.constraints.Positive
import kr.weit.roadyfoody.auth.security.LoginUser
import kr.weit.roadyfoody.common.dto.SliceResponse
import kr.weit.roadyfoody.common.exception.ErrorCode
import kr.weit.roadyfoody.common.exception.ErrorResponse
import kr.weit.roadyfoody.global.swagger.ApiErrorCodeExamples
import kr.weit.roadyfoody.global.swagger.v1.SwaggerTag
import kr.weit.roadyfoody.global.validator.MaxFileSize
import kr.weit.roadyfoody.global.validator.WebPImage
import kr.weit.roadyfoody.user.application.dto.UserInfoResponse
import kr.weit.roadyfoody.user.application.dto.UserLikedReviewResponse
import kr.weit.roadyfoody.user.application.dto.UserNicknameRequest
import kr.weit.roadyfoody.user.application.dto.UserReportHistoriesResponse
import kr.weit.roadyfoody.user.application.dto.UserReviewResponse
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.utils.SliceReportHistories
import kr.weit.roadyfoody.user.utils.SliceUserLike
import kr.weit.roadyfoody.user.utils.SliceUserReview
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

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

    @Operation(
        description = "유저의 리뷰 리스트 조회 API",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "리뷰 리스트 조회 성공",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema =
                            Schema(
                                implementation = SliceUserReview::class,
                            ),
                    ),
                ],
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.SIZE_NON_POSITIVE,
            ErrorCode.LAST_ID_NON_POSITIVE,
            ErrorCode.USER_ID_NON_POSITIVE,
        ],
    )
    fun getUserReviews(
        @PathVariable("userId")
        @Positive(message = "유저 ID는 양수여야 합니다.")
        userId: Long,
        @Positive(message = "조회할 개수는 양수여야 합니다.")
        @RequestParam(defaultValue = "10", required = false)
        size: Int,
        @Positive(message = "마지막 ID는 양수여야 합니다.")
        @RequestParam(required = false)
        lastId: Long?,
    ): SliceResponse<UserReviewResponse>

    @Operation(
        description = "유저의 닉네임 변경 API",
        responses = [
            ApiResponse(
                responseCode = "204",
                description = "닉네임 변경 성공",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.NICKNAME_ALREADY_EXISTS,
            ErrorCode.INVALID_NICKNAME,
        ],
    )
    fun updateNickname(
        @LoginUser
        user: User,
        @RequestBody
        @Valid
        userNicknameRequest: UserNicknameRequest,
    )

    @Operation(
        description = "유저의 프로필 이미지 변경 API",
        responses = [
            ApiResponse(
                responseCode = "204",
                description = "프로필 이미지 변경 성공",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.INVALID_WEBP_IMAGE,
            ErrorCode.MAX_FILE_SIZE_EXCEEDED,
        ],
    )
    fun updateProfileImage(
        @LoginUser
        user: User,
        @RequestPart
        @MaxFileSize
        @WebPImage
        profileImage: MultipartFile,
    )

    @Operation(
        description = "유저의 프로필 이미지 삭제 API",
        responses = [
            ApiResponse(
                responseCode = "204",
                description = "프로필 이미지 삭제 성공",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.PROFILE_IMAGE_NOT_EXISTS,
        ],
    )
    fun deleteProfile(
        @LoginUser user: User,
    )

    @Operation(
        description = "유저 좋아요 누른 게시물 조회 API",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "유저 좋아요 누른 게시물 조회 성공",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema =
                            Schema(
                                implementation = SliceUserLike::class,
                            ),
                    ),
                ],
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.SIZE_NON_POSITIVE,
            ErrorCode.USER_ID_NON_POSITIVE,
            ErrorCode.LAST_TIME_FUTURE_OR_PRESENT,
            ErrorCode.NOT_FOUND_USER,
        ],
    )
    fun getUserLikeReviews(
        @PathVariable("userId")
        @Positive(message = "유저 ID는 양수여야 합니다.")
        userId: Long,
        @Positive(message = "조회할 개수는 양수여야 합니다.")
        @RequestParam(defaultValue = "10", required = false)
        size: Int,
        @RequestParam(required = false)
        @Past(message = "마지막 시간은 현재 시간 이전이어야 합니다.")
        lastTime: LocalDateTime?,
    ): SliceResponse<UserLikedReviewResponse>
}
