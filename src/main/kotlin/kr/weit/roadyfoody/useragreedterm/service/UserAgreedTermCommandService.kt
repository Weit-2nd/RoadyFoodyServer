package kr.weit.roadyfoody.useragreedterm.service

import kr.weit.roadyfoody.term.repository.TermRepository
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.useragreedterm.domain.UserAgreedTerm
import kr.weit.roadyfoody.useragreedterm.repository.UserAgreedTermRepository
import org.springframework.stereotype.Service

@Service
class UserAgreedTermCommandService(
    private val userAgreedTermRepository: UserAgreedTermRepository,
    private val termRepository: TermRepository,
) {
    fun storeUserAgreedTerms(
        user: User,
        termIds: List<Long>,
    ) {
        val terms = termRepository.findAllByIdIn(termIds)
        val userAgreedTerms =
            terms.map { term ->
                UserAgreedTerm(user = user, term = term)
            }
        userAgreedTermRepository.saveAll(userAgreedTerms)
    }
}
