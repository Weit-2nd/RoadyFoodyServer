package kr.weit.roadyfoody.review.presentation.api

import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import kr.weit.roadyfoody.auth.security.LoginUser
import kr.weit.roadyfoody.common.dto.SliceResponse
import kr.weit.roadyfoody.foodSpots.validator.WebPImageList
import kr.weit.roadyfoody.review.application.dto.FoodSpotsReviewResponse
import kr.weit.roadyfoody.review.application.dto.ReviewDetailResponse
import kr.weit.roadyfoody.review.application.dto.ReviewRequest
import kr.weit.roadyfoody.review.application.dto.UserReviewResponse
import kr.weit.roadyfoody.review.application.service.ReviewCommandService
import kr.weit.roadyfoody.review.application.service.ReviewQueryService
import kr.weit.roadyfoody.review.presentation.spec.ReviewControllerSpec
import kr.weit.roadyfoody.review.repository.ReviewSortType
import kr.weit.roadyfoody.user.domain.User
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/review")
class ReviewController(
    private val reviewCommandService: ReviewCommandService,
    private val reviewQueryService: ReviewQueryService,
) : ReviewControllerSpec {
    @ResponseStatus(CREATED)
    @PostMapping(consumes = [MULTIPART_FORM_DATA_VALUE])
    override fun createReview(
        @LoginUser
        user: User,
        @Valid
        @RequestPart
        reviewRequest: ReviewRequest,
        @Size(max = 3, message = "이미지는 최대 3개까지 업로드할 수 있습니다.")
        @WebPImageList
        @RequestPart(required = false)
        reviewPhotos: List<MultipartFile>?,
    ) {
        reviewCommandService.createReview(user, reviewRequest, reviewPhotos)
    }

    @GetMapping("/user/{userId}")
    override fun getUserReviews(
        @PathVariable("userId")
        @Positive(message = "유저 ID는 양수여야 합니다.")
        userId: Long,
        @Positive(message = "조회할 개수는 양수여야 합니다.")
        @RequestParam(defaultValue = "10", required = false)
        size: Int,
        @Positive(message = "마지막 ID는 양수여야 합니다.")
        @RequestParam(required = false)
        lastId: Long?,
    ): SliceResponse<UserReviewResponse> = reviewQueryService.getUserReviews(userId, size, lastId)

    @GetMapping("/food-spots/{foodSpotsId}")
    override fun getFoodSpotsReviews(
        @PathVariable("foodSpotsId")
        @Positive(message = "음식점 ID는 양수여야 합니다.")
        foodSpotsId: Long,
        @Positive(message = "조회할 개수는 양수여야 합니다.")
        @RequestParam(defaultValue = "10", required = false)
        size: Int,
        @Positive(message = "마지막 ID는 양수여야 합니다.")
        @RequestParam(required = false)
        lastId: Long?,
        @RequestParam(required = false, defaultValue = "LATEST")
        sortType: ReviewSortType,
    ): SliceResponse<FoodSpotsReviewResponse> = reviewQueryService.getFoodSpotsReview(foodSpotsId, size, lastId, sortType)

    @GetMapping("/{reviewId}")
    override fun getReviewDetail(
        @PathVariable
        reviewId: Long,
    ): ReviewDetailResponse = reviewQueryService.getReviewDetail(reviewId)

    @ResponseStatus(NO_CONTENT)
    @DeleteMapping("/{reviewId}")
    override fun deleteFoodSpotsReviews(
        @LoginUser
        user: User,
        @Positive(message = "리뷰 ID는 양수여야 합니다.")
        @PathVariable("reviewId")
        reviewId: Long,
    ) = reviewCommandService.deleteReview(user, reviewId)
}
