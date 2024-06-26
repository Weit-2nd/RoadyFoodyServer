package kr.weit.roadyfoody.term.application.dto

import kr.weit.roadyfoody.term.domain.Term

data class SummaryTermsResponse(
    val terms: List<SummaryTermResponse>,
) {
    companion object {
        fun from(terms: List<Term>): SummaryTermsResponse =
            SummaryTermsResponse(
                terms.map { SummaryTermResponse.from(it) },
            )
    }
}

data class SummaryTermResponse(
    val id: Long,
    val title: String,
    val isRequired: Boolean,
) {
    companion object {
        fun from(term: Term): SummaryTermResponse =
            SummaryTermResponse(
                term.id,
                term.title,
                term.required,
            )
    }
}
