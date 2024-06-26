package kr.weit.roadyfoody.term.application.service

import kr.weit.roadyfoody.term.application.dto.DetailedTermResponse
import kr.weit.roadyfoody.term.application.dto.SummaryTermsResponse
import kr.weit.roadyfoody.term.repository.TermRepository
import kr.weit.roadyfoody.term.repository.getByTermId
import org.springframework.stereotype.Service

@Service
class TermQueryService(
    private val termRepository: TermRepository,
) {
    fun getAllSummaryTerms(): SummaryTermsResponse = SummaryTermsResponse.from(termRepository.findAll())

    fun getDetailedTerm(termId: Long): DetailedTermResponse = DetailedTermResponse.from(termRepository.getByTermId(termId))
}
