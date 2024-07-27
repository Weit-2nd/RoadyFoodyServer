package kr.weit.roadyfoody.review.presentation.spec

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import kr.weit.roadyfoody.auth.security.LoginUser
import kr.weit.roadyfoody.common.dto.SliceResponse
import kr.weit.roadyfoody.common.exception.ErrorCode
import kr.weit.roadyfoody.foodSpots.validator.WebPImageList
import kr.weit.roadyfoody.global.swagger.ApiErrorCodeExamples
import kr.weit.roadyfoody.global.swagger.v1.SwaggerTag
import kr.weit.roadyfoody.review.application.dto.FoodSpotsReviewResponse
import kr.weit.roadyfoody.review.application.dto.ReviewDetailResponse
import kr.weit.roadyfoody.review.application.dto.ReviewRequest
import kr.weit.roadyfoody.review.application.dto.UserReviewResponse
import kr.weit.roadyfoody.review.repository.ReviewSortType
import kr.weit.roadyfoody.review.utils.SliceFoodSpotsReview
import kr.weit.roadyfoody.review.utils.SliceUserReview
import kr.weit.roadyfoody.user.domain.User
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile

@Tag(name = SwaggerTag.REVIEW)
interface ReviewControllerSpec {
    @Operation(
        description = "리뷰 생성 API",
        responses = [
            ApiResponse(
                responseCode = "201",
                description = "리뷰 등록 성공",
            ),
        ],
    )
    @ApiErrorCodeExamples(
        [
            ErrorCode.FOOD_SPOT_ID_NON_POSITIVE,
            ErrorCode.REVIEW_CONTENT_BLANK,
            ErrorCode.REVIEW_CONTENT_LENGTH_TOO_LONG,
            ErrorCode.REVIEW_RATING_NEGATIVE,
            ErrorCode.REVIEW_RATING_TOO_HIGH,
            ErrorCode.IMAGES_TOO_MANY,
            ErrorCode.INVALID_IMAGE_TYPE,
            ErrorCode.IMAGES_SIZE_TOO_LARGE,
            ErrorCode.NOT_FOUND_FOOD_SPOTS,
        ],
    )
    fun createReview(
        @LoginUser
        user: User,
        @Valid
        @RequestPart
        reviewRequest: ReviewRequest,
        @Size(max = 3, message = "이미지는 최대 3개까지 업로드할 수 있습니다.")
        @WebPImageList
        @RequestPart(required = false)
        reviewPhotos: List<MultipartFile>?,
    )

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
        description = "음식점의 리뷰 리스트 조회 API",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "리뷰 리스트 조회 성공",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema =
                            Schema(
                                implementation = SliceFoodSpotsReview::class,
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
            ErrorCode.FOOD_SPOT_ID_NON_POSITIVE,
        ],
    )
    fun getFoodSpotsReviews(
        @PathVariable("foodSpotsId")
        @Positive(message = "음식점 ID는 양수여야 합니다.")
        foodSpotsId: Long,
        @Positive(message = "조회할 개수는 양수여야 합니다.")
        @RequestParam(defaultValue = "10", required = false)
        size: Int,
        @Positive(message = "마지막 ID는 양수여야 합니다.")
        @RequestParam(required = false)
        lastId: Long?,
        @RequestParam(defaultValue = "LATEST", required = false)
        sortType: ReviewSortType,
    ): SliceResponse<FoodSpotsReviewResponse>

    @Operation(
        description = "리뷰 상세 조회 API",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "리뷰 상세 조회 성공",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema =
                            Schema(
                                implementation = ReviewDetailResponse::class,
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
            ErrorCode.NOT_FOUND_FOOD_SPOTS_REVIEW,
        ],
    )
    fun getReviewDetail(
        @PathVariable("reviewId")
        reviewId: Long,
    ): ReviewDetailResponse
}
