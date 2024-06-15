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
        val (requiredSize, optionalSize) = getRequiredAndOptionalSizes(terms)
        return SummaryTermsResponse.from(terms.size, requiredSize, optionalSize, termRepository.findAll())
    }

    fun getAllDetailedTerms(): DetailedTermsResponse {
        val terms = termRepository.findAll()
        val (requiredSize, optionalSize) = getRequiredAndOptionalSizes(terms)
        return DetailedTermsResponse.from(terms.size, requiredSize, optionalSize, termRepository.findAll())
    }

    fun getDetailedTerm(termId: Long): DetailedTermResponse = DetailedTermResponse.from(termRepository.getByTermId(termId))

    private fun getRequiredAndOptionalSizes(terms: List<Term>): RequiredAndOptionalSizes {
        val requiredSize = terms.count { it.requiredFlag }
        return RequiredAndOptionalSizes(requiredSize, terms.size - requiredSize)
    }
}

data class RequiredAndOptionalSizes(
    val requiredSize: Int,
    val optionalSize: Int,
)
