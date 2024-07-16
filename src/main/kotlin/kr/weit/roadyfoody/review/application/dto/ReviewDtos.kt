package kr.weit.roadyfoody.review.application.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
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
    @field:Positive(message = "양수가 아닌 별점은 입력할 수 없습니다.")
    @field:Max(10, message = "별점은 10점 이하로 입력해주세요.")
    val rating: Int,
) {
    fun toEntity(
        user: User,
        foodSpot: FoodSpots,
    ): FoodSpotsReview = FoodSpotsReview(id = 0L, foodSpot, user, rating, contents)
}
