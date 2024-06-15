package kr.weit.roadyfoody.term.fixture

import kr.weit.roadyfoody.term.domain.Term
import kr.weit.roadyfoody.term.service.RequiredAndOptionalSizes

const val TEST_REQUIRED_TERM_1_ID = 1L
const val TEST_REQUIRED_TERM_1_TITLE = "테스트 필수 약관 $TEST_REQUIRED_TERM_1_ID"
val TEST_REQUIRED_TERM_1_CONTENT = coverContentHtmlTag(("테스트 필수 약관 $TEST_REQUIRED_TERM_1_ID 내용"))
const val TEST_REQUIRED_TERM_1_REQUIRED_FLAG = true

// Required Terms
fun createTestRequiredTerm1() =
    Term(
        TEST_REQUIRED_TERM_1_ID,
        TEST_REQUIRED_TERM_1_TITLE,
        TEST_REQUIRED_TERM_1_CONTENT,
        TEST_REQUIRED_TERM_1_REQUIRED_FLAG,
    )

const val TEST_REQUIRED_TERM_2_ID = TEST_REQUIRED_TERM_1_ID + 1L
const val TEST_REQUIRED_TERM_2_TITLE = "테스트 필수 약관 $TEST_REQUIRED_TERM_2_ID"
val TEST_REQUIRED_TERM_2_CONTENT = coverContentHtmlTag("테스트 필수 약관 $TEST_REQUIRED_TERM_2_ID 내용")
const val TEST_REQUIRED_TERM_2_REQUIRED_FLAG = true

fun createTestRequiredTerm2() =
    Term(
        TEST_REQUIRED_TERM_2_ID,
        TEST_REQUIRED_TERM_2_TITLE,
        TEST_REQUIRED_TERM_2_CONTENT,
        TEST_REQUIRED_TERM_2_REQUIRED_FLAG,
    )

const val TEST_REQUIRED_TERM_3_ID = TEST_REQUIRED_TERM_2_ID + 1L
const val TEST_REQUIRED_TERM_3_TITLE = "테스트 필수 약관 $TEST_REQUIRED_TERM_3_ID"
val TEST_REQUIRED_TERM_3_CONTENT = coverContentHtmlTag("테스트 필수 약관 $TEST_REQUIRED_TERM_3_ID 내용")
const val TEST_REQUIRED_TERM_3_REQUIRED_FLAG = true

fun createTestRequiredTerm3() =
    Term(
        TEST_REQUIRED_TERM_3_ID,
        TEST_REQUIRED_TERM_3_TITLE,
        TEST_REQUIRED_TERM_3_CONTENT,
        TEST_REQUIRED_TERM_3_REQUIRED_FLAG,
    )

fun createTestRequiredTerms() =
    listOf(
        createTestRequiredTerm1(),
        createTestRequiredTerm2(),
        createTestRequiredTerm3(),
    )

fun createTestRequiredTermIds() = createTestRequiredTerms().map { it.id }

val TEST_REQUIRED_TERMS_SIZE = createTestRequiredTerms().size

// Optional Terms
val TEST_OPTIONAL_TERM_1_ID = TEST_REQUIRED_TERMS_SIZE + 1L
val TEST_OPTIONAL_TERM_1_TITLE = "테스트 선택 약관 $TEST_OPTIONAL_TERM_1_ID"
val TEST_OPTIONAL_TERM_1_CONTENT = coverContentHtmlTag("테스트 선택 약관 $TEST_OPTIONAL_TERM_1_ID 내용")
const val TEST_OPTIONAL_TERM_1_REQUIRED_FLAG = false

fun createTestOptionalTerm1() =
    Term(
        TEST_OPTIONAL_TERM_1_ID,
        TEST_OPTIONAL_TERM_1_TITLE,
        TEST_OPTIONAL_TERM_1_CONTENT,
        TEST_OPTIONAL_TERM_1_REQUIRED_FLAG,
    )

val TEST_OPTIONAL_TERM_2_ID = TEST_OPTIONAL_TERM_1_ID + 1L
val TEST_OPTIONAL_TERM_2_TITLE = "테스트 선택 약관 $TEST_OPTIONAL_TERM_2_ID"
val TEST_OPTIONAL_TERM_2_CONTENT = coverContentHtmlTag("테스트 선택 약관 $TEST_OPTIONAL_TERM_2_ID 내용")
const val TEST_OPTIONAL_TERM_2_REQUIRED_FLAG = false

fun createTestOptionalTerm2() =
    Term(
        TEST_OPTIONAL_TERM_2_ID,
        TEST_OPTIONAL_TERM_2_TITLE,
        TEST_OPTIONAL_TERM_2_CONTENT,
        TEST_OPTIONAL_TERM_2_REQUIRED_FLAG,
    )

val TEST_OPTIONAL_TERM_3_ID = TEST_OPTIONAL_TERM_2_ID + 1L
val TEST_OPTIONAL_TERM_3_TITLE = "테스트 선택 약관 $TEST_OPTIONAL_TERM_3_ID"
val TEST_OPTIONAL_TERM_3_CONTENT = coverContentHtmlTag("테스트 선택 약관 $TEST_OPTIONAL_TERM_3_ID 내용")
const val TEST_OPTIONAL_TERM_3_REQUIRED_FLAG = false

fun createTestOptionalTerm3() =
    Term(
        TEST_OPTIONAL_TERM_3_ID,
        TEST_OPTIONAL_TERM_3_TITLE,
        TEST_OPTIONAL_TERM_3_CONTENT,
        TEST_OPTIONAL_TERM_3_REQUIRED_FLAG,
    )

fun createTestOptionalTerms() =
    listOf(
        createTestOptionalTerm1(),
        createTestOptionalTerm2(),
        createTestOptionalTerm3(),
    )

val TEST_OPTIONAL_TERMS_SIZE = createTestOptionalTerms().size

// All Terms
fun createTestTerms() = createTestRequiredTerms() + createTestOptionalTerms()

val TEST_TERMS_SIZE = createTestTerms().size

fun createTestRequiredAndOptionalSizes() = RequiredAndOptionalSizes(TEST_REQUIRED_TERMS_SIZE, TEST_OPTIONAL_TERMS_SIZE)

fun createTestZerosRequiredAndOptionalSizes() = RequiredAndOptionalSizes(0, 0)

fun createTestTermIds() = createTestTerms().map { it.id }

private fun coverContentHtmlTag(content: String) = """<!DOCTYPE html><html lang="ko">$content</html>"""

// Nonexistent Term
const val TEST_NONEXISTENT_TERM_ID = 0L

fun createTestNotEnoughRequiredTermIds() = createTestRequiredTerms().run { this.subList(0, this.size - 1) }.map { it.id }
