package kr.weit.roadyfoody.useragreedterm.fixture

import kr.weit.roadyfoody.term.fixture.TEST_OPTIONAL_TERMS_SIZE
import kr.weit.roadyfoody.term.fixture.TEST_REQUIRED_TERMS_SIZE
import kr.weit.roadyfoody.term.fixture.createTestOptionalTerm
import kr.weit.roadyfoody.term.fixture.createTestRequiredTerm
import kr.weit.roadyfoody.user.fixture.createTestUser
import kr.weit.roadyfoody.useragreedterm.domain.UserAgreedTerm

fun createTestUserAgreedRequiredTerm(
    id: Long,
    userId: Long,
    termId: Long,
) = UserAgreedTerm(
    id,
    createTestUser(userId),
    createTestRequiredTerm(termId),
)

fun createTestUserAgreedRequiredTerms(userId: Long) =
    (1L..TEST_REQUIRED_TERMS_SIZE).map { id ->
        createTestUserAgreedRequiredTerm(
            id,
            userId,
            id,
        )
    }

fun createTestUserAgreedOptionalTerm(
    id: Long,
    userId: Long,
    termId: Long,
) = UserAgreedTerm(
    id,
    createTestUser(userId),
    createTestOptionalTerm(termId),
)

fun createTestUserAgreedOptionalTerms(userId: Long) =
    (TEST_REQUIRED_TERMS_SIZE + 1L..TEST_REQUIRED_TERMS_SIZE + TEST_OPTIONAL_TERMS_SIZE).map { id ->
        createTestUserAgreedOptionalTerm(
            id,
            userId,
            id,
        )
    }

fun createTestUserAgreedTerms(userId: Long) = createTestUserAgreedRequiredTerms(userId) + createTestUserAgreedOptionalTerms(userId)
