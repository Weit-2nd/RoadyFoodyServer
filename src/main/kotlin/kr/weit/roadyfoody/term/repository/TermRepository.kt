package kr.weit.roadyfoody.term.repository

import kr.weit.roadyfoody.term.domain.Term
import kr.weit.roadyfoody.term.exception.TermNotFoundException
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.findByIdOrNull

fun TermRepository.getByTermId(termId: Long): Term = findByIdOrNull(termId) ?: throw TermNotFoundException(termId)

interface TermRepository : JpaRepository<Term, Long> {
    fun findAllByIdIn(termIdSet: Set<Long>): List<Term>

    @Query("SELECT t.id FROM Term t WHERE t.required = true")
    fun findAllIdsByRequiredIsTrue(): Set<Long>
}
