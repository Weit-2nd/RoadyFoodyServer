package kr.weit.roadyfoody.foodSpots.application.service

import TEST_FOOD_SPOT_ID
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.spyk
import io.mockk.verify
import jakarta.persistence.EntityManager
import kr.weit.roadyfoody.common.exception.RoadyFoodyBadRequestException
import kr.weit.roadyfoody.foodSpots.application.dto.OperationHoursRequest
import kr.weit.roadyfoody.foodSpots.domain.DayOfWeek
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsFoodCategory
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsOperationHours
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsPhoto
import kr.weit.roadyfoody.foodSpots.domain.ReportFoodCategory
import kr.weit.roadyfoody.foodSpots.domain.ReportOperationHours
import kr.weit.roadyfoody.foodSpots.exception.AlreadyClosedFoodSpotsException
import kr.weit.roadyfoody.foodSpots.exception.CategoriesNotFoundException
import kr.weit.roadyfoody.foodSpots.exception.NotFoodSpotsHistoriesOwnerException
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOTS_HISTORY_ID
import kr.weit.roadyfoody.foodSpots.fixture.TEST_NEW_CATEGORY_NAME
import kr.weit.roadyfoody.foodSpots.fixture.TEST_UPDATE_FOOD_SPOT_LATITUDE
import kr.weit.roadyfoody.foodSpots.fixture.TEST_UPDATE_FOOD_SPOT_LONGITUDE
import kr.weit.roadyfoody.foodSpots.fixture.TEST_UPDATE_FOOD_SPOT_NAME
import kr.weit.roadyfoody.foodSpots.fixture.TEST_UPDATE_OPERATION_HOURS_CLOSE
import kr.weit.roadyfoody.foodSpots.fixture.TEST_UPDATE_OPERATION_HOURS_OPEN
import kr.weit.roadyfoody.foodSpots.fixture.createMockPhotoList
import kr.weit.roadyfoody.foodSpots.fixture.createMockTestFoodHistory
import kr.weit.roadyfoody.foodSpots.fixture.createMockTestFoodSpot
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodCategories
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodCategory
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodOperationHours
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodSpotsFoodCategories
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodSpotsPhoto
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodSpotsUpdateRequest
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodSpotsUpdateRequestFromEntity
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
import kr.weit.roadyfoody.foodSpots.repository.getByHistoryId
import kr.weit.roadyfoody.global.service.ImageService
import kr.weit.roadyfoody.support.utils.ImageFormat
import kr.weit.roadyfoody.user.application.service.UserCommandService
import kr.weit.roadyfoody.user.fixture.TEST_OTHER_USER_ID
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
                    createTestFoodSpotsFoodCategories()
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
                    then("CategoriesNotFoundException 이 발생한다.") {
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

            given("doUpdateReport 테스트") {
                `when`("정상적인 데이터가 들어올 경우") {
                    every { foodSpotsRepository.findById(any()) } returns Optional.of(createMockTestFoodSpot())
                    every { foodSpotsCategoryRepository.deleteAll(any<List<FoodSpotsFoodCategory>>()) } just runs
                    every { foodCategoryRepository.findFoodCategoryByIdIn(any()) } returns createTestFoodCategories()
                    every { foodSpotsCategoryRepository.saveAll(any<List<FoodSpotsFoodCategory>>()) } returns
                        createTestFoodSpotsFoodCategories()
                    every { foodSportsOperationHoursRepository.deleteAll(any<List<FoodSpotsOperationHours>>()) } just runs
                    every { foodSportsOperationHoursRepository.saveAll(any<List<FoodSpotsOperationHours>>()) } returns
                        listOf(createTestFoodOperationHours())
                    every { foodSpotsHistoryRepository.save(any()) } returns createMockTestFoodHistory()
                    every { reportFoodCategoryRepository.saveAll(any<List<ReportFoodCategory>>()) } returns
                        listOf(createTestReportFoodCategory())
                    every { reportOperationHoursRepository.saveAll(any<List<ReportOperationHours>>()) } returns
                        listOf(createTestReportOperationHours())
                    every { entityManager.flush() } just runs
                    every { imageService.upload(any(), any()) } just runs
                    every { userCommandService.increaseCoin(any(), any()) } just runs

                    val foodSpotsUpdateRequest = createTestFoodSpotsUpdateRequest()

                    then("정상적으로 업데이트 되어야 한다.") {
                        foodSpotsCommandService.doUpdateReport(
                            createTestUser(),
                            TEST_FOOD_SPOT_ID,
                            foodSpotsUpdateRequest,
                        )
                    }
                }

                `when`("음식점 이름만 변경할 경우") {
                    val foodSpots = createMockTestFoodSpot()
                    every { foodSpotsRepository.findById(any()) } returns Optional.of(foodSpots)
                    every { foodSpotsCategoryRepository.deleteAll(any<List<FoodSpotsFoodCategory>>()) } just runs
                    every { foodCategoryRepository.findFoodCategoryByIdIn(any()) } returns createTestFoodCategories()
                    every { foodSpotsCategoryRepository.saveAll(any<List<FoodSpotsFoodCategory>>()) } returns
                        createTestFoodSpotsFoodCategories()
                    every { foodSportsOperationHoursRepository.deleteAll(any<List<FoodSpotsOperationHours>>()) } just runs
                    every { foodSportsOperationHoursRepository.saveAll(any<List<FoodSpotsOperationHours>>()) } returns
                        listOf(createTestFoodOperationHours())
                    every { foodSpotsHistoryRepository.save(any()) } returns createMockTestFoodHistory()
                    every { reportFoodCategoryRepository.saveAll(any<List<ReportFoodCategory>>()) } returns
                        listOf(createTestReportFoodCategory())
                    every { reportOperationHoursRepository.saveAll(any<List<ReportOperationHours>>()) } returns
                        listOf(createTestReportOperationHours())
                    val onlyNameChangeRequest =
                        createTestFoodSpotsUpdateRequestFromEntity(
                            foodSpots,
                        ).copy(name = TEST_UPDATE_FOOD_SPOT_NAME)

                    then("정상적으로 업데이트 되어야 한다.") {
                        foodSpotsCommandService.doUpdateReport(
                            createTestUser(),
                            TEST_FOOD_SPOT_ID,
                            onlyNameChangeRequest,
                        )
                    }
                }

                `when`("경도와 위도만 변경할 경우") {
                    val foodSpots = createMockTestFoodSpot()
                    every { foodSpotsRepository.findById(any()) } returns Optional.of(foodSpots)
                    every { foodSpotsCategoryRepository.deleteAll(any<List<FoodSpotsFoodCategory>>()) } just runs
                    every { foodCategoryRepository.findFoodCategoryByIdIn(any()) } returns createTestFoodCategories()
                    every { foodSpotsCategoryRepository.saveAll(any<List<FoodSpotsFoodCategory>>()) } returns
                        createTestFoodSpotsFoodCategories()
                    every { foodSportsOperationHoursRepository.deleteAll(any<List<FoodSpotsOperationHours>>()) } just runs
                    every { foodSportsOperationHoursRepository.saveAll(any<List<FoodSpotsOperationHours>>()) } returns
                        listOf(createTestFoodOperationHours())
                    every { foodSpotsHistoryRepository.save(any()) } returns createMockTestFoodHistory()
                    every { reportFoodCategoryRepository.saveAll(any<List<ReportFoodCategory>>()) } returns
                        listOf(createTestReportFoodCategory())
                    every { reportOperationHoursRepository.saveAll(any<List<ReportOperationHours>>()) } returns
                        listOf(createTestReportOperationHours())
                    val onlyCoordinateChangeRequest = createTestFoodSpotsUpdateRequestFromEntity(foodSpots)

                    then("정상적으로 업데이트 되어야 한다.") {
                        forAll(
                            row(onlyCoordinateChangeRequest.copy(longitude = TEST_UPDATE_FOOD_SPOT_LONGITUDE)),
                            row(onlyCoordinateChangeRequest.copy(latitude = TEST_UPDATE_FOOD_SPOT_LATITUDE)),
                            row(
                                onlyCoordinateChangeRequest.copy(
                                    longitude = TEST_UPDATE_FOOD_SPOT_LONGITUDE,
                                    latitude = TEST_UPDATE_FOOD_SPOT_LATITUDE,
                                ),
                            ),
                        ) { request ->
                            foodSpotsCommandService.doUpdateReport(
                                createTestUser(),
                                TEST_FOOD_SPOT_ID,
                                request,
                            )
                        }
                    }
                }

                `when`("카테고리만 추가될 경우") {
                    val foodSpots = createMockTestFoodSpot()
                    every { foodSpotsRepository.findById(any()) } returns Optional.of(foodSpots)
                    every { foodSpotsCategoryRepository.deleteAll(any<List<FoodSpotsFoodCategory>>()) } just runs
                    val newCategory = createTestFoodCategory(id = 10, name = TEST_NEW_CATEGORY_NAME)
                    // storeReport 시 request category 전체를 조회
                    every { foodCategoryRepository.findFoodCategoryByIdIn(any()) } returns createTestFoodCategories() + newCategory
                    // updateFoodSpotsCategory 시 새로 추가되어야하는 category 만 조회
                    every { foodCategoryRepository.findFoodCategoryByIdIn(setOf(newCategory.id)) } returns listOf(newCategory)
                    every { foodSpotsCategoryRepository.saveAll(any<List<FoodSpotsFoodCategory>>()) } returns
                        createTestFoodSpotsFoodCategories()
                    every { foodSportsOperationHoursRepository.deleteAll(any<List<FoodSpotsOperationHours>>()) } just runs
                    every { foodSportsOperationHoursRepository.saveAll(any<List<FoodSpotsOperationHours>>()) } returns
                        listOf(createTestFoodOperationHours())
                    every { foodSpotsHistoryRepository.save(any()) } returns createMockTestFoodHistory()
                    every { reportFoodCategoryRepository.saveAll(any<List<ReportFoodCategory>>()) } returns
                        listOf(createTestReportFoodCategory())
                    every { reportOperationHoursRepository.saveAll(any<List<ReportOperationHours>>()) } returns
                        listOf(createTestReportOperationHours())
                    val onlyCategoryAddRequest =
                        createTestFoodSpotsUpdateRequestFromEntity(foodSpots)
                            .copy(
                                foodCategories =
                                    foodSpots.foodCategoryList.map { it.foodCategory.id }.toSet() +
                                        newCategory.id,
                            )

                    then("정상적으로 업데이트 되어야 한다.") {
                        foodSpotsCommandService.doUpdateReport(
                            createTestUser(),
                            TEST_FOOD_SPOT_ID,
                            onlyCategoryAddRequest,
                        )
                    }
                }

                `when`("카테고리만 삭제될 경우") {
                    val foodSpots = createMockTestFoodSpot()
                    every { foodSpotsRepository.findById(any()) } returns Optional.of(foodSpots)
                    every { foodSpotsCategoryRepository.deleteAll(any<List<FoodSpotsFoodCategory>>()) } just runs
                    val lastCategoryId = createTestFoodCategories().last().id
                    every { foodCategoryRepository.findFoodCategoryByIdIn(any()) } returns createTestFoodCategories().dropLast(1)
                    every { foodSpotsCategoryRepository.saveAll(any<List<FoodSpotsFoodCategory>>()) } returns
                        createTestFoodSpotsFoodCategories()
                    every { foodSportsOperationHoursRepository.deleteAll(any<List<FoodSpotsOperationHours>>()) } just runs
                    every { foodSportsOperationHoursRepository.saveAll(any<List<FoodSpotsOperationHours>>()) } returns
                        listOf(createTestFoodOperationHours())
                    every { foodSpotsHistoryRepository.save(any()) } returns createMockTestFoodHistory()
                    every { reportFoodCategoryRepository.saveAll(any<List<ReportFoodCategory>>()) } returns
                        listOf(createTestReportFoodCategory())
                    every { reportOperationHoursRepository.saveAll(any<List<ReportOperationHours>>()) } returns
                        listOf(createTestReportOperationHours())
                    val onlyCategoryDeleteRequest =
                        createTestFoodSpotsUpdateRequestFromEntity(foodSpots)
                            .copy(
                                foodCategories =
                                    foodSpots.foodCategoryList.map { it.foodCategory.id }.toSet() -
                                        lastCategoryId,
                            )

                    then("정상적으로 업데이트 되어야 한다.") {
                        foodSpotsCommandService.doUpdateReport(
                            createTestUser(),
                            TEST_FOOD_SPOT_ID,
                            onlyCategoryDeleteRequest,
                        )
                    }
                }

                `when`("카테고리 추가와 삭제가 동시에 일어날 경우") {
                    val foodSpots = createMockTestFoodSpot()
                    every { foodSpotsRepository.findById(any()) } returns Optional.of(foodSpots)
                    every { foodSpotsCategoryRepository.deleteAll(any<List<FoodSpotsFoodCategory>>()) } just runs
                    val newCategory = createTestFoodCategory(id = 10, name = TEST_NEW_CATEGORY_NAME)
                    val lastCategoryIdToRemove = createTestFoodCategories().last().id
                    every { foodCategoryRepository.findFoodCategoryByIdIn(any()) } returns
                        createTestFoodCategories().dropLast(1) + newCategory
                    every { foodCategoryRepository.findFoodCategoryByIdIn(setOf(newCategory.id)) } returns listOf(newCategory)
                    every { foodSpotsCategoryRepository.saveAll(any<List<FoodSpotsFoodCategory>>()) } returns
                        createTestFoodSpotsFoodCategories()
                    every { foodSportsOperationHoursRepository.deleteAll(any<List<FoodSpotsOperationHours>>()) } just runs
                    every { foodSportsOperationHoursRepository.saveAll(any<List<FoodSpotsOperationHours>>()) } returns
                        listOf(createTestFoodOperationHours())
                    every { foodSpotsHistoryRepository.save(any()) } returns createMockTestFoodHistory()
                    every { reportFoodCategoryRepository.saveAll(any<List<ReportFoodCategory>>()) } returns
                        listOf(createTestReportFoodCategory())
                    every { reportOperationHoursRepository.saveAll(any<List<ReportOperationHours>>()) } returns
                        listOf(createTestReportOperationHours())
                    val categoryAddAndDeleteRequest =
                        createTestFoodSpotsUpdateRequestFromEntity(foodSpots)
                            .copy(
                                foodCategories =
                                    foodSpots.foodCategoryList.map { it.foodCategory.id }.toSet() +
                                        newCategory.id -
                                        lastCategoryIdToRemove,
                            )

                    then("정상적으로 업데이트 되어야 한다.") {
                        foodSpotsCommandService.doUpdateReport(
                            createTestUser(),
                            TEST_FOOD_SPOT_ID,
                            categoryAddAndDeleteRequest,
                        )
                    }
                }

                `when`("운영시간이 변경될 경우") {
                    val foodSpots = createMockTestFoodSpot()
                    every { foodSpotsRepository.findById(any()) } returns Optional.of(foodSpots)
                    every { foodSpotsCategoryRepository.deleteAll(any<List<FoodSpotsFoodCategory>>()) } just runs
                    every { foodCategoryRepository.findFoodCategoryByIdIn(any()) } returns createTestFoodCategories()
                    every { foodSpotsCategoryRepository.saveAll(any<List<FoodSpotsFoodCategory>>()) } returns
                        createTestFoodSpotsFoodCategories()
                    every { foodSportsOperationHoursRepository.deleteAll(any<List<FoodSpotsOperationHours>>()) } just runs
                    every { foodSportsOperationHoursRepository.saveAll(any<List<FoodSpotsOperationHours>>()) } returns
                        listOf(createTestFoodOperationHours())
                    every { foodSpotsHistoryRepository.save(any()) } returns createMockTestFoodHistory()
                    every { reportFoodCategoryRepository.saveAll(any<List<ReportFoodCategory>>()) } returns
                        listOf(createTestReportFoodCategory())
                    every { reportOperationHoursRepository.saveAll(any<List<ReportOperationHours>>()) } returns
                        listOf(createTestReportOperationHours())
                    val onlyOperationHoursChangeRequest =
                        createTestFoodSpotsUpdateRequestFromEntity(foodSpots)
                            .copy(
                                operationHours =
                                    listOf(
                                        OperationHoursRequest(
                                            dayOfWeek = DayOfWeek.MON,
                                            openingHours = TEST_UPDATE_OPERATION_HOURS_OPEN,
                                            closingHours = TEST_UPDATE_OPERATION_HOURS_CLOSE,
                                        ),
                                    ),
                            )

                    then("정상적으로 업데이트 되어야 한다.") {
                        foodSpotsCommandService.doUpdateReport(
                            createTestUser(),
                            TEST_FOOD_SPOT_ID,
                            onlyOperationHoursChangeRequest,
                        )
                    }
                }

                `when`("변경된 값이 없을 경우") {
                    val foodSpots = createMockTestFoodSpot()
                    every { foodSpotsRepository.findById(any()) } returns Optional.of(foodSpots)
                    every { foodSpotsCategoryRepository.deleteAll(any<List<FoodSpotsFoodCategory>>()) } just runs
                    every { foodCategoryRepository.findFoodCategoryByIdIn(any()) } returns createTestFoodCategories()
                    every { foodSpotsCategoryRepository.saveAll(any<List<FoodSpotsFoodCategory>>()) } returns
                        createTestFoodSpotsFoodCategories()
                    every { foodSportsOperationHoursRepository.deleteAll(any<List<FoodSpotsOperationHours>>()) } just runs
                    every { foodSportsOperationHoursRepository.saveAll(any<List<FoodSpotsOperationHours>>()) } returns
                        listOf(createTestFoodOperationHours())
                    every { foodSpotsHistoryRepository.save(any()) } returns createMockTestFoodHistory()
                    every { reportFoodCategoryRepository.saveAll(any<List<ReportFoodCategory>>()) } returns
                        listOf(createTestReportFoodCategory())
                    every { reportOperationHoursRepository.saveAll(any<List<ReportOperationHours>>()) } returns
                        listOf(createTestReportOperationHours())

                    val noChangeRequest = createTestFoodSpotsUpdateRequestFromEntity(foodSpots)

                    then("변경된 값이 없으므로 RoadyFoodyBadRequestException 이 발생해야 한다.") {
                        shouldThrow<RoadyFoodyBadRequestException> {
                            foodSpotsCommandService.doUpdateReport(
                                createTestUser(),
                                TEST_FOOD_SPOT_ID,
                                noChangeRequest,
                            )
                        }
                    }
                }

                `when`("이미 폐업한 음식점에 폐업 리포트를 작성한 경우") {
                    val alreadyClosedFoodSpots = createMockTestFoodSpot(open = false, storeClosure = true)
                    every { foodSpotsRepository.findById(any()) } returns Optional.of(alreadyClosedFoodSpots)

                    val closeUpdateRequest = createTestFoodSpotsUpdateRequest().copy(open = false, closed = true)

                    then("AlreadyClosedFoodSpotsException 이 발생해야 한다.") {
                        shouldThrow<AlreadyClosedFoodSpotsException> {
                            foodSpotsCommandService.doUpdateReport(
                                createTestUser(),
                                TEST_FOOD_SPOT_ID,
                                closeUpdateRequest,
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
                        every { setIfAbsent(any(), any()) } returns true
                    }
                then("정상적으로 업데이트 되어야 한다.") {
                    foodSpotsCommandService.setFoodSpotsOpen()
                    verify(exactly = 1) { foodSpotsRepository.updateOpeningStatus() }
                }
            }

            given("deleteFoodSpotsHistories 테스트") {
                `when`("리포트의 주인이 아닌경우") {
                    every { foodSpotsHistoryRepository.getByHistoryId(any()) } returns
                        createMockTestFoodHistory(
                            createTestUser(TEST_OTHER_USER_ID),
                        )
                    then("예외가 발생한다.") {
                        shouldThrow<NotFoodSpotsHistoriesOwnerException> {
                            foodSpotsCommandService.deleteFoodSpotsHistories(user, TEST_FOOD_SPOTS_HISTORY_ID)
                        }
                    }
                }
                `when`("리포트 삭제 요청이 들어올 경우") {
                    every { foodSpotsHistoryRepository.getByHistoryId(any()) } returns createMockTestFoodHistory(user)
                    every { foodSpotsHistoryRepository.deleteById(any()) } returns Unit
                    every { reportFoodCategoryRepository.findByFoodSpotsHistoryId(any()) } returns
                        listOf(
                            createTestReportFoodCategory(),
                        )
                    every { reportFoodCategoryRepository.deleteAll(any()) } returns Unit
                    every { reportOperationHoursRepository.findByFoodSpotsHistoryId(any()) } returns
                        listOf(
                            createTestReportOperationHours(),
                        )
                    every { reportOperationHoursRepository.deleteAll(any()) } returns Unit
                    every { foodSpotsPhotoRepository.findByHistoryId(any()) } returns
                        listOf(
                            createTestFoodSpotsPhoto(),
                        )
                    every { foodSpotsPhotoRepository.deleteAll(any()) } returns Unit
                    every { entityManager.flush() } returns Unit
                    every { userCommandService.decreaseCoin(any(), any()) } returns Unit
                    every { imageService.remove(any()) } returns Unit
                    then("정상적으로 삭제되어야 한다.") {
                        foodSpotsCommandService.deleteFoodSpotsHistories(user, TEST_FOOD_SPOTS_HISTORY_ID)
                        verify(exactly = 1) {
                            foodSpotsHistoryRepository.deleteById(any())
                        }
                    }
                }
            }
        },
    )
