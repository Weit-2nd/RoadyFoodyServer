package kr.weit.roadyfoody.term.service.dto

import com.fasterxml.jackson.annotation.JsonProperty
import kr.weit.roadyfoody.term.domain.Term

data class DetailedTermsResponse(
    @JsonProperty("all_terms_size")
    val allTermsSize: Int,
    @JsonProperty("required_term_size")
    val requiredTermsSize: Int,
    @JsonProperty("optional_term_size")
    val optionalTermsSize: Int,
    val terms: List<DetailedTermResponse>,
) {
    companion object {
        @JvmStatic
        fun from(
            allTermsSize: Int,
            requiredTermsSize: Int,
            optionalTermsSize: Int,
            terms: List<Term>,
        ): DetailedTermsResponse =
            DetailedTermsResponse(
                allTermsSize,
                requiredTermsSize,
                optionalTermsSize,
                terms.map { DetailedTermResponse.from(it) },
            )
    }
}

data class DetailedTermResponse(
    val id: Long,
    val title: String,
    @JsonProperty("required_flag")
    val requiredFlag: Boolean,
    val content: String,
) {
    companion object {
        @JvmStatic
        fun from(term: Term): DetailedTermResponse =
            DetailedTermResponse(
                term.id,
                term.title,
                term.requiredFlag,
                term.content,
            )
    }
}
