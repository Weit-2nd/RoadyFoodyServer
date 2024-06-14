package kr.weit.roadyfoody.term.fixture

import kr.weit.roadyfoody.term.service.dto.DetailedTermResponse
import kr.weit.roadyfoody.term.service.dto.DetailedTermsResponse
import kr.weit.roadyfoody.term.service.dto.SummaryTermsResponse

fun createTestSummaryTermsResponse() =
    SummaryTermsResponse.from(
        TEST_TERMS_SIZE,
        TEST_REQUIRED_TERMS_SIZE,
        TEST_OPTIONAL_TERMS_SIZE,
        createTestTerms(),
    )

fun createTestDetailedTermResponse() =
    DetailedTermResponse.from(
        createTestRequiredTerm1(),
    )

fun createTestDetailedTermsResponse() =
    DetailedTermsResponse.from(
        TEST_TERMS_SIZE,
        TEST_REQUIRED_TERMS_SIZE,
        TEST_OPTIONAL_TERMS_SIZE,
        createTestTerms(),
    )
