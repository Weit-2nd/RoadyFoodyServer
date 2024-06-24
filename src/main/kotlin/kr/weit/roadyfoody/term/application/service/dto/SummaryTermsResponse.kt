package kr.weit.roadyfoody.term.application.service.dto

import kr.weit.roadyfoody.term.domain.Term

data class SummaryTermsResponse(
    val allTermsSize: Int,
    val requiredTermsSize: Int,
    val optionalTermsSize: Int,
    val terms: List<SummaryTermResponse>,
) {
    companion object {
        fun from(terms: List<Term>): SummaryTermsResponse {
            val requiredTermsSize = terms.count { it.required }
            return SummaryTermsResponse(
                terms.size,
                requiredTermsSize,
                terms.size - requiredTermsSize,
                terms.map { SummaryTermResponse.from(it) },
            )
        }
    }
}

data class SummaryTermResponse(
    val title: String,
    val isRequired: Boolean,
) {
    companion object {
        fun from(term: Term): SummaryTermResponse =
            SummaryTermResponse(
                term.title,
                term.required,
            )
    }
}
