package kr.weit.roadyfoody.useragreedterm.service

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.weit.roadyfoody.term.fixture.TEST_NONEXISTENT_TERM_ID
import kr.weit.roadyfoody.term.fixture.createTestTermIds
import kr.weit.roadyfoody.term.fixture.createTestTerms
import kr.weit.roadyfoody.term.repository.TermRepository
import kr.weit.roadyfoody.user.fixture.createTestUser1
import kr.weit.roadyfoody.useragreedterm.domain.UserAgreedTerm
import kr.weit.roadyfoody.useragreedterm.fixture.createTestUser1AgreedTerms
import kr.weit.roadyfoody.useragreedterm.repository.UserAgreedTermRepository

class UserAgreedTermCommandServiceTest :
    BehaviorSpec({
        val termRepository = mockk<TermRepository>()
        val userAgreedTermRepository = mockk<UserAgreedTermRepository>()
        val userAgreedTermCommandService = UserAgreedTermCommandService(userAgreedTermRepository, termRepository)

        given("storeUserAgreedTerms 테스트") {
            `when`("단일 User 와 하나 이상의 termId 가 주어질 시") {
                every { termRepository.findAllByIdIn(any<List<Long>>()) } returns createTestTerms()
                every { userAgreedTermRepository.saveAll(any<List<UserAgreedTerm>>()) } returns createTestUser1AgreedTerms()
                then("해당 UserAgreedTerm 들을 저장한다.") {
                    userAgreedTermCommandService.storeUserAgreedTerms(createTestUser1(), createTestTermIds())
                    verify { termRepository.findAllByIdIn(any<List<Long>>()) }
                    verify { userAgreedTermRepository.saveAll(any<List<UserAgreedTerm>>()) }
                }
            }

            `when`("단일 User 와 존재하지 않는 termId 가 주어질 시") {
                every { termRepository.findAllByIdIn(any<List<Long>>()) } returns emptyList()
                every { userAgreedTermRepository.saveAll(any<List<UserAgreedTerm>>()) } returns emptyList()
                then("해당 UserAgreedTerm 들을 저장하지 않는다.") {
                    userAgreedTermCommandService.storeUserAgreedTerms(createTestUser1(), listOf(TEST_NONEXISTENT_TERM_ID))
                    verify { termRepository.findAllByIdIn(any<List<Long>>()) }
                    verify { userAgreedTermRepository.saveAll(any<List<UserAgreedTerm>>()) }
                }
            }

            `when`("단일 User 와 빈 termId 가 주어질 시") {
                every { termRepository.findAllByIdIn(emptyList()) } returns emptyList()
                every { userAgreedTermRepository.saveAll(emptyList()) } returns emptyList()
                then("해당 UserAgreedTerm 들을 저장하지 않는다.") {
                    userAgreedTermCommandService.storeUserAgreedTerms(createTestUser1(), emptyList())
                    verify { termRepository.findAllByIdIn(emptyList()) }
                    verify { userAgreedTermRepository.saveAll(emptyList()) }
                }
            }
        }
    })
