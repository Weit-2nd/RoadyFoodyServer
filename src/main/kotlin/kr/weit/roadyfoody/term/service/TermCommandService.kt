package kr.weit.roadyfoody.term.service

import kr.weit.roadyfoody.term.repository.TermRepository
import kr.weit.roadyfoody.useragreedterm.exception.RequiredTermNotAgreedException
import org.springframework.stereotype.Service

@Service
class TermCommandService(
    private val termRepository: TermRepository,
) {
    fun checkRequiredTermsOrThrow(agreedTermIdSet: Set<Long>) {
        val requiredTermIdSet = termRepository.findAllIdsByRequiredIsTrue()
        val notAgreedTermIdSet = requiredTermIdSet.subtract(agreedTermIdSet)

        if (notAgreedTermIdSet.isNotEmpty()) {
            throw RequiredTermNotAgreedException(notAgreedTermIdSet)
        }
    }
}
