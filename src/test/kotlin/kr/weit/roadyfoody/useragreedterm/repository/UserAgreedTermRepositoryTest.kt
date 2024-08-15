package kr.weit.roadyfoody.useragreedterm.repository

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import kr.weit.roadyfoody.support.annotation.RepositoryTest
import kr.weit.roadyfoody.term.domain.Term
import kr.weit.roadyfoody.term.fixture.createTestTerms
import kr.weit.roadyfoody.term.repository.TermRepository
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.fixture.createTestUser
import kr.weit.roadyfoody.user.repository.UserRepository
import kr.weit.roadyfoody.useragreedterm.domain.UserAgreedTerm

@RepositoryTest
class UserAgreedTermRepositoryTest(
    private val userRepository: UserRepository,
    private val termRepository: TermRepository,
    private val userAgreedTermRepository: UserAgreedTermRepository,
) : DescribeSpec({
        lateinit var user: User
        lateinit var terms: List<Term>

        beforeEach {
            user = userRepository.save(createTestUser())
            terms = termRepository.saveAll(createTestTerms())
            userAgreedTermRepository.saveAll(
                terms.map { term ->
                    UserAgreedTerm(
                        user = user,
                        term = term,
                    )
                },
            )
        }

        describe("deleteAllByUser 메소드는") {
            context("존재하는 user 를 받는 경우") {
                it("해당 user 와 관련된 UserAgreedTerm 을 모두 삭제한다.") {
                    userAgreedTermRepository.deleteAllByUser(user)
                    val actual = userAgreedTermRepository.findAll()
                    actual.shouldBeEmpty()
                }
            }
        }
    })
