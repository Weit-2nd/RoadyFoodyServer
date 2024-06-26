package kr.weit.roadyfoody.useragreedterm.application.service

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.weit.roadyfoody.term.fixture.TEST_NONEXISTENT_TERM_ID
import kr.weit.roadyfoody.term.fixture.createTestTermIdSet
import kr.weit.roadyfoody.term.fixture.createTestTerms
import kr.weit.roadyfoody.term.repository.TermRepository
import kr.weit.roadyfoody.user.fixture.TEST_USER_ID
import kr.weit.roadyfoody.user.fixture.createTestUser
import kr.weit.roadyfoody.useragreedterm.domain.UserAgreedTerm
import kr.weit.roadyfoody.useragreedterm.fixture.createTestUserAgreedTerms
import kr.weit.roadyfoody.useragreedterm.repository.UserAgreedTermRepository

class UserAgreedTermCommandServiceTest :
    BehaviorSpec({
        val termRepository = mockk<TermRepository>()
        val userAgreedTermRepository = mockk<UserAgreedTermRepository>()
        val userAgreedTermCommandService = UserAgreedTermCommandService(userAgreedTermRepository, termRepository)

        afterEach { clearAllMocks() }

        given("storeUserAgreedTerms 테스트") {
            `when`("단일 User 와 하나 이상의 termId 가 주어질 시") {
                every { termRepository.findAllByIdIn(any<Set<Long>>()) } returns createTestTerms()
                every { userAgreedTermRepository.saveAll(any<List<UserAgreedTerm>>()) } returns
                    createTestUserAgreedTerms(TEST_USER_ID)
                then("해당 UserAgreedTerm 들을 저장한다.") {
                    userAgreedTermCommandService.storeUserAgreedTerms(createTestUser(TEST_USER_ID), createTestTermIdSet())
                    verify(exactly = 1) { termRepository.findAllByIdIn(any<Set<Long>>()) }
                    verify(exactly = 1) { userAgreedTermRepository.saveAll(any<List<UserAgreedTerm>>()) }
                }
            }

            `when`("단일 User 와 존재하지 않는 termId 가 주어질 시") {
                every { termRepository.findAllByIdIn(any<Set<Long>>()) } returns emptyList()
                every { userAgreedTermRepository.saveAll(any<List<UserAgreedTerm>>()) } returns emptyList()
                then("해당 UserAgreedTerm 들을 저장하지 않는다.") {
                    userAgreedTermCommandService.storeUserAgreedTerms(createTestUser(TEST_USER_ID), setOf(TEST_NONEXISTENT_TERM_ID))
                    verify(exactly = 1) { termRepository.findAllByIdIn(any<Set<Long>>()) }
                    verify(exactly = 1) { userAgreedTermRepository.saveAll(any<List<UserAgreedTerm>>()) }
                }
            }

            `when`("단일 User 와 빈 termId 가 주어질 시") {
                every { termRepository.findAllByIdIn(emptySet()) } returns emptyList()
                every { userAgreedTermRepository.saveAll(emptyList()) } returns emptyList()
                then("해당 UserAgreedTerm 들을 저장하지 않는다.") {
                    userAgreedTermCommandService.storeUserAgreedTerms(createTestUser(TEST_USER_ID), emptySet())
                    verify(exactly = 1) { termRepository.findAllByIdIn(emptySet()) }
                    verify(exactly = 1) { userAgreedTermRepository.saveAll(emptyList()) }
                }
            }
        }
    })
