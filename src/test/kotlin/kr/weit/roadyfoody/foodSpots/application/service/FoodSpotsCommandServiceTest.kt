package kr.weit.roadyfoody.foodSpots.application.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.spyk
import jakarta.persistence.EntityManager
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsFoodCategory
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsOperationHours
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsPhoto
import kr.weit.roadyfoody.foodSpots.domain.ReportFoodCategory
import kr.weit.roadyfoody.foodSpots.domain.ReportOperationHours
import kr.weit.roadyfoody.foodSpots.exception.CategoriesNotFoundException
import kr.weit.roadyfoody.foodSpots.fixture.createMockPhotoList
import kr.weit.roadyfoody.foodSpots.fixture.createMockTestFoodHistory
import kr.weit.roadyfoody.foodSpots.fixture.createMockTestFoodSpot
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodCategory
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodOperationHours
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodSpotsFoodCategory
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodSpotsPhoto
import kr.weit.roadyfoody.foodSpots.fixture.createTestReportFoodCategory
import kr.weit.roadyfoody.foodSpots.fixture.createTestReportOperationHours
import kr.weit.roadyfoody.foodSpots.fixture.createTestReportRequest
import kr.weit.roadyfoody.foodSpots.repository.FoodCategoryRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSportsOperationHoursRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsFoodCategoryRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsHistoryRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsPhotoRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsRepository
import kr.weit.roadyfoody.foodSpots.repository.ReportFoodCategoryRepository
import kr.weit.roadyfoody.foodSpots.repository.ReportOperationHoursRepository
import kr.weit.roadyfoody.global.service.ImageService
import kr.weit.roadyfoody.support.utils.ImageFormat
import kr.weit.roadyfoody.user.application.UserCommandService
import kr.weit.roadyfoody.user.fixture.TEST_USER_ID
import kr.weit.roadyfoody.user.fixture.createTestUser
import kr.weit.roadyfoody.user.repository.UserRepository
import org.redisson.api.RedissonClient
import java.util.Optional
import java.util.concurrent.ExecutorService

