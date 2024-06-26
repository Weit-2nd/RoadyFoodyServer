package kr.weit.roadyfoody.term.presentation.spec

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import kr.weit.roadyfoody.common.exception.ErrorResponse
import kr.weit.roadyfoody.global.swagger.v1.SwaggerTag
import kr.weit.roadyfoody.term.application.dto.DetailedTermResponse
import kr.weit.roadyfoody.term.application.dto.SummaryTermsResponse
import org.springframework.http.MediaType

@Tag(name = SwaggerTag.TERM)
interface TermControllerSpec {
    @Operation(
        summary = "모든 약관 간략 정보 조회 API",
        description = "모든 약관의 간략한 정보 (약관 ID, 약관 제목, 필수여부) 반환 API",
    )
    fun getAllSummaryTerms(): SummaryTermsResponse

    @Operation(
        summary = "단일 약관 상세 정보 조회 API",
        description = "단일 약관의 상세 정보 반환 API",
        parameters = [
            Parameter(name = "termId", description = "요청 약관 ID", required = true, example = "1"),
        ],
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "약관 상세 정보 반환 성공",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = DetailedTermResponse::class),
                        examples = [
                            ExampleObject(
                                name = "약관 상세 정보 반환 성공",
                                summary = "약관 상세 정보 반환 성공",
                                value = """
                            {
                                "id": 1,
                                "title": "약관 제목",
                                "isRequired": true,
                                "content": "<html>약관 내용</html>"
                            }
                            """,
                            ),
                        ],
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "약관 상세 정보 반환 실패",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "약관 상세 정보 반환 실패",
                                summary = "약관 상세 정보 반환 실패",
                                value = """
                        {
                            "code": -10010,
                            "errorMessage": "약관 ID: 0 약관을 찾을 수 없습니다."
                        }
                        """,
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    fun getDetailedTerm(termId: Long): DetailedTermResponse
}
