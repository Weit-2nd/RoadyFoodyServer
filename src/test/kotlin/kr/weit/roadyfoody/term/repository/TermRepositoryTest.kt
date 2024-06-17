package kr.weit.roadyfoody.term.repository

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kr.weit.roadyfoody.support.annotation.RepositoryTest
import kr.weit.roadyfoody.term.domain.Term
import kr.weit.roadyfoody.term.exception.TermNotFoundException
import kr.weit.roadyfoody.term.fixture.TEST_NONEXISTENT_TERM_ID
import kr.weit.roadyfoody.term.fixture.createTestTerms

@RepositoryTest
class TermRepositoryTest(
    private val termRepository: TermRepository,
) : DescribeSpec({

        lateinit var givenTerms: List<Term>

        beforeEach {
            givenTerms =
                termRepository
                    .saveAll(
                        createTestTerms(),
                    )
        }

        describe("getByTermId 메소드는") {
            context("존재하는 id 한 개 를 받는 경우") {
                it("해당 Term 을 반환한다.") {
                    val term = termRepository.getByTermId(givenTerms.first().id)
                    term shouldBe givenTerms.first()
                }
            }

            context("존재하지 않는 id 를 받는 경우") {
                it("TermNotFoundException 을 던진다.") {
                    shouldThrow<TermNotFoundException> {
                        termRepository.getByTermId(TEST_NONEXISTENT_TERM_ID)
                    }
                }
            }
        }

        describe("findAllByIdIn 메소드는") {
            context("존재하는 id ${givenTerms.size} 개 의 리스트를 받는 경우") {
                it("존재하는 id ${givenTerms.size} 개의 리스트를 반환한다.") {
                    val terms = termRepository.findAllByIdIn(givenTerms.map { it.id }.toSet())
                    terms shouldBe givenTerms
                }
            }

            context("존재하는 id ${givenTerms.size} 개 와 존재하지 않는 id 가 섞인 리스트를 받는 경우") {
                it("존재하는 id ${givenTerms.size} 개의 리스트를 반환한다.") {
                    val terms = termRepository.findAllByIdIn(givenTerms.map { it.id }.toSet() + TEST_NONEXISTENT_TERM_ID)
                    terms shouldBe givenTerms
                }
            }

            context("존재하지 않는 id 리스트를 받는 경우") {
                it("빈 리스트를 반환한다.") {
                    val terms = termRepository.findAllByIdIn(setOf(TEST_NONEXISTENT_TERM_ID))
                    terms.shouldBeEmpty()
                }
            }

            context("빈 id 리스트를 받는 경우") {
                it("빈 리스트를 반환한다.") {
                    val terms = termRepository.findAllByIdIn(emptySet())
                    terms.shouldBeEmpty()
                }
            }
        }

        describe("findAllIdsByRequiredIsTrue 메소드는") {
            context("required 가 true 인 Term 이 존재하는 경우") {
                it("해당 Term 의 Id 가 있는 Set 을 반환한다.") {
                    val requiredIdSet = termRepository.findAllIdsByRequiredIsTrue()
                    requiredIdSet shouldBe givenTerms.filter { it.required }.map { it.id }
                }
            }

            context("required 가 true 인 Term 이 존재하지 않는 경우") {
                it("빈 Set 을 반환한다.") {
                    termRepository.deleteAll()
                    val requiredIdSet = termRepository.findAllIdsByRequiredIsTrue()
                    requiredIdSet.shouldBeEmpty()
                }
            }
        }
    })
