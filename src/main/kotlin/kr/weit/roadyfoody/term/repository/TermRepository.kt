package kr.weit.roadyfoody.term.repository

import kr.weit.roadyfoody.term.domain.Term
import kr.weit.roadyfoody.term.exception.TermNotFoundException
import kr.weit.roadyfoody.term.exception.TermNotFoundException.Companion.termNotFoundMessage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.findByIdOrNull

fun TermRepository.getByTermId(termId: Long): Term = findByIdOrNull(termId) ?: throw TermNotFoundException(termNotFoundMessage(termId))

interface TermRepository : JpaRepository<Term, Long> {
    fun findAllByIdIn(termIds: List<Long>): List<Term>

    @Query("SELECT t.id FROM Term t WHERE t.requiredFlag = true")
    fun findAllIdsByRequiredFlagIsTrue(): List<Long>
}
