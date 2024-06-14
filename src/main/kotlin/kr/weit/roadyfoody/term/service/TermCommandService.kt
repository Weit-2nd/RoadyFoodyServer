package kr.weit.roadyfoody.term.service

import kr.weit.roadyfoody.term.repository.TermRepository
import kr.weit.roadyfoody.useragreedterm.exception.RequiredTermNotAgreedException
import kr.weit.roadyfoody.useragreedterm.exception.RequiredTermNotAgreedException.Companion.requiredTermNotAgreedMessage
import org.springframework.stereotype.Service

@Service
class TermCommandService(
    private val termRepository: TermRepository,
) {
    fun checkRequiredTermsOrThrow(agreedTermIds: List<Long>) {
        val requiredTermIds = termRepository.findAllIdsByRequiredFlagIsTrue()
        val agreedTermIdSet = agreedTermIds.toSet()

        val notAgreedTermIdSet = requiredTermIds.subtract(agreedTermIdSet)

        if (notAgreedTermIdSet.isNotEmpty()) {
            throw RequiredTermNotAgreedException(requiredTermNotAgreedMessage(notAgreedTermIdSet))
        }
    }
}
