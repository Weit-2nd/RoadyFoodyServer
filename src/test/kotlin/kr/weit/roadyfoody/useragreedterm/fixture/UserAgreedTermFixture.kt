package kr.weit.roadyfoody.useragreedterm.fixture

import kr.weit.roadyfoody.term.fixture.createTestOptionalTerm
import kr.weit.roadyfoody.term.fixture.createTestRequiredTerm
import kr.weit.roadyfoody.user.fixture.createTestUser1
import kr.weit.roadyfoody.useragreedterm.domain.UserAgreedTerm

fun createTestUser1AgreedRequiredTerm(
    id: Long,
    termId: Long,
) = UserAgreedTerm(
    id = id,
    user = createTestUser1(),
    term = createTestRequiredTerm(termId),
)

const val TEST_USER_1_AGREED_REQUIRED_TERMS_SIZE = 3

fun createTestUser1AgreedRequiredTerms() =
    (1..TEST_USER_1_AGREED_REQUIRED_TERMS_SIZE).map {
        createTestUser1AgreedRequiredTerm(
            it.toLong(),
            it.toLong(),
        )
    }

fun createTestUser1AgreedOptionalTerm(
    id: Long,
    termId: Long,
) = UserAgreedTerm(
    id = id,
    user = createTestUser1(),
    term = createTestOptionalTerm(termId),
)

fun createTestUser1AgreedOptionalTerms() =
    (TEST_USER_1_AGREED_REQUIRED_TERMS_SIZE + 1..TEST_USER_1_AGREED_REQUIRED_TERMS_SIZE + 3).map {
        createTestUser1AgreedOptionalTerm(
            it.toLong(),
            it.toLong(),
        )
    }

fun createTestUser1AgreedTerms() = createTestUser1AgreedRequiredTerms() + createTestUser1AgreedOptionalTerms()