class FoodSpotsCommandServiceTest :
    BehaviorSpec(
        {
            val foodSpotsRepository = mockk<FoodSpotsRepository>()
            val foodSpotsHistoryRepository = mockk<FoodSpotsHistoryRepository>()
            val foodSpotsPhotoRepository = mockk<FoodSpotsPhotoRepository>()
            val userRepository = mockk<UserRepository>()
            val reportOperationHoursRepository = mockk<ReportOperationHoursRepository>()
            val foodSportsOperationHoursRepository = mockk<FoodSportsOperationHoursRepository>()
            val foodCategoryRepository = mockk<FoodCategoryRepository>()
            val reportFoodCategoryRepository = mockk<ReportFoodCategoryRepository>()
            val foodSpotsCategoryRepository = mockk<FoodSpotsFoodCategoryRepository>()
            val imageService = spyk(ImageService(mockk()))
            val executor = mockk<ExecutorService>()
            val userCommandService = mockk<UserCommandService>()
            val entityManager = mockk<EntityManager>()
            val redissonClient = mockk<RedissonClient>()
            val foodSpotsCommandService =
                FoodSpotsCommandService(
                    foodSpotsRepository,
                    foodSpotsHistoryRepository,
                    foodSpotsPhotoRepository,
                    reportOperationHoursRepository,
                    foodSportsOperationHoursRepository,
                    foodCategoryRepository,
                    reportFoodCategoryRepository,
                    foodSpotsCategoryRepository,
                    imageService,
                    executor,
                    userCommandService,
                    entityManager,
                    redissonClient,
                )
            val user = createTestUser()

            given("createReport 테스트") {
                every { foodSpotsRepository.save(any()) } returns createMockTestFoodSpot()
                every { foodSpotsHistoryRepository.save(any()) } returns createMockTestFoodHistory()
                every { userRepository.findById(TEST_USER_ID) } returns Optional.of(user)
                every { imageService.upload(any(), any()) } returns Unit
                every { foodCategoryRepository.findFoodCategoryByIdIn(any()) } returns
                    listOf(
                        createTestFoodCategory(),
                    )
                every { reportOperationHoursRepository.saveAll(any<List<ReportOperationHours>>()) } returns
                    listOf(createTestReportOperationHours())
                every { foodSportsOperationHoursRepository.saveAll(any<List<FoodSpotsOperationHours>>()) } returns
                    listOf(createTestFoodOperationHours())
                every { reportFoodCategoryRepository.saveAll(any<List<ReportFoodCategory>>()) } returns
                    listOf(createTestReportFoodCategory())
                every { foodSpotsCategoryRepository.saveAll(any<List<FoodSpotsFoodCategory>>()) } returns
                    createTestFoodSpotsFoodCategory()
                every { foodSpotsPhotoRepository.saveAll(any<List<FoodSpotsPhoto>>()) } returns
                    listOf(createTestFoodSpotsPhoto())
                every { executor.execute(any()) } answers {
                    firstArg<Runnable>().run()
                }
                every { userCommandService.increaseCoin(any(), any()) } just runs
                every { entityManager.flush() } just runs
                `when`("정상적인 데이터와 이미지가 들어올 경우") {
                    then("정상적으로 저장되어야 한다.") {
                        foodSpotsCommandService.createReport(
                            createTestUser(),
                            createTestReportRequest(),
                            createMockPhotoList(ImageFormat.WEBP),
                        )
                    }
                }

                `when`("정상적인 데이터만 들어올 경우") {
                    then("정상적으로 저장되어야 한다.") {
                        foodSpotsCommandService.createReport(
                            createTestUser(),
                            createTestReportRequest(),
                            null,
                        )
                    }
                }

                `when`("카테고리가 전부 존재하지 않을 경우") {
                    every { userRepository.findById(TEST_USER_ID) } returns Optional.of(user)
                    every { foodCategoryRepository.findFoodCategoryByIdIn(any()) } returns emptyList()
                    then("CategoriesNotFoundException이 발생한다.") {
                        shouldThrow<CategoriesNotFoundException> {
                            foodSpotsCommandService.createReport(
                                createTestUser(),
                                createTestReportRequest(),
                                createMockPhotoList(ImageFormat.WEBP),
                            )
                        }
                    }
                }
            }
            given("deleteWithdrawUserReport 테스트") {
                `when`("유저 삭제 요청이 들어올 경우") {
                    every { foodSpotsHistoryRepository.findByUser(any()) } returns
                        listOf(
                            createMockTestFoodHistory(),
                        )
                    every { reportOperationHoursRepository.deleteByFoodSpotsHistoryIn(any()) } returns Unit
                    every { reportFoodCategoryRepository.deleteByFoodSpotsHistoryIn(any()) } returns Unit
                    every { foodSpotsPhotoRepository.findByHistoryIn(any()) } returns
                        listOf(
                            createTestFoodSpotsPhoto(),
                        )
                    every { imageService.remove(any()) } returns Unit
                    every { foodSpotsPhotoRepository.deleteAll(any()) } returns Unit
                    every { foodSpotsHistoryRepository.deleteAll(any()) } returns Unit
                    then("정상적으로 삭제되어야 한다.") {
                        foodSpotsCommandService.deleteWithdrawUserReport(user)
                    }
                }

                `when`("유저가 작성한 리포트가 없을 경우") {
                    every { foodSpotsHistoryRepository.findByUser(any()) } returns emptyList()
                    then("아무런 동작이 일어나지 않아야 한다.") {
                        foodSpotsCommandService.deleteWithdrawUserReport(user)
                    }
                }
            }

            given("setFoodSpotsOpen 테스트") {
                every { foodSpotsRepository.updateOpeningStatus() } returns 1
                every { redissonClient.getBucket<String>(any<String>()) } returns
                    mockk {
                        every { setIfAbsent(any(), any()) } returns false
                    }
                then("정상적으로 업데이트 되어야 한다.") {
                    foodSpotsCommandService.setFoodSpotsOpen()
                }
            }
        },
    )
