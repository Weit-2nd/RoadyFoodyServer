package kr.weit.roadyfoody.foodSpots.application.service

import com.ninjasquad.springmockk.SpykBean
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.should
import io.kotest.matchers.types.beInstanceOf
import io.mockk.every
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kr.weit.roadyfoody.foodSpots.domain.FoodCategory
import kr.weit.roadyfoody.foodSpots.exception.TooManyReportRequestException
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodCategories
import kr.weit.roadyfoody.foodSpots.fixture.createTestReportRequest
import kr.weit.roadyfoody.foodSpots.repository.FoodCategoryRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSportsOperationHoursRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsFoodCategoryRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsHistoryRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsRepository
import kr.weit.roadyfoody.foodSpots.repository.ReportFoodCategoryRepository
import kr.weit.roadyfoody.foodSpots.repository.ReportOperationHoursRepository
import kr.weit.roadyfoody.support.annotation.ServiceIntegrateTest
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.fixture.createTestUser
import kr.weit.roadyfoody.user.repository.UserRepository
import org.springframework.data.redis.core.RedisTemplate

@ServiceIntegrateTest
class FoodSpotsCommandServiceIntegrationTest(
    @SpykBean private val foodSpotsRepository: FoodSpotsRepository,
    private val foodSpotsCategoryRepository: FoodSpotsFoodCategoryRepository,
    private val foodSportsOperationHoursRepository: FoodSportsOperationHoursRepository,
    private val foodSpotsCommandService: FoodSpotsCommandService,
    private val userRepository: UserRepository,
    private val categoryRepository: FoodCategoryRepository,
    private val foodSpotsHistoryRepository: FoodSpotsHistoryRepository,
    private val reportFoodCategoryRepository: ReportFoodCategoryRepository,
    private val reportOperationHoursRepository: ReportOperationHoursRepository,
    @SpykBean private val redisTemplate: RedisTemplate<String, String>,
) : BehaviorSpec(
        {

            given("가게 오픈 상태 업데이트") {
                `when`("동시에 가게 오픈 상태 업데이트 요청이 들어올 경우") {
                    val numberOfCoroutines = 10
                    then("한번만 업데이트 되어야 한다.") {

                        runBlocking {
                            val jobs =
                                List(numberOfCoroutines) {
                                    launch {
                                        foodSpotsCommandService.setFoodSpotsOpen()
                                    }
                                }
                            jobs.joinAll()
                            verify(exactly = 1) { foodSpotsRepository.updateOpeningStatus() }
                        }
                    }
                }
            }

            given("리포트 생성 시") {
                lateinit var user: User
                lateinit var categories: List<FoodCategory>
                beforeEach {
                    user = userRepository.save(createTestUser())
                    categories = categoryRepository.saveAll(createTestFoodCategories())
                }
                afterEach {
                    reportFoodCategoryRepository.deleteAll()
                    reportOperationHoursRepository.deleteAll()
                    foodSpotsHistoryRepository.deleteAll()
                    foodSpotsCategoryRepository.deleteAll()
                    foodSportsOperationHoursRepository.deleteAll()
                    foodSpotsRepository.deleteAll()
                    userRepository.delete(user)
                    categoryRepository.deleteAll(categories)
                    val keys = redisTemplate.keys("*")
                    redisTemplate.delete(keys)
                }
                `when`("일일 요청 횟수 5를 초과하는 경우") {
                    then("TooManyReportRequestException 예외가 발생해야 한다.") {
                        runBlocking {
                            val exceptions = mutableListOf<Throwable>()
                            val jobs =
                                List(10) {
                                    launch {
                                        try {
                                            foodSpotsCommandService.createReport(
                                                user,
                                                createTestReportRequest(),
                                                null,
                                            )
                                        } catch (ex: Throwable) {
                                            exceptions.add(ex)
                                        }
                                    }
                                }
                            jobs.joinAll()

                            exceptions.shouldNotBeEmpty()
                            exceptions.forEach {
                                it should beInstanceOf<TooManyReportRequestException>()
                            }
                        }
                    }
                }

                `when`("redis 상 요청 횟수가 증가한 뒤 오류가 발생한다면") {
                    every { foodSpotsRepository.save(any()) } throws RuntimeException()
                    val ops = spyk(redisTemplate.opsForValue())
                    every { redisTemplate.opsForValue() } returns ops
                    then("그 값만큼 감소한다.") {
                        repeat(5) {
                            runCatching {
                                foodSpotsCommandService.createReport(
                                    user,
                                    createTestReportRequest(foodCategories = categories.map { it.id }.toSet()),
                                    null,
                                )
                            }
                        }
                        verify(exactly = 5) { ops.decrement(any()) }
                    }
                }
            }
        },
    )
