package kr.weit.roadyfoody.user.application.dto

import io.swagger.v3.oas.annotations.media.Schema
import kr.weit.roadyfoody.review.application.dto.ReviewPhotoResponse
import kr.weit.roadyfoody.review.domain.FoodSpotsReview
import kr.weit.roadyfoody.review.domain.ReviewLike
import kr.weit.roadyfoody.user.domain.User
import java.time.LocalDateTime

data class ReviewerInfoResponse(
    @Schema(description = "유저 id", example = "1")
    val id: Long,
    @Schema(description = "유저 닉네임", example = "TestNickname")
    val nickname: String,
    @Schema(description = "유저 뱃지", example = "초심자")
    val badge: String,
    @Schema(description = "프로필 URL")
    val url: String?,
) {
    companion object {
        fun of(
            user: User,
            url: String?,
        ) = ReviewerInfoResponse(user.id, user.profile.nickname, user.badge.description, url)
    }
}

data class UserReviewResponse(
    @Schema(description = "리뷰 ID")
    val id: Long,
    @Schema(description = "리뷰 내용")
    val contents: String,
    @Schema(description = "별점")
    val rate: Int,
    @Schema(description = "사진 리스트")
    val photos: List<ReviewPhotoResponse>,
    @Schema(description = "리뷰 작성일")
    val createdAt: LocalDateTime,
    @Schema(description = "좋아요 수")
    val likeTotal: Int,
) {
    constructor(review: FoodSpotsReview, photos: List<ReviewPhotoResponse>) : this(
        id = review.id,
        contents = review.contents,
        rate = review.rate,
        photos = photos,
        createdAt = review.createdDateTime,
        likeTotal = review.likeTotal,
    )
}

data class UserLikedReviewResponse(
    @Schema(description = "리뷰 ID")
    val id: Long,
    @Schema(description = "리뷰 내용")
    val contents: String,
    @Schema(description = "별점")
    val rate: Int,
    @Schema(description = "사진 리스트")
    val photos: List<ReviewPhotoResponse>,
    @Schema(description = "리뷰 작성일")
    val reviewCreatedAt: LocalDateTime,
    @Schema(description = "리뷰 작성자 정보")
    val reviewer: ReviewerInfoResponse,
    @Schema(description = "좋아요 생성일")
    val likeCreatedAt: LocalDateTime,
) {
    constructor(reviewLike: ReviewLike, photos: List<ReviewPhotoResponse>, url: String?) : this(
        id = reviewLike.review.id,
        contents = reviewLike.review.contents,
        rate = reviewLike.review.rate,
        photos = photos,
        reviewCreatedAt = reviewLike.review.createdDateTime,
        reviewer = ReviewerInfoResponse.of(reviewLike.review.user, url),
        likeCreatedAt = reviewLike.createdDateTime,
    )
}
