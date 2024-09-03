package kr.weit.roadyfoody.review.presentation.spec

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Positive
import kr.weit.roadyfoody.auth.security.LoginUser
import kr.weit.roadyfoody.common.exception.ErrorCode
import kr.weit.roadyfoody.global.swagger.ApiErrorCodeExamples
import kr.weit.roadyfoody.global.swagger.v1.SwaggerTag
import kr.weit.roadyfoody.review.application.dto.ToggleLikeResponse
import kr.weit.roadyfoody.user.domain.User
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PathVariable

@Tag(name = SwaggerTag.LIKE)
interface ReviewLikeControllerSpec {
    @Operation(
        description = "리뷰 좋아요 토글 API",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "리뷰 좋아요 토글 성공",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ToggleLikeResponse::class),
                    ),
                ],
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.REVIEW_ID_NON_POSITIVE,
            ErrorCode.NOT_FOUND_FOOD_SPOTS_REVIEW,
            ErrorCode.NEGATIVE_NUMBER_OF_LIKED,
        ],
    )
    fun toggleLike(
        @LoginUser
        user: User,
        @Positive(message = "리뷰 ID는 양수여야 합니다.")
        @PathVariable("reviewId")
        reviewId: Long,
    ): ToggleLikeResponse
}
