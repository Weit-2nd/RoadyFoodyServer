package kr.weit.roadyfoody.term.service.dto

import kr.weit.roadyfoody.term.domain.Term

data class SummaryTermsResponse(
    val allTermsSize: Int,
    val requiredTermsSize: Int,
    val optionalTermsSize: Int,
    val terms: List<SummaryTermResponse>,
) {
    companion object {
        fun from(
            allTermsSize: Int,
            requiredTermsSize: Int,
            optionalTermsSize: Int,
            terms: List<Term>,
        ): SummaryTermsResponse =
            SummaryTermsResponse(
                allTermsSize,
                requiredTermsSize,
                optionalTermsSize,
                terms.map { SummaryTermResponse.from(it) },
            )
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
