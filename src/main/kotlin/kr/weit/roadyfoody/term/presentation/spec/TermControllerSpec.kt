package kr.weit.roadyfoody.term.presentation.spec

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import kr.weit.roadyfoody.global.swagger.v1.SwaggerTag
import kr.weit.roadyfoody.term.service.dto.DetailedTermResponse
import kr.weit.roadyfoody.term.service.dto.DetailedTermsResponse
import kr.weit.roadyfoody.term.service.dto.SummaryTermsResponse

@Tag(name = SwaggerTag.TERM)
interface TermControllerSpec {
    @Operation(description = "모든 약관의 간략한 정보 (약관 수, 약관 제목) 반환 API")
    fun getAllSummaryTerms(): SummaryTermsResponse

    @Operation(description = "모든 약관의 상세 정보 반환 API")
    fun getAllDetailedTerms(): DetailedTermsResponse

    @Operation(
        description = "단일 약관의 상세 정보 반환 API",
        parameters = [
            Parameter(name = "termId", description = "요청 약관 ID", required = true, example = "1"),
        ],
    )
    fun getDetailedTerm(termId: Long): DetailedTermResponse
}
