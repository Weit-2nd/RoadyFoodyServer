package kr.weit.roadyfoody.user.application.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kr.weit.roadyfoody.common.exception.ErrorCode
import kr.weit.roadyfoody.common.exception.RoadyFoodyBadRequestException
import kr.weit.roadyfoody.user.fixture.TEST_USER_ID
import kr.weit.roadyfoody.user.fixture.createTestUser
import kr.weit.roadyfoody.user.repository.UserRepository
import java.util.Optional

class UserCommandServiceTest :
    BehaviorSpec({
        val userRepository = mockk<UserRepository>()
        val userCommandService = UserCommandService(userRepository)
        val user = createTestUser()
        given("decreaseCoin 테스트") {
            val minusCoin = 100
            val expectedCoin = user.coin - minusCoin
            every { userRepository.findById(TEST_USER_ID) } returns Optional.of(user)
            `when`("코인을 감소시키면") {
                userCommandService.decreaseCoin(user.id, minusCoin)
                then("코인이 감소한다.") {
                    user.coin shouldBe expectedCoin
                }
            }

            `when`("코인이 부족하면") {
                val minusCoin = 1000
                every { userRepository.findById(TEST_USER_ID) } returns Optional.of(user)
                then("에러가 발생한다.") {
                    val ex =
                        shouldThrow<RoadyFoodyBadRequestException> {
                            userCommandService.decreaseCoin(user.id, minusCoin)
                        }
                    ex.message shouldBe ErrorCode.COIN_NOT_ENOUGH.errorMessage
                }
            }

            given("increaseCoin 테스트") {
                val plusCoin = 100
                val expectedCoin = user.coin + plusCoin
                every { userRepository.findById(TEST_USER_ID) } returns Optional.of(user)
                `when`("코인을 증가시키면") {
                    userCommandService.increaseCoin(user.id, plusCoin)
                    then("코인이 증가한다.") {
                        user.coin shouldBe expectedCoin
                    }
                }
            }
        }
    })
