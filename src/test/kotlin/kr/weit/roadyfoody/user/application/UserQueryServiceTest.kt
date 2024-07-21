package kr.weit.roadyfoody.user.application

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kr.weit.roadyfoody.user.fixture.TEST_USER_ID
import kr.weit.roadyfoody.user.fixture.createTestUser
import kr.weit.roadyfoody.user.repository.UserRepository
import java.util.Optional

class UserQueryServiceTest :
    BehaviorSpec({
        val userRepository = mockk<UserRepository>()
        val userQueryService = UserQueryService(userRepository)
        val user = createTestUser()
        given("decreaseCoin 테스트") {
            val minusCoin = 100
            val expectedCoin = user.coin - minusCoin
            every { userRepository.findById(TEST_USER_ID) } returns Optional.of(user)
            `when`("코인을 감소시키면") {
                userQueryService.decreaseCoin(user.id, minusCoin)
                then("코인이 감소한다.") {
                    user.coin shouldBe expectedCoin
                }
            }
        }

        given("increaseCoin 테스트") {
            val plusCoin = 100
            val expectedCoin = user.coin + plusCoin
            every { userRepository.findById(TEST_USER_ID) } returns Optional.of(user)
            `when`("코인을 증가시키면") {
                userQueryService.increaseCoin(user.id, plusCoin)
                then("코인이 증가한다.") {
                    user.coin shouldBe expectedCoin
                }
            }
        }
    })
