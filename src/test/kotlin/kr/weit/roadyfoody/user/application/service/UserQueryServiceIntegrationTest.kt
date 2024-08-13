package kr.weit.roadyfoody.user.application.service

import io.awspring.cloud.s3.S3Template
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.ints.shouldBeZero
import io.kotest.matchers.shouldBe
import kr.weit.roadyfoody.foodSpots.application.service.FoodSpotsCommandService.Companion.FOOD_SPOTS_REPORT_LIMIT_COUNT
import kr.weit.roadyfoody.foodSpots.application.service.FoodSpotsCommandService.Companion.getFoodSpotsReportCountKey
import kr.weit.roadyfoody.global.config.S3Properties
import kr.weit.roadyfoody.support.annotation.ServiceIntegrateTest
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.fixture.createTestUser
import kr.weit.roadyfoody.user.repository.UserRepository
import org.springframework.data.redis.core.RedisTemplate

@ServiceIntegrateTest
class UserQueryServiceIntegrationTest(
    private val userRepository: UserRepository,
    private val s3Properties: S3Properties,
    private val s3Template: S3Template,
    private val redisTemplate: RedisTemplate<String, String>,
    private val userQueryService: UserQueryService,
) : BehaviorSpec({
        lateinit var user: User

        beforeSpec {
            s3Template.createBucket(s3Properties.bucket)
            user = userRepository.save(createTestUser())
        }

        afterSpec {
            userRepository.delete(user)
            s3Template.deleteBucket(s3Properties.bucket)
        }

        given("getUserInfo 테스트") {
            afterEach {
                redisTemplate.delete(getFoodSpotsReportCountKey(user.id))
            }
            val reportCountKey = getFoodSpotsReportCountKey(user.id)
            `when`("하루 동안 리포트를 생성하지 않은 경우") {
                then("당일 잔여 리포트 생성 횟수(${FOOD_SPOTS_REPORT_LIMIT_COUNT})를 전달합니다.") {
                    val userInfo = userQueryService.getUserInfo(user)
                    userInfo.restDailyReportCreationCount shouldBe FOOD_SPOTS_REPORT_LIMIT_COUNT
                }
            }

            `when`("하루 동안 리포트를 생성한 경우") {
                redisTemplate.opsForValue().increment(reportCountKey)
                then("해당하는 당일 잔여 리포트 생성 횟수를 전달합니다.") {
                    val userInfo = userQueryService.getUserInfo(user)
                    userInfo.restDailyReportCreationCount shouldBe FOOD_SPOTS_REPORT_LIMIT_COUNT - 1
                }
            }

            `when`("하루 동안 생성할 수 있는 리포트를 모두 생성한 경우") {
                repeat(FOOD_SPOTS_REPORT_LIMIT_COUNT) { redisTemplate.opsForValue().increment(reportCountKey) }
                then("당일 잔여 리포트 생성 횟수(0)를 전달합니다.") {
                    val userInfo = userQueryService.getUserInfo(user)
                    userInfo.restDailyReportCreationCount.shouldBeZero()
                }
            }
        }
    })
