package kr.weit.roadyfoody.term.fixture

import kr.weit.roadyfoody.term.application.dto.DetailedTermResponse
import kr.weit.roadyfoody.term.application.dto.SummaryTermsResponse
import kr.weit.roadyfoody.term.domain.Term

fun createTestSummaryTermsResponse(terms: List<Term>) = SummaryTermsResponse.from(terms)

fun createTestDetailedTermResponse(id: Long) = DetailedTermResponse.from(createTestRequiredTerm(id))
