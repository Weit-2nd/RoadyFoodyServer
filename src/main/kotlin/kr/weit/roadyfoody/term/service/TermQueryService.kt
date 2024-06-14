package kr.weit.roadyfoody.term.service

import kr.weit.roadyfoody.term.domain.Term
import kr.weit.roadyfoody.term.repository.TermRepository
import kr.weit.roadyfoody.term.repository.getByTermId
import kr.weit.roadyfoody.term.service.dto.DetailedTermResponse
import kr.weit.roadyfoody.term.service.dto.DetailedTermsResponse
import kr.weit.roadyfoody.term.service.dto.SummaryTermsResponse
import org.springframework.stereotype.Service

@Service
class TermQueryService(
    private val termRepository: TermRepository,
) {
    fun getAllSummaryTerms(): SummaryTermsResponse {
        val terms = termRepository.findAll()
        val (allTermsSize, requiredSize, optionalSize) = calculateEachTypeSizes(terms)
        return SummaryTermsResponse.from(allTermsSize, requiredSize, optionalSize, termRepository.findAll())
    }

    fun getAllDetailedTerms(): DetailedTermsResponse {
        val terms = termRepository.findAll()
        val (allTermsSize, requiredSize, optionalSize) = calculateEachTypeSizes(terms)
        return DetailedTermsResponse.from(allTermsSize, requiredSize, optionalSize, termRepository.findAll())
    }

    fun getDetailedTerm(termId: Long): DetailedTermResponse = DetailedTermResponse.from(termRepository.getByTermId(termId))

    private fun calculateEachTypeSizes(terms: List<Term>): Triple<Int, Int, Int> {
        val requiredSize = terms.count { it.requiredFlag }
        return Triple(terms.size, requiredSize, terms.size - requiredSize)
    }
}
