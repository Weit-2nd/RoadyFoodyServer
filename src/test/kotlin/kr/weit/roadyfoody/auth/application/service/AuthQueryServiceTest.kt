package kr.weit.roadyfoody.auth.application.service

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.weit.roadyfoody.auth.fixture.TEST_SOCIAL_ACCESS_TOKEN
import kr.weit.roadyfoody.auth.fixture.createTestKakaoUserResponse
import kr.weit.roadyfoody.auth.presentation.client.KakaoClientInterface

class AuthQueryServiceTest :
    BehaviorSpec({
        val kakaoClientInterface = mockk<KakaoClientInterface>()
        val authQueryService = AuthQueryService(kakaoClientInterface)

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
        }
    })
