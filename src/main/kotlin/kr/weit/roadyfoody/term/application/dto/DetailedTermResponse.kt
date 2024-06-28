package kr.weit.roadyfoody.term.application.dto

import kr.weit.roadyfoody.term.domain.Term

data class DetailedTermResponse(
    val id: Long,
    val title: String,
    val isRequired: Boolean,
    val content: String,
) {
    companion object {
        fun from(term: Term): DetailedTermResponse =
            DetailedTermResponse(
                term.id,
                term.title,
                term.required,
                term.content,
            )
    }
}
