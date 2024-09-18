package kr.weit.roadyfoody.user.utils

import io.swagger.v3.oas.annotations.media.Schema
import kr.weit.roadyfoody.common.dto.SliceResponse
import kr.weit.roadyfoody.user.application.dto.UserLikedReviewResponse

class SliceUserLike(
    @Schema(description = "유저가 좋아요한 리뷰 리스트")
    contents: List<UserLikedReviewResponse>,
    @Schema(description = "다음 페이지 존재 여부")
    hasNext: Boolean,
) : SliceResponse<UserLikedReviewResponse>(contents, hasNext)
