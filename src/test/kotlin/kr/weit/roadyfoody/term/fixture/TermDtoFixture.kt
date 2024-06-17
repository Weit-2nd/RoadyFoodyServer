package kr.weit.roadyfoody.term.fixture

import kr.weit.roadyfoody.term.service.dto.DetailedTermResponse
import kr.weit.roadyfoody.term.service.dto.SummaryTermsResponse

fun createTestSummaryTermsResponse() =
    SummaryTermsResponse.from(
        TEST_TERMS_SIZE,
        TEST_REQUIRED_TERMS_SIZE,
        TEST_OPTIONAL_TERMS_SIZE,
        createTestTerms(),
    )

fun createTestDetailedTermResponse(id: Long) =
    DetailedTermResponse.from(
        createTestRequiredTerm(id),
    )

// fail case
fun createTestZerosSummaryTermsResponse() =
    SummaryTermsResponse.from(
        0,
        0,
        0,
        emptyList(),
    )
