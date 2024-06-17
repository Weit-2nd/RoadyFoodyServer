package kr.weit.roadyfoody.term.service

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.weit.roadyfoody.term.fixture.TEST_REQUIRED_TERMS_SIZE
import kr.weit.roadyfoody.term.fixture.createTestNotEnoughRequiredTermIdSet
import kr.weit.roadyfoody.term.fixture.createTestRequiredTermIdSet
import kr.weit.roadyfoody.term.fixture.createTestTermIdSet
import kr.weit.roadyfoody.term.repository.TermRepository
import kr.weit.roadyfoody.useragreedterm.exception.RequiredTermNotAgreedException

class TermCommandServiceTest :
    BehaviorSpec({
        val termRepository = mockk<TermRepository>()
        val termCommandService = TermCommandService(termRepository)

        given("checkRequiredTermsOrThrow 테스트") {
            every { termRepository.findAllIdsByRequiredIsTrue() } returns createTestRequiredTermIdSet()
            `when`("모든 필수 약관 ($TEST_REQUIRED_TERMS_SIZE 개) 과 모든 선택 약관을 전달받으면") {
                then("RequiredTermNotAgreedException 을 던지지 않는다.") {
                    shouldNotThrow<RequiredTermNotAgreedException> {
                        termCommandService.checkRequiredTermsOrThrow(createTestTermIdSet())
                    }
                    verify { termRepository.findAllIdsByRequiredIsTrue() }
                }
            }

            `when`("모든 필수 약관 ($TEST_REQUIRED_TERMS_SIZE 개) 만 전달받으면") {
                then("RequiredTermNotAgreedException 을 던지지 않는다.") {
                    shouldNotThrow<RequiredTermNotAgreedException> {
                        termCommandService.checkRequiredTermsOrThrow(createTestRequiredTermIdSet())
                    }
                    verify { termRepository.findAllIdsByRequiredIsTrue() }
                }
            }

            `when`("일부 필수 약관을 전달 받지 못하면") {
                then("RequiredTermNotAgreedException 을 던진다.") {
                    shouldThrow<RequiredTermNotAgreedException> {
                        termCommandService.checkRequiredTermsOrThrow(createTestNotEnoughRequiredTermIdSet())
                    }
                    verify { termRepository.findAllIdsByRequiredIsTrue() }
                }
            }

            `when`("모든 필수 약관을 전달 받지 못하면") {
                then("RequiredTermNotAgreedException 을 던진다.") {
                    shouldThrow<RequiredTermNotAgreedException> {
                        termCommandService.checkRequiredTermsOrThrow(emptySet())
                    }
                    verify { termRepository.findAllIdsByRequiredIsTrue() }
                }
            }
        }
    })
