package kr.weit.roadyfoody.review.application.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import kr.weit.roadyfoody.foodSpots.domain.FoodSpots
import kr.weit.roadyfoody.review.domain.FoodSpotsReview
import kr.weit.roadyfoody.user.domain.User
import org.hibernate.validator.constraints.Length

data class ReviewRequest(
    @Schema(description = "음식점 ID")
    @field:Positive(message = "음식점 ID는 양수여야 합니다.")
    val foodSpotId: Long,
    @Schema(description = "리뷰 내용")
    @field:NotBlank(message = "리뷰는 필수 입력값입니다.")
    @field:Length(max = 1200, message = "리뷰 최대 길이를 초과했습니다.")
    val contents: String,
    @Schema(description = "별점")
    @field:Min(1, message = "별점은 1점 이상으로 입력해주세요.")
    @field:Max(5, message = "별점은 5점 이하로 입력해주세요.")
    val rating: Int,
) {
    fun toEntity(
        user: User,
        foodSpot: FoodSpots,
    ): FoodSpotsReview = FoodSpotsReview(id = 0L, foodSpot, user, rating, contents, 0)
}

data class ReviewUpdateRequest(
    @Schema(description = "리뷰 내용(수정할 내용이 없으면 null)")
    @field:NotBlank(message = "리뷰는 필수 입력값입니다.")
    @field:Length(max = 1200, message = "리뷰 최대 길이를 초과했습니다.")
    val contents: String?,
    @Schema(description = "별점(수정할 내용이 없으면 null)")
    @field:Min(1, message = "별점은 1점 이상으로 입력해주세요.")
    @field:Max(5, message = "별점은 5점 이하로 입력해주세요.")
    val rating: Int?,
    @Schema(description = "삭제할 리뷰 사진 ID 목록(수정할 내용이 없으면 null)")
    val deletePhotoIds: Set<Long>?,
)

data class ReviewPhotoResponse(
    @Schema(description = "리뷰 사진 ID", example = "1")
    val id: Long,
    @Schema(description = "리뷰 사진 URL")
    val url: String,
)
