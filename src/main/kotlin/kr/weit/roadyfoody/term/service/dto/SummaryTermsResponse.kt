package kr.weit.roadyfoody.term.service.dto

import com.fasterxml.jackson.annotation.JsonProperty
import kr.weit.roadyfoody.term.domain.Term

data class SummaryTermsResponse(
    @JsonProperty("all_terms_size")
    val allTermsSize: Int,
    @JsonProperty("required_term_size")
    val requiredTermsSize: Int,
    @JsonProperty("optional_term_size")
    val optionalTermsSize: Int,
    val terms: List<SummaryTermResponse>,
) {
    companion object {
        @JvmStatic
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
    @JsonProperty("required_flag")
    val requiredFlag: Boolean,
) {
    companion object {
        @JvmStatic
        fun from(term: Term): SummaryTermResponse =
            SummaryTermResponse(
                term.title,
                term.requiredFlag,
            )
    }
}
