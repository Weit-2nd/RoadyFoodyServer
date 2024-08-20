package kr.weit.roadyfoody.admin.application.service

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import kr.weit.roadyfoody.foodSpots.application.service.FoodSpotsCommandService.Companion.getFoodSpotsReportCountKey
import kr.weit.roadyfoody.support.annotation.ServiceIntegrateTest
import kr.weit.roadyfoody.user.fixture.TEST_USER_ID
import kr.weit.roadyfoody.user.repository.UserRepository
import org.springframework.data.redis.core.RedisTemplate

@ServiceIntegrateTest
class AdminCommandServiceIntegrationTest(
    @MockkBean private val userRepository: UserRepository,
    private val adminCommandService: AdminCommandService,
    private val redisTemplate: RedisTemplate<String, String>,
) : BehaviorSpec({
        given("이미 생성 횟수가 5인 회원이 있을 때") {
            val userKey = getFoodSpotsReportCountKey(TEST_USER_ID)
            every { userRepository.existsById(TEST_USER_ID) } returns true
            redisTemplate.opsForValue().set(userKey, "5")
            `when`("그 회원의 생성 횟수를 1로 변경 요청하면") {
                adminCommandService.updateUserDailyReportCount(TEST_USER_ID, 1)
                then("redis 에 값이 변경되어야 한다.") {
                    val restReportCount = redisTemplate.opsForValue().get(userKey)
                    restReportCount shouldBe "1"
                }
            }
            redisTemplate.delete(userKey)
        }
    })
