package kr.weit.roadyfoody.useragreedterm.application.service

import kr.weit.roadyfoody.term.repository.TermRepository
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.useragreedterm.domain.UserAgreedTerm
import kr.weit.roadyfoody.useragreedterm.repository.UserAgreedTermRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserAgreedTermCommandService(
    private val userAgreedTermRepository: UserAgreedTermRepository,
    private val termRepository: TermRepository,
) {
    @Transactional
    fun storeUserAgreedTerms(
        user: User,
        termIdSet: Set<Long>,
    ) {
        val terms = termRepository.findAllByIdIn(termIdSet)
        val userAgreedTerms =
            terms.map { term ->
                UserAgreedTerm(user = user, term = term)
            }
        userAgreedTermRepository.saveAll(userAgreedTerms)
    }
}
