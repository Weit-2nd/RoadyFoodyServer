package kr.weit.roadyfoody.term.fixture

import kr.weit.roadyfoody.term.service.dto.DetailedTermResponse
import kr.weit.roadyfoody.term.service.dto.SummaryTermsResponse

fun createTestSummaryTermsResponse() = SummaryTermsResponse.from(createTestTerms())

fun createTestDetailedTermResponse(id: Long) = DetailedTermResponse.from(createTestRequiredTerm(id))

// fail case
fun createTestZerosSummaryTermsResponse() = SummaryTermsResponse.from(emptyList())
