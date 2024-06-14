package kr.weit.roadyfoody.useragreedterm.fixture

import kr.weit.roadyfoody.term.fixture.createTestOptionalTerm1
import kr.weit.roadyfoody.term.fixture.createTestOptionalTerm2
import kr.weit.roadyfoody.term.fixture.createTestOptionalTerm3
import kr.weit.roadyfoody.term.fixture.createTestRequiredTerm1
import kr.weit.roadyfoody.term.fixture.createTestRequiredTerm2
import kr.weit.roadyfoody.term.fixture.createTestRequiredTerm3
import kr.weit.roadyfoody.user.fixture.createTestUser1
import kr.weit.roadyfoody.useragreedterm.domain.UserAgreedTerm

const val TEST_USER_1_AGREED_REQUIRED_TERM_1_ID = 1L

fun createTestUser1AgreedRequiredTerm1() =
    UserAgreedTerm(TEST_USER_1_AGREED_REQUIRED_TERM_1_ID, createTestUser1(), createTestRequiredTerm1())

const val TEST_USER_1_AGREED_REQUIRED_TERM_2_ID = TEST_USER_1_AGREED_REQUIRED_TERM_1_ID + 1L

fun createTestUser1AgreedRequiredTerm2() =
    UserAgreedTerm(TEST_USER_1_AGREED_REQUIRED_TERM_2_ID, createTestUser1(), createTestRequiredTerm2())

const val TEST_USER_1_AGREED_REQUIRED_TERM_3_ID = TEST_USER_1_AGREED_REQUIRED_TERM_2_ID + 1L

fun createTestUser1AgreedRequiredTerm3() =
    UserAgreedTerm(TEST_USER_1_AGREED_REQUIRED_TERM_3_ID, createTestUser1(), createTestRequiredTerm3())

fun createTestUser1AgreedRequiredTerms() =
    listOf(
        createTestUser1AgreedRequiredTerm1(),
        createTestUser1AgreedRequiredTerm2(),
        createTestUser1AgreedRequiredTerm3(),
    )

val TEST_USER_1_AGREED_REQUIRED_TERMS_SIZE = createTestUser1AgreedRequiredTerms().size

val TEST_USER_1_AGREED_OPTIONAL_TERM_1_ID = TEST_USER_1_AGREED_REQUIRED_TERMS_SIZE + 1L

fun createTestUser1AgreedOptionalTerm1() =
    UserAgreedTerm(TEST_USER_1_AGREED_OPTIONAL_TERM_1_ID, createTestUser1(), createTestOptionalTerm1())

val TEST_USER_1_AGREED_OPTIONAL_TERM_2_ID = TEST_USER_1_AGREED_OPTIONAL_TERM_1_ID + 1L

fun createTestUser1AgreedOptionalTerm2() =
    UserAgreedTerm(TEST_USER_1_AGREED_OPTIONAL_TERM_2_ID, createTestUser1(), createTestOptionalTerm2())

val TEST_USER_1_AGREED_OPTIONAL_TERM_3_ID = TEST_USER_1_AGREED_OPTIONAL_TERM_2_ID + 1L

fun createTestUser1AgreedOptionalTerm3() =
    UserAgreedTerm(TEST_USER_1_AGREED_OPTIONAL_TERM_3_ID, createTestUser1(), createTestOptionalTerm3())

fun createTestUser1AgreedOptionalTerms() =
    listOf(
        createTestUser1AgreedOptionalTerm1(),
        createTestUser1AgreedOptionalTerm2(),
        createTestUser1AgreedOptionalTerm3(),
    )

fun createTestUser1AgreedTerms() = createTestUser1AgreedRequiredTerms() + createTestUser1AgreedOptionalTerms()
