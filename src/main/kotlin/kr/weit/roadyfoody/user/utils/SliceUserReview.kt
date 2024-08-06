package kr.weit.roadyfoody.user.utils

import io.swagger.v3.oas.annotations.media.Schema
import kr.weit.roadyfoody.common.dto.SliceResponse
import kr.weit.roadyfoody.user.application.dto.UserReviewResponse

class SliceUserReview(
    @Schema(description = "조회된 데이터 리스트")
    contents: List<UserReviewResponse>,
    @Schema(description = "다음 페이지 존재 여부")
    hasNext: Boolean,
) : SliceResponse<UserReviewResponse>(contents, hasNext)
