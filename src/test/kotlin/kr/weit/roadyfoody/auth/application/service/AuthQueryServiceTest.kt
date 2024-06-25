package kr.weit.roadyfoody.auth.application.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.weit.roadyfoody.auth.exception.InvalidTokenException
import kr.weit.roadyfoody.auth.fixture.TEST_SOCIAL_ACCESS_TOKEN
import kr.weit.roadyfoody.auth.fixture.createTestKakaoUserResponse
import kr.weit.roadyfoody.auth.presentation.client.KakaoClientInterface
import kr.weit.roadyfoody.common.exception.RestClientException
import kr.weit.roadyfoody.user.repository.UserRepository

class AuthQueryServiceTest :
    BehaviorSpec({
        val kakaoClientInterface = mockk<KakaoClientInterface>()
        val userRepository = mockk<UserRepository>()
        val authQueryService = AuthQueryService(kakaoClientInterface, userRepository)

        afterEach { clearAllMocks() }

        given("requestKakaoUserInfo 테스트") {
            `when`("카카오 Access Token 을 가져오면") {
                every {
                    kakaoClientInterface.requestUserInfo(
                        TEST_SOCIAL_ACCESS_TOKEN,
                    )
                } returns createTestKakaoUserResponse()
                then("카카오 사용자 정보를 반환한다") {
                    val kakaoUserResponse = authQueryService.requestKakaoUserInfo(TEST_SOCIAL_ACCESS_TOKEN)
                    kakaoUserResponse shouldBe createTestKakaoUserResponse()
                    verify(exactly = 1) { kakaoClientInterface.requestUserInfo(TEST_SOCIAL_ACCESS_TOKEN) }
                }
            }

            `when`("카카오 API 호출 시 에러가 발생하면") {
                every {
                    kakaoClientInterface.requestUserInfo(
                        TEST_SOCIAL_ACCESS_TOKEN,
                    )
                } throws RestClientException()
                then("InvalidTokenException 을 반환한다") {
                    val exception =
                        shouldThrow<InvalidTokenException> {
                            authQueryService.requestKakaoUserInfo(TEST_SOCIAL_ACCESS_TOKEN)
                        }
                    exception shouldBe InvalidTokenException()
                    verify(exactly = 1) { kakaoClientInterface.requestUserInfo(TEST_SOCIAL_ACCESS_TOKEN) }
                }
            }
        }

        given("checkDuplicatedNickname 테스트") {
            `when`("닉네임이 중복되면") {
                every { userRepository.existsByProfileNickname(any()) } returns true
                then("true 를 반환한다") {
                    authQueryService.checkDuplicatedNickname("중복닉네임") shouldBe true
                    verify(exactly = 1) { userRepository.existsByProfileNickname("중복닉네임") }
                }
            }

            `when`("닉네임이 중복되지 않으면") {
                every { userRepository.existsByProfileNickname(any()) } returns false
                then("false 를 반환한다") {
                    authQueryService.checkDuplicatedNickname("중복되지않는닉네임") shouldBe false
                    verify(exactly = 1) { userRepository.existsByProfileNickname("중복되지않는닉네임") }
                }
            }
        }
    })
