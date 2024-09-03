package kr.weit.roadyfoody.review.application.dto

import io.swagger.v3.oas.annotations.media.Schema

data class ToggleLikeResponse(
    @Schema(description = "리뷰 ID", example = "1")
    val reviewId: Long,
    @Schema(description = "리뷰 좋아요 수", example = "1")
    val likeTotal: Int,
    @Schema(description = "좋아요 여부(false는 좋아요 취소)", example = "true")
    val liked: Boolean,
)
