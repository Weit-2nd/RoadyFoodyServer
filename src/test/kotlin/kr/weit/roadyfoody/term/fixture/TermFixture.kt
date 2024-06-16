package kr.weit.roadyfoody.term.fixture

import kr.weit.roadyfoody.term.domain.Term
import kr.weit.roadyfoody.term.service.RequiredAndOptionalSizes

const val TEST_TERM_PREFIX = "테스트 약관"

// Required Terms
fun createTestRequiredTerm(id: Long) =
    Term(
        id,
        "$TEST_TERM_PREFIX $id",
        coverContentHtmlTag("$TEST_TERM_PREFIX $id 내용"),
        true,
    )

const val TEST_REQUIRED_TERMS_SIZE = 3

fun createTestRequiredTerms() = (1..TEST_REQUIRED_TERMS_SIZE).map { createTestRequiredTerm(it.toLong()) }

fun createTestRequiredTermIds() = createTestRequiredTerms().map { it.id }

// Optional Terms
fun createTestOptionalTerm(id: Long) =
    Term(
        id,
        "$TEST_TERM_PREFIX $id",
        coverContentHtmlTag("$TEST_TERM_PREFIX $id 내용"),
        false,
    )

const val TEST_OPTIONAL_TERMS_SIZE = 3

fun createTestOptionalTerms() =
    (TEST_REQUIRED_TERMS_SIZE + 1..TEST_REQUIRED_TERMS_SIZE + TEST_OPTIONAL_TERMS_SIZE).map {
        createTestOptionalTerm(it.toLong())
    }

// All Terms
const val TEST_TERMS_SIZE = TEST_REQUIRED_TERMS_SIZE + TEST_OPTIONAL_TERMS_SIZE

fun createTestTerms() = createTestRequiredTerms() + createTestOptionalTerms()

fun createTestTermIds() = createTestTerms().map { it.id }

fun createTestRequiredAndOptionalSizes() =
    RequiredAndOptionalSizes(
        TEST_REQUIRED_TERMS_SIZE,
        TEST_OPTIONAL_TERMS_SIZE,
    )

fun createTestZerosRequiredAndOptionalSizes() = RequiredAndOptionalSizes(0, 0)

private fun coverContentHtmlTag(content: String) = """<!DOCTYPE html><html lang="ko">$content</html>"""

// Fail Case
const val TEST_NONEXISTENT_TERM_ID = 0L

fun createTestNotEnoughRequiredTermIds() = createTestRequiredTerms().run { this.subList(0, this.size - 1) }.map { it.id }
