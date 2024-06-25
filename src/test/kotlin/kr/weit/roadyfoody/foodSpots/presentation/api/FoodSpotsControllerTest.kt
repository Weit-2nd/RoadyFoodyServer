package kr.weit.roadyfoody.foodSpots.presentation.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOT_NAME_EMPTY
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOT_NAME_INVALID_STR
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOT_NAME_TOO_LONG
import kr.weit.roadyfoody.foodSpots.fixture.createFoodSpotsRequestFile
import kr.weit.roadyfoody.foodSpots.fixture.createMockPhotoList
import kr.weit.roadyfoody.foodSpots.fixture.createTestReportRequest
import kr.weit.roadyfoody.foodSpots.service.FoodSpotsService
import kr.weit.roadyfoody.support.annotation.ControllerTest
import kr.weit.roadyfoody.support.utils.ImageFormat
import kr.weit.roadyfoody.support.utils.multipartWithAuth
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(FoodSpotsController::class)
@ControllerTest
class FoodSpotsControllerTest(
    @MockkBean private val foodSpotsService: FoodSpotsService,
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
) : BehaviorSpec(
        {
            val requestPath = "/api/v1/food-spots"

            given("POST $requestPath Test") {
                var reportRequest = createTestReportRequest()
                var reportPhotos = createMockPhotoList(ImageFormat.WEBP)
                every {
                    foodSpotsService.createReport(any(), any(), any())
                } returns Unit
                `when`("정상적인 데이터가 들어올 경우") {
                    then("가게 리포트가 등록된다.") {
                        mockMvc
                            .perform(
                                multipartWithAuth(requestPath)
                                    .file(createFoodSpotsRequestFile(objectMapper.writeValueAsBytes(reportRequest).inputStream()))
                                    .file("reportPhotos", reportPhotos[0].bytes)
                                    .file("reportPhotos", reportPhotos[1].bytes),
                            ).andExpect(status().isCreated)
                    }
                }

                reportRequest = createTestReportRequest(name = TEST_FOOD_SPOT_NAME_EMPTY)
                `when`("상호명이 공백인 경우") {
                    then("400을 반환") {
                        mockMvc
                            .perform(
                                multipartWithAuth(requestPath)
                                    .file(createFoodSpotsRequestFile(objectMapper.writeValueAsBytes(reportRequest).inputStream())),
                            ).andExpect(status().isBadRequest)
                    }
                }

                reportRequest = createTestReportRequest(name = TEST_FOOD_SPOT_NAME_INVALID_STR)
                `when`("상호명에 특수문자가 포함된 경우") {
                    then("400을 반환") {
                        mockMvc
                            .perform(
                                multipartWithAuth(requestPath)
                                    .file(createFoodSpotsRequestFile(objectMapper.writeValueAsBytes(reportRequest).inputStream())),
                            ).andExpect(status().isBadRequest)
                    }
                }

                reportRequest = createTestReportRequest(name = TEST_FOOD_SPOT_NAME_TOO_LONG)
                `when`("상호명이 30자 초과인 경우") {
                    then("400을 반환") {
                        mockMvc
                            .perform(
                                multipartWithAuth(requestPath)
                                    .file(createFoodSpotsRequestFile(objectMapper.writeValueAsBytes(reportRequest).inputStream())),
                            ).andExpect(status().isBadRequest)
                    }
                }

                reportRequest = createTestReportRequest(longitude = 190.0)
                `when`("경도가 범위를 벗어난 경우") {
                    then("400을 반환") {
                        mockMvc
                            .perform(
                                multipartWithAuth(requestPath)
                                    .file(createFoodSpotsRequestFile(objectMapper.writeValueAsBytes(reportRequest).inputStream())),
                            ).andExpect(status().isBadRequest)
                    }
                }

                reportRequest = createTestReportRequest(latitude = -190.0)
                `when`("위도가 범위를 벗어난 경우") {
                    then("400을 반환") {
                        mockMvc
                            .perform(
                                multipartWithAuth(requestPath)
                                    .file(
                                        createFoodSpotsRequestFile(
                                            objectMapper
                                                .writeValueAsBytes(
                                                    reportRequest,
                                                ).inputStream(),
                                        ),
                                    ),
                            ).andExpect(status().isBadRequest)
                    }
                }

                reportRequest = createTestReportRequest()
                reportPhotos = createMockPhotoList(ImageFormat.WEBP) + createMockPhotoList(ImageFormat.WEBP)
                `when`("이미지가 3개 초과인 경우") {
                    then("400을 반환") {
                        mockMvc
                            .perform(
                                multipartWithAuth(requestPath)
                                    .file(createFoodSpotsRequestFile(objectMapper.writeValueAsBytes(reportRequest).inputStream()))
                                    .file("reportPhotos", reportPhotos[0].bytes)
                                    .file("reportPhotos", reportPhotos[1].bytes)
                                    .file("reportPhotos", reportPhotos[2].bytes)
                                    .file("reportPhotos", reportPhotos[3].bytes),
                            ).andExpect(status().isBadRequest)
                    }
                }

                reportPhotos = createMockPhotoList(ImageFormat.JPEG)
                `when`("이미지 형식이 webp가 아닌 경우") {
                    then("400을 반환") {
                        mockMvc
                            .perform(
                                multipartWithAuth(requestPath)
                                    .file(createFoodSpotsRequestFile(objectMapper.writeValueAsBytes(reportRequest).inputStream()))
                                    .file("reportPhotos", reportPhotos[0].bytes)
                                    .file("reportPhotos", reportPhotos[1].bytes),
                            ).andExpect(status().isBadRequest)
                    }
                }
            }
        },
    )
