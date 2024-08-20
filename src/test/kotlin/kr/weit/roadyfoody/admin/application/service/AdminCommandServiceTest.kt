package kr.weit.roadyfoody.admin.application.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kr.weit.roadyfoody.auth.fixture.TEST_ACCESS_TOKEN
import kr.weit.roadyfoody.auth.security.jwt.JwtUtil
import kr.weit.roadyfoody.user.exception.UserNotFoundException
import kr.weit.roadyfoody.user.fixture.TEST_USER_ID
import kr.weit.roadyfoody.user.fixture.createTestUser
import kr.weit.roadyfoody.user.repository.UserRepository
import org.springframework.data.redis.core.RedisTemplate
import java.util.Optional

class AdminCommandServiceTest :
    BehaviorSpec({
        val userRepository = mockk<UserRepository>()
        val jwtUtil = mockk<JwtUtil>()
        val redisTemplate = mockk<RedisTemplate<String, String>>()
        val adminCommandService = AdminCommandService(userRepository, jwtUtil, redisTemplate)

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

        given("updateUserDailyReportCount 테스트") {
            `when`("유저 ID 와 일일 신고 횟수로 요청하면") {
                every { userRepository.existsById(any()) } returns true
                every { redisTemplate.opsForValue().set(any(), any()) } returns Unit
                then("성공한다.") {
                    adminCommandService.updateUserDailyReportCount(TEST_USER_ID, 1)
                }
            }

            `when`("유저 ID 에 해당하는 유저가 존재하지 않으면") {
                every { userRepository.existsById(any()) } returns false
                then("UserNotFoundException 이 발생한다.") {
                    shouldThrow<UserNotFoundException> {
                        adminCommandService.updateUserDailyReportCount(TEST_USER_ID, 1)
                    }
                }
            }
        }
    })
