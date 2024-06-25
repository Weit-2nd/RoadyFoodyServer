package kr.weit.roadyfoody.foodSpots.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kr.weit.roadyfoody.foodSpots.fixture.createMockPhotoList
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodHistory
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodSpot
import kr.weit.roadyfoody.foodSpots.fixture.createTestReportRequest
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsHistoryRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsRepository
import kr.weit.roadyfoody.global.service.ImageService
import kr.weit.roadyfoody.support.utils.ImageFormat
import kr.weit.roadyfoody.user.exception.UserNotFoundException
import kr.weit.roadyfoody.user.fixture.TEST_USER_ID
import kr.weit.roadyfoody.user.fixture.createTestUser
import kr.weit.roadyfoody.user.repository.UserRepository
import java.util.Optional

class FoodSpotsServiceTest :
    BehaviorSpec(
        {
            val foodSpotsRepository = mockk<FoodSpotsRepository>()
            val foodSpotsHistoryRepository = mockk<FoodSpotsHistoryRepository>()
            val userRepository = mockk<UserRepository>()
            val imageService = spyk(ImageService(mockk()))
            val foodSpotsService = FoodSpotsService(foodSpotsRepository, foodSpotsHistoryRepository, userRepository, imageService)

            given("createReport 테스트") {
                val user = createTestUser()
                every { foodSpotsRepository.save(any()) } returns createTestFoodSpot()
                every { foodSpotsHistoryRepository.save(any()) } returns createTestFoodHistory()
                every { userRepository.findById(TEST_USER_ID) } returns Optional.of(user)
                every { imageService.upload(any(), any()) } returns Unit
                `when`("정상적인 데이터와 이미지가 들어올 경우") {
                    then("정상적으로 저장되어야 한다.") {
                        foodSpotsService.createReport(TEST_USER_ID, createTestReportRequest(), createMockPhotoList(ImageFormat.WEBP))
                    }
                }

                `when`("정상적인 데이터만 들어올 경우") {
                    every { userRepository.findById(TEST_USER_ID) } returns Optional.of(user)
                    then("정상적으로 저장되어야 한다.") {
                        foodSpotsService.createReport(TEST_USER_ID, createTestReportRequest(), emptyList())
                    }
                }

                `when`("사용자가 존재하지 않는 경우") {
                    every { userRepository.findById(TEST_USER_ID) } returns Optional.empty()
                    then("UserNotFoundException이 발생한다.") {
                        shouldThrow<UserNotFoundException> {
                            foodSpotsService.createReport(TEST_USER_ID, createTestReportRequest(), createMockPhotoList(ImageFormat.WEBP))
                        }
                    }
                }
            }
        },
    )
