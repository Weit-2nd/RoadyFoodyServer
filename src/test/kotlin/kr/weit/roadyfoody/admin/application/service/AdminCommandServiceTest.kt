package kr.weit.roadyfoody.admin.application.service

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kr.weit.roadyfoody.auth.fixture.TEST_ACCESS_TOKEN
import kr.weit.roadyfoody.auth.security.jwt.JwtUtil
import kr.weit.roadyfoody.user.fixture.TEST_USER_ID
import kr.weit.roadyfoody.user.fixture.createTestUser
import kr.weit.roadyfoody.user.repository.UserRepository
import java.util.Optional

class AdminCommandServiceTest :
    BehaviorSpec({
        val userRepository = mockk<UserRepository>()
        val jwtUtil = mockk<JwtUtil>()
        val adminCommandService = AdminCommandService(userRepository, jwtUtil)

        given("getUserAccessToken 테스트") {
            `when`("유저 ID 로 요청하면") {
                every { userRepository.findById(any()) } returns Optional.of(createTestUser())
                every { jwtUtil.generateAccessToken(any(), any()) } returns TEST_ACCESS_TOKEN
                then("UserAccessTokenResponse 를 반환한다.") {
                    val actual = adminCommandService.getUserAccessToken(TEST_USER_ID).accessToken
                    actual shouldBe TEST_ACCESS_TOKEN
                }
            }
        }
    })
