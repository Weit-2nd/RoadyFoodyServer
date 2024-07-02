package kr.weit.roadyfoody.common.dto

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.domain.Slice

open class SliceResponse<T>(
    @Schema(description = "조회된 데이터 리스트")
    val contents: List<T>,
    @Schema(description = "다음 페이지 존재 여부")
    val hasNext: Boolean,
) {
    constructor(contents: Slice<T>) : this(
        contents = contents.content,
        hasNext = contents.hasNext(),
    )
}
