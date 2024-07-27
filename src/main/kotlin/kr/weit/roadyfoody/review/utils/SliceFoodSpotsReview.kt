package kr.weit.roadyfoody.review.utils

import io.swagger.v3.oas.annotations.media.Schema
import kr.weit.roadyfoody.common.dto.SliceResponse
import kr.weit.roadyfoody.review.application.dto.FoodSpotsReviewResponse

class SliceFoodSpotsReview(
    @Schema(description = "조회된 데이터 리스트")
    contents: List<FoodSpotsReviewResponse>,
    @Schema(description = "다음 페이지 존재 여부")
    hasNext: Boolean,
) : SliceResponse<FoodSpotsReviewResponse>(contents, hasNext)
