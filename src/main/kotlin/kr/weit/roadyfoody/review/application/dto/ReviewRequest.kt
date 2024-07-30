package kr.weit.roadyfoody.review.application.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import kr.weit.roadyfoody.foodSpots.domain.FoodSpots
import kr.weit.roadyfoody.review.domain.FoodSpotsReview
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.dto.UserSimpleInfoResponse
import org.hibernate.validator.constraints.Length
import java.time.LocalDateTime

data class ReviewRequest(
    @Schema(description = "음식점 ID")
    @field:Positive(message = "음식점 ID는 양수여야 합니다.")
    val foodSpotId: Long,
    @Schema(description = "리뷰 내용")
    @field:NotBlank(message = "리뷰는 필수 입력값입니다.")
    @field:Length(max = 1200, message = "리뷰 최대 길이를 초과했습니다.")
    val contents: String,
    @Schema(description = "별점")
    @field:Min(0, message = "별점은 0점 이상으로 입력해주세요.")
    @field:Max(10, message = "별점은 10점 이하로 입력해주세요.")
    val rating: Int,
) {
    fun toEntity(
        user: User,
        foodSpot: FoodSpots,
    ): FoodSpotsReview = FoodSpotsReview(id = 0L, foodSpot, user, rating, contents)
}

data class UserReviewResponse(
    @Schema(description = "리뷰 ID")
    val id: Long,
    @Schema(description = "리뷰 내용")
    val contents: String,
    @Schema(description = "별점")
    val rate: Int,
    @Schema(description = "리뷰 작성일")
    val createdAt: LocalDateTime,
) {
    constructor(review: FoodSpotsReview) : this(
        id = review.id,
        contents = review.contents,
        rate = review.rate,
        createdAt = review.createdDateTime,
    )
}

data class FoodSpotsReviewResponse(
    @Schema(description = "리뷰 ID")
    val id: Long,
    @Schema(description = "직성지 정보")
    val userInfo: UserSimpleInfoResponse,
    @Schema(description = "리뷰 내용")
    val contents: String,
    @Schema(description = "별점")
    val rate: Int,
    @Schema(description = "사진 리스트")
    val photos: List<ReviewPhotoResponse>,
    @Schema(description = "리뷰 작성일")
    val createdAt: LocalDateTime,
) {
    constructor(
        review: FoodSpotsReview,
        userInfo: UserSimpleInfoResponse,
        photoList: List<ReviewPhotoResponse>,
    ) : this(
        id = review.id,
        userInfo = userInfo,
        contents = review.contents,
        rate = review.rate,
        photos = photoList,
        createdAt = review.createdDateTime,
    )
}

data class ReviewDetailResponse(
    @Schema(description = "리뷰 ID")
    val id: Long,
    @Schema(description = "음식점 ID")
    val foodSpotId: Long,
    @Schema(description = "음식점 이름")
    val foodSpotName: String,
    @Schema(description = "리뷰 내용")
    val contents: String,
    @Schema(description = "별점")
    val rate: Int,
    @Schema(description = "사진 리스트")
    val photos: List<ReviewPhotoResponse>,
    @Schema(description = "리뷰 작성일")
    val createdAt: LocalDateTime,
) {
    constructor(review: FoodSpotsReview, photoList: List<ReviewPhotoResponse>) : this(
        id = review.id,
        foodSpotId = review.foodSpots.id,
        foodSpotName = review.foodSpots.name,
        contents = review.contents,
        rate = review.rate,
        photos = photoList,
        createdAt = review.createdDateTime,
    )
}

data class ReviewPhotoResponse(
    @Schema(description = "리뷰 사진 ID", example = "1")
    val id: Long,
    @Schema(description = "리뷰 사진 URL")
    val url: String,
)
