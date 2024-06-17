package kr.weit.roadyfoody.term.service

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kr.weit.roadyfoody.term.domain.Term
import kr.weit.roadyfoody.term.exception.TermNotFoundException
import kr.weit.roadyfoody.term.fixture.TEST_NONEXISTENT_TERM_ID
import kr.weit.roadyfoody.term.fixture.TEST_OPTIONAL_TERMS_SIZE
import kr.weit.roadyfoody.term.fixture.TEST_REQUIRED_TERMS_SIZE
import kr.weit.roadyfoody.term.fixture.createTestRequiredAndOptionalSizes
import kr.weit.roadyfoody.term.fixture.createTestSummaryTermsResponse
import kr.weit.roadyfoody.term.fixture.createTestTermIdSet
import kr.weit.roadyfoody.term.fixture.createTestTerms
import kr.weit.roadyfoody.term.fixture.createTestZerosRequiredAndOptionalSizes
import kr.weit.roadyfoody.term.fixture.createTestZerosSummaryTermsResponse
import kr.weit.roadyfoody.term.repository.TermRepository
import kr.weit.roadyfoody.term.repository.getByTermId
import kr.weit.roadyfoody.term.service.dto.DetailedTermResponse
import org.junit.jupiter.api.assertThrows

class TermQueryServiceTest :
    BehaviorSpec({
        val termRepository = mockk<TermRepository>()
        val termQueryService = spyk(TermQueryService(termRepository), recordPrivateCalls = true)

        given("getAllSummaryTerms 테스트") {
            `when`("필수 약관이 $TEST_REQUIRED_TERMS_SIZE 개 , 선택 약관이 $TEST_OPTIONAL_TERMS_SIZE 개 존재할 시") {
                every { termRepository.findAll() } returns createTestTerms()
                every { termQueryService["getRequiredAndOptionalSizes"](any<List<Term>>()) } returns createTestRequiredAndOptionalSizes()
                then("SummaryTermsResponse 를 반환한다.") {
                    termQueryService.getAllSummaryTerms() shouldBeEqual createTestSummaryTermsResponse()
                    verify { termRepository.findAll() }
                    verify { termQueryService["getRequiredAndOptionalSizes"](any<List<Term>>()) }
                }
            }

            `when`("약관이 존재하지 않을 시") {
                every { termRepository.findAll() } returns emptyList()
                every { termQueryService["getRequiredAndOptionalSizes"](any<List<Term>>()) } returns
                    createTestZerosRequiredAndOptionalSizes()
                then("내용이 0인 SummaryTermsResponse 를 반환한다.") {
                    termQueryService.getAllSummaryTerms() shouldBeEqual createTestZerosSummaryTermsResponse()
                    verify { termRepository.findAll() }
                    verify { termQueryService["getRequiredAndOptionalSizes"](any<List<Term>>()) }
                }
            }
        }

        given("getDetailedTerm 테스트") {
            `when`("termId 가 ${createTestTermIdSet().first()} 일 시") {
                every { termRepository.getByTermId(createTestTermIdSet().first()) } returns createTestTerms().first()
                then("DetailedTermResponse 를 반환한다.") {
                    val detailedTerm = termQueryService.getDetailedTerm(createTestTermIdSet().first())
                    detailedTerm shouldBeEqual DetailedTermResponse.from(createTestTerms().first())
                    verify { termRepository.getByTermId(createTestTermIdSet().first()) }
                }
            }

            `when`("termId 가 존재하지 않을 시") {
                every { termRepository.getByTermId(TEST_NONEXISTENT_TERM_ID) } throws
                    TermNotFoundException(TEST_NONEXISTENT_TERM_ID)
                then("TermNotFoundException 를 던진다.") {
                    assertThrows<TermNotFoundException> { termQueryService.getDetailedTerm(TEST_NONEXISTENT_TERM_ID) }
                    verify { termRepository.getByTermId(TEST_NONEXISTENT_TERM_ID) }
                }
            }
        }
    })
