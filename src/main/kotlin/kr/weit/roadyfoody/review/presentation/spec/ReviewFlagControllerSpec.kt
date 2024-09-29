package kr.weit.roadyfoody.review.presentation.spec

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Positive
import kr.weit.roadyfoody.common.exception.ErrorCode
import kr.weit.roadyfoody.global.swagger.ApiErrorCodeExamples
import kr.weit.roadyfoody.global.swagger.v1.SwaggerTag
import kr.weit.roadyfoody.user.domain.User

@Tag(name = SwaggerTag.REVIEW_FLAG)
interface ReviewFlagControllerSpec {
    @Operation(
        description = "리뷰 신고 API",
        responses = [
            ApiResponse(
                responseCode = "203",
                description = "리뷰 신고 성공",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.REVIEW_ID_NON_POSITIVE,
            ErrorCode.NOT_FOUND_FOOD_SPOTS_REVIEW,
            ErrorCode.REVIEW_FLAG_ALREADY_EXISTS,
        ],
    )
    fun flagReview(
        user: User,
        @Positive(message = "리뷰 ID는 양수여야 합니다.")
        reviewId: Long,
    )
}
