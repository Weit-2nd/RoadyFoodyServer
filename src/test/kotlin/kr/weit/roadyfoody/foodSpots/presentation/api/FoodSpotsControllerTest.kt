package kr.weit.roadyfoody.foodSpots.presentation.api

import TEST_FOOD_SPOT_ID
import TEST_INVALID_FOOD_SPOT_ID
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kr.weit.roadyfoody.common.dto.SliceResponse
import kr.weit.roadyfoody.foodSpots.application.service.FoodSpotsCommandService
import kr.weit.roadyfoody.foodSpots.application.service.FoodSpotsQueryService
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOTS_HAS_NEXT
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOTS_HISTORY_ID
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOTS_LAST_ID
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOTS_REQUEST_NAME
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOTS_REQUEST_PHOTO
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOTS_SIZE
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOT_NAME_EMPTY
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOT_NAME_INVALID_STR
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOT_NAME_TOO_LONG
import kr.weit.roadyfoody.foodSpots.fixture.TEST_INVALID_TIME_FORMAT
import kr.weit.roadyfoody.foodSpots.fixture.createMockPhotoList
import kr.weit.roadyfoody.foodSpots.fixture.createOperationHoursRequest
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodSpotsUpdateRequest
import kr.weit.roadyfoody.foodSpots.fixture.createTestReportHistoriesResponse
import kr.weit.roadyfoody.foodSpots.fixture.createTestReportRequest
import kr.weit.roadyfoody.support.annotation.ControllerTest
import kr.weit.roadyfoody.support.utils.ImageFormat
import kr.weit.roadyfoody.support.utils.ImageFormat.WEBP
import kr.weit.roadyfoody.support.utils.createMultipartFile
import kr.weit.roadyfoody.support.utils.createTestImageFile
import kr.weit.roadyfoody.support.utils.deleteWithAuth
import kr.weit.roadyfoody.support.utils.getWithAuth
import kr.weit.roadyfoody.support.utils.multipartWithAuth
import kr.weit.roadyfoody.support.utils.patchWithAuth
import kr.weit.roadyfoody.user.fixture.TEST_USER_ID
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(FoodSpotsController::class)
@ControllerTest
class FoodSpotsControllerTest(
    @MockkBean private val foodSpotsCommandService: FoodSpotsCommandService,
    @MockkBean private val foodSpotsQueryService: FoodSpotsQueryService,
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
) : BehaviorSpec(
        {
            val requestPath = "/api/v1/food-spots"

            given("POST $requestPath Test") {
                var reportRequest = createTestReportRequest()
                var reportPhotos = createMockPhotoList(WEBP)
                every {
                    foodSpotsCommandService.createReport(any(), any(), any())
                } returns Unit
                `when`("정상적인 데이터가 들어올 경우") {
                    then("가게 리포트가 등록된다.") {
                        mockMvc
                            .perform(
                                multipartWithAuth(requestPath)
                                    .file(
                                        createMultipartFile(
                                            TEST_FOOD_SPOTS_REQUEST_NAME,
                                            objectMapper
                                                .writeValueAsBytes(reportRequest)
                                                .inputStream(),
                                        ),
                                    ).file("reportPhotos", reportPhotos[0].bytes)
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
                                    .file(
                                        createMultipartFile(
                                            TEST_FOOD_SPOTS_REQUEST_NAME,
                                            objectMapper
                                                .writeValueAsBytes(reportRequest)
                                                .inputStream(),
                                        ),
                                    ),
                            ).andExpect(status().isBadRequest)
                    }
                }

                reportRequest = createTestReportRequest(name = TEST_FOOD_SPOT_NAME_INVALID_STR)
                `when`("상호명에 허용되지 않은 특수문자가 포함된 경우") {
                    then("400을 반환") {
                        mockMvc
                            .perform(
                                multipartWithAuth(requestPath)
                                    .file(
                                        createMultipartFile(
                                            TEST_FOOD_SPOTS_REQUEST_NAME,
                                            objectMapper
                                                .writeValueAsBytes(reportRequest)
                                                .inputStream(),
                                        ),
                                    ),
                            ).andExpect(status().isBadRequest)
                    }
                }

                reportRequest = createTestReportRequest(name = TEST_FOOD_SPOT_NAME_TOO_LONG)
                `when`("상호명이 30자 초과인 경우") {
                    then("400을 반환") {
                        mockMvc
                            .perform(
                                multipartWithAuth(requestPath)
                                    .file(
                                        createMultipartFile(
                                            TEST_FOOD_SPOTS_REQUEST_NAME,
                                            objectMapper
                                                .writeValueAsBytes(reportRequest)
                                                .inputStream(),
                                        ),
                                    ),
                            ).andExpect(status().isBadRequest)
                    }
                }

                reportRequest = createTestReportRequest(longitude = 190.0)
                `when`("경도가 범위를 벗어난 경우") {
                    then("400을 반환") {
                        mockMvc
                            .perform(
                                multipartWithAuth(requestPath)
                                    .file(
                                        createMultipartFile(
                                            TEST_FOOD_SPOTS_REQUEST_NAME,
                                            objectMapper
                                                .writeValueAsBytes(reportRequest)
                                                .inputStream(),
                                        ),
                                    ),
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
                                        createMultipartFile(
                                            TEST_FOOD_SPOTS_REQUEST_NAME,
                                            objectMapper
                                                .writeValueAsBytes(reportRequest)
                                                .inputStream(),
                                        ),
                                    ),
                            ).andExpect(status().isBadRequest)
                    }
                }

                reportRequest = createTestReportRequest(foodCategories = setOf())
                `when`("음식 카테고리가 없는 경우") {
                    then("400을 반환") {
                        mockMvc
                            .perform(
                                multipartWithAuth(requestPath)
                                    .file(
                                        createMultipartFile(
                                            TEST_FOOD_SPOTS_REQUEST_NAME,
                                            objectMapper
                                                .writeValueAsBytes(reportRequest)
                                                .inputStream(),
                                        ),
                                    ),
                            ).andExpect(status().isBadRequest)
                    }
                }

                reportRequest =
                    createTestReportRequest(
                        operationHours =
                            listOf(
                                createOperationHoursRequest(openingHours = TEST_INVALID_TIME_FORMAT),
                            ),
                    )
                `when`("운영시간 형식이 잘못된 경우") {
                    then("400을 반환") {
                        mockMvc
                            .perform(
                                multipartWithAuth(requestPath)
                                    .file(
                                        createMultipartFile(
                                            TEST_FOOD_SPOTS_REQUEST_NAME,
                                            objectMapper
                                                .writeValueAsBytes(reportRequest)
                                                .inputStream(),
                                        ),
                                    ),
                            ).apply {
                                this.toString()
                            }.andExpect(status().isBadRequest)
                    }

                    reportRequest = createTestReportRequest()
                    reportPhotos = createMockPhotoList(WEBP, size = 4)
                    `when`("이미지가 3개 초과인 경우") {
                        then("400을 반환") {
                            mockMvc
                                .perform(
                                    multipartWithAuth(requestPath)
                                        .file(
                                            createMultipartFile(
                                                TEST_FOOD_SPOTS_REQUEST_NAME,
                                                objectMapper
                                                    .writeValueAsBytes(reportRequest)
                                                    .inputStream(),
                                            ),
                                        ).file("reportPhotos", reportPhotos[0].bytes)
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
                                        .file(
                                            createMultipartFile(
                                                TEST_FOOD_SPOTS_REQUEST_NAME,
                                                objectMapper
                                                    .writeValueAsBytes(reportRequest)
                                                    .inputStream(),
                                            ),
                                        ).file("reportPhotos", reportPhotos[0].bytes)
                                        .file("reportPhotos", reportPhotos[1].bytes),
                                ).andExpect(status().isBadRequest)
                        }
                    }

                    `when`("파일의 크기가 1MB를 초과하면") {
                        val mockFile: MockMultipartFile = mockk<MockMultipartFile>()
                        every { mockFile.size } returns 1024 * 1024 + 1
                        every { mockFile.name } returns TEST_FOOD_SPOTS_REQUEST_PHOTO
                        every { mockFile.inputStream } returns createTestImageFile(WEBP).inputStream
                        every {
                            foodSpotsCommandService.createReport(
                                any(),
                                any(),
                                any(),
                            )
                        } just runs
                        then("400을 반환") {
                            mockMvc
                                .perform(
                                    multipartWithAuth(requestPath)
                                        .file(
                                            createMultipartFile(
                                                TEST_FOOD_SPOTS_REQUEST_NAME,
                                                objectMapper
                                                    .writeValueAsBytes(reportRequest)
                                                    .inputStream(),
                                            ),
                                        ).file(mockFile),
                                ).andExpect(status().isBadRequest)
                            verify(exactly = 0) {
                                foodSpotsCommandService.createReport(
                                    any(),
                                    any(),
                                    any(),
                                )
                            }
                        }
                    }
                }
            }

            given("GET $requestPath/histories/{userId} Test") {
                val response =
                    SliceResponse(
                        listOf(createTestReportHistoriesResponse()),
                        TEST_FOOD_SPOTS_HAS_NEXT,
                    )
                every {
                    foodSpotsQueryService.getReportHistories(any(), any(), any())
                } returns response
                `when`("정상적인 요청이 들어올 경우") {
                    then("해당 유저의 리포트 이력을 반환한다.") {
                        mockMvc
                            .perform(
                                getWithAuth("$requestPath/histories/$TEST_USER_ID")
                                    .param("size", TEST_FOOD_SPOTS_SIZE.toString())
                                    .param("lastId", TEST_FOOD_SPOTS_LAST_ID.toString()),
                            ).andExpect(status().isOk)
                    }
                }

                `when`("size와 lastId가 없는 경우") {
                    every {
                        foodSpotsQueryService.getReportHistories(any(), any(), any())
                    } returns response
                    then("기본값으로 해당 유저의 리포트 이력을 반환한다.") {
                        mockMvc
                            .perform(
                                getWithAuth("$requestPath/histories/$TEST_USER_ID"),
                            ).andExpect(status().isOk)
                    }
                }

                `when`("size가 양수가 아닌 경우") {
                    then("400을 반환") {
                        mockMvc
                            .perform(
                                getWithAuth("$requestPath/histories/$TEST_USER_ID")
                                    .param("size", "0"),
                            ).andExpect(status().isBadRequest)
                    }
                }

                `when`("lastId가 양수가 아닌 경우") {
                    then("400을 반환") {
                        mockMvc
                            .perform(
                                getWithAuth("$requestPath/histories/$TEST_USER_ID")
                                    .param("lastId", "-1"),
                            ).andExpect(status().isBadRequest)
                    }
                }
            }

            given("PATCH $requestPath/{foodSpotsId} Test") {
                every { foodSpotsCommandService.doUpdateReport(any(), any(), any()) } just runs
                `when`("정상적인 요청이 들어올 경우") {
                    val request = createTestFoodSpotsUpdateRequest()
                    then("해당 가게 정보를 수정한다.") {
                        mockMvc
                            .perform(
                                patchWithAuth("$requestPath/$TEST_FOOD_SPOT_ID")
                                    .content(objectMapper.writeValueAsString(request))
                                    .contentType("application/json"),
                            ).andExpect(status().isCreated)
                    }
                }

                `when`("음식점 ID가 양수가 아닌 경우") {
                    then("400을 반환") {
                        mockMvc
                            .perform(
                                patchWithAuth("$requestPath/$TEST_INVALID_FOOD_SPOT_ID")
                                    .content(objectMapper.writeValueAsString(createTestFoodSpotsUpdateRequest()))
                                    .contentType("application/json"),
                            ).andExpect(status().isBadRequest)
                    }
                }

                `when`("상호명이 공백인 경우") {
                    val request = createTestFoodSpotsUpdateRequest(name = TEST_FOOD_SPOT_NAME_EMPTY)
                    then("400을 반환") {
                        mockMvc
                            .perform(
                                patchWithAuth("$requestPath/$TEST_FOOD_SPOT_ID")
                                    .content(
                                        objectMapper.writeValueAsString(request),
                                    ).contentType("application/json"),
                            ).andExpect(status().isBadRequest)
                    }
                }

                `when`("상호명에 허용되지 않은 특수문자가 포함된 경우") {
                    val request = createTestFoodSpotsUpdateRequest(name = TEST_FOOD_SPOT_NAME_INVALID_STR)
                    then("400을 반환") {
                        mockMvc
                            .perform(
                                patchWithAuth("$requestPath/$TEST_FOOD_SPOT_ID")
                                    .content(
                                        objectMapper.writeValueAsString(request),
                                    ).contentType("application/json"),
                            ).andExpect(status().isBadRequest)
                    }
                }

                `when`("상호명이 30자 초과인 경우") {
                    val request = createTestFoodSpotsUpdateRequest(name = TEST_FOOD_SPOT_NAME_TOO_LONG)
                    then("400을 반환") {
                        mockMvc
                            .perform(
                                patchWithAuth("$requestPath/$TEST_FOOD_SPOT_ID")
                                    .content(
                                        objectMapper.writeValueAsString(request),
                                    ).contentType("application/json"),
                            ).andExpect(status().isBadRequest)
                    }
                }

                `when`("경도가 범위를 벗어난 경우") {
                    val request = createTestFoodSpotsUpdateRequest(longitude = 190.0)
                    then("400을 반환") {
                        mockMvc
                            .perform(
                                patchWithAuth("$requestPath/$TEST_FOOD_SPOT_ID")
                                    .content(
                                        objectMapper.writeValueAsString(request),
                                    ).contentType("application/json"),
                            ).andExpect(status().isBadRequest)
                    }
                }

                `when`("위도가 범위를 벗어난 경우") {
                    val request = createTestFoodSpotsUpdateRequest(latitude = -190.0)
                    then("400을 반환") {
                        mockMvc
                            .perform(
                                patchWithAuth("$requestPath/$TEST_FOOD_SPOT_ID")
                                    .content(
                                        objectMapper.writeValueAsString(request),
                                    ).contentType("application/json"),
                            ).andExpect(status().isBadRequest)
                    }
                }

                `when`("운영시간 형식이 잘못된 경우") {
                    val request =
                        createTestFoodSpotsUpdateRequest(
                            operationHours =
                                listOf(
                                    createOperationHoursRequest(openingHours = TEST_INVALID_TIME_FORMAT),
                                ),
                        )
                    then("400을 반환") {
                        mockMvc
                            .perform(
                                patchWithAuth("$requestPath/$TEST_FOOD_SPOT_ID")
                                    .content(
                                        objectMapper.writeValueAsString(request),
                                    ).contentType("application/json"),
                            ).andExpect(status().isBadRequest)
                    }
                }
            }

            given("DELETE $requestPath/histories/{historyId}") {
                `when`("음수인 리포트 ID가 들어올 경우") {
                    then("400을 반환한다.") {
                        val it =
                            mockMvc
                                .perform(
                                    deleteWithAuth("$requestPath/histories/-1"),
                                )
                        it.andExpect(status().isBadRequest)
                    }
                }

                every {
                    foodSpotsCommandService.deleteFoodSpotsHistories(any(), any())
                } just runs
                `when`("정상적인 요청이 들어올 경우") {
                    then("해당 리포트를 삭제한다.") {
                        mockMvc
                            .perform(
                                deleteWithAuth("$requestPath/histories/$TEST_FOOD_SPOTS_HISTORY_ID"),
                            ).andExpect(status().isNoContent)
                    }
                }
            }
        },
    )
