package kr.weit.roadyfoody.admin.fixture

import kr.weit.roadyfoody.admin.dto.SimpleUserInfoResponse
import kr.weit.roadyfoody.admin.dto.SimpleUserInfoResponses
import kr.weit.roadyfoody.user.fixture.TEST_USER_ID
import kr.weit.roadyfoody.user.fixture.createTestUser

fun createTestSimpleUserInfoResponse(userId: Long = TEST_USER_ID): SimpleUserInfoResponse =
    SimpleUserInfoResponse.from(createTestUser(userId))

fun createTestSimpleUserInfoResponses(size: Int = 5): SimpleUserInfoResponses =
    SimpleUserInfoResponses(
        List(size) { userId -> createTestSimpleUserInfoResponse(userId + 1L) },
    )
