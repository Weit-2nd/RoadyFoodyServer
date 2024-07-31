package kr.weit.roadyfoody.user.application.dto

import io.swagger.v3.oas.annotations.media.Schema
import kr.weit.roadyfoody.review.domain.FoodSpotsReview
import kr.weit.roadyfoody.user.domain.User
import java.time.LocalDateTime

data class ReviewerInfoResponse(
    @Schema(description = "유저 id", example = "1")
    val id: Long,
    @Schema(description = "유저 닉네임", example = "TestNickname")
    val nickname: String,
    @Schema(description = "프로필 URL")
    val url: String?,
) {
    companion object {
        fun of(
            user: User,
            url: String?,
        ) = ReviewerInfoResponse(user.id, user.profile.nickname, url)
    }
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
