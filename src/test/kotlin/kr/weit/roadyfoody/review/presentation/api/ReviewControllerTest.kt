package kr.weit.roadyfoody.review.presentation.api

import TEST_INVALID_FOOD_SPOT_ID
import TEST_INVALID_RATING
import TEST_INVALID_RATING_OVER
import TEST_INVALID_REVIEW_ID
import TEST_REVIEW_CONTENT_MAX_LENGTH
import TEST_REVIEW_CREATE_REQUEST_NAME
import TEST_REVIEW_ID
import TEST_REVIEW_REQUEST_PHOTO
import TEST_REVIEW_UPDATE_REQUEST_NAME
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import createTestReviewRequest
import createTestReviewUpdateRequest
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kr.weit.roadyfoody.foodSpots.fixture.createMockPhotoList
import kr.weit.roadyfoody.review.application.service.ReviewCommandService
import kr.weit.roadyfoody.support.annotation.ControllerTest
import kr.weit.roadyfoody.support.utils.ImageFormat.PNG
import kr.weit.roadyfoody.support.utils.ImageFormat.WEBP
import kr.weit.roadyfoody.support.utils.createMultipartFile
import kr.weit.roadyfoody.support.utils.createTestImageFile
import kr.weit.roadyfoody.support.utils.deleteWithAuth
import kr.weit.roadyfoody.support.utils.multipartPatchWithAuth
import kr.weit.roadyfoody.support.utils.multipartWithAuth
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(ReviewController::class)
@ControllerTest
class ReviewControllerTest(
    @MockkBean private val reviewCommandService: ReviewCommandService,
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
) : BehaviorSpec(
        {
            val requestPath = "/api/v1/review"

            given("POST $requestPath Test") {
                var reportRequest = createTestReviewRequest()
                var reportPhotos = createMockPhotoList(WEBP)
                every {
                    reviewCommandService.createReview(any(), any(), any())
                } returns Unit
                `when`("정상적인 데이터가 들어올 경우") {
                    then("리뷰가 등록된다.") {
                        mockMvc
                            .perform(
                                multipartWithAuth(requestPath)
                                    .file(
                                        createMultipartFile(
                                            TEST_REVIEW_CREATE_REQUEST_NAME,
                                            objectMapper
                                                .writeValueAsBytes(reportRequest)
                                                .inputStream(),
                                        ),
                                    ).file(TEST_REVIEW_REQUEST_PHOTO, reportPhotos[0].bytes)
                                    .file(TEST_REVIEW_REQUEST_PHOTO, reportPhotos[1].bytes),
                            ).andExpect(status().isCreated)
                    }
                }

                reportRequest = createTestReviewRequest(foodSpotsId = TEST_INVALID_FOOD_SPOT_ID)
                `when`("음식점 id가 양수가 아닌 경우") {
                    then("400 반환") {
                        mockMvc
                            .perform(
                                multipartWithAuth(requestPath)
                                    .file(
                                        createMultipartFile(
                                            TEST_REVIEW_CREATE_REQUEST_NAME,
                                            objectMapper
                                                .writeValueAsBytes(reportRequest)
                                                .inputStream(),
                                        ),
                                    ),
                            ).andExpect(status().isBadRequest)
                    }
                }

                reportRequest = createTestReviewRequest(contents = "")
                `when`("리뷰가 공백인 경우") {
                    then("400 반환") {
                        mockMvc
                            .perform(
                                multipartWithAuth(requestPath)
                                    .file(
                                        createMultipartFile(
                                            TEST_REVIEW_CREATE_REQUEST_NAME,
                                            objectMapper
                                                .writeValueAsBytes(reportRequest)
                                                .inputStream(),
                                        ),
                                    ),
                            ).andExpect(status().isBadRequest)
                    }
                }

                reportRequest =
                    createTestReviewRequest(contents = "a".repeat(TEST_REVIEW_CONTENT_MAX_LENGTH + 1))
                `when`("리뷰가 최대 길이를 초과한 경우") {
                    then("400 반환") {
                        mockMvc
                            .perform(
                                multipartWithAuth(requestPath)
                                    .file(
                                        createMultipartFile(
                                            TEST_REVIEW_CREATE_REQUEST_NAME,
                                            objectMapper
                                                .writeValueAsBytes(reportRequest)
                                                .inputStream(),
                                        ),
                                    ),
                            ).andExpect(status().isBadRequest)
                    }
                }

                reportRequest = createTestReviewRequest(rating = TEST_INVALID_RATING)
                `when`("별점이 1점보다 작은 경우") {
                    then("400 반환") {
                        mockMvc
                            .perform(
                                multipartWithAuth(requestPath)
                                    .file(
                                        createMultipartFile(
                                            TEST_REVIEW_CREATE_REQUEST_NAME,
                                            objectMapper
                                                .writeValueAsBytes(reportRequest)
                                                .inputStream(),
                                        ),
                                    ),
                            ).andExpect(status().isBadRequest)
                    }
                }

                reportRequest = createTestReviewRequest(rating = TEST_INVALID_RATING_OVER)
                `when`("별점이 5점보다 큰 경우") {
                    then("400 반환") {
                        mockMvc
                            .perform(
                                multipartWithAuth(requestPath)
                                    .file(
                                        createMultipartFile(
                                            TEST_REVIEW_CREATE_REQUEST_NAME,
                                            objectMapper
                                                .writeValueAsBytes(reportRequest)
                                                .inputStream(),
                                        ),
                                    ),
                            ).andExpect(status().isBadRequest)
                    }
                }

                reportRequest = createTestReviewRequest()
                reportPhotos = createMockPhotoList(WEBP, size = 4)
                `when`("리뷰 사진이 3개를 초과한 경우") {
                    then("400 반환") {
                        mockMvc
                            .perform(
                                multipartWithAuth(requestPath)
                                    .file(
                                        createMultipartFile(
                                            TEST_REVIEW_CREATE_REQUEST_NAME,
                                            objectMapper
                                                .writeValueAsBytes(reportRequest)
                                                .inputStream(),
                                        ),
                                    ).file(TEST_REVIEW_REQUEST_PHOTO, reportPhotos[0].bytes)
                                    .file(TEST_REVIEW_REQUEST_PHOTO, reportPhotos[1].bytes)
                                    .file(TEST_REVIEW_REQUEST_PHOTO, reportPhotos[2].bytes)
                                    .file(TEST_REVIEW_REQUEST_PHOTO, reportPhotos[3].bytes),
                            ).andExpect(status().isBadRequest)
                    }
                }

                reportPhotos = createMockPhotoList(PNG)
                `when`("리뷰 사진의 형식이 webp가 아닌 경우") {
                    then("400 반환") {
                        mockMvc
                            .perform(
                                multipartWithAuth(requestPath)
                                    .file(
                                        createMultipartFile(
                                            TEST_REVIEW_CREATE_REQUEST_NAME,
                                            objectMapper
                                                .writeValueAsBytes(reportRequest)
                                                .inputStream(),
                                        ),
                                    ).file(TEST_REVIEW_REQUEST_PHOTO, reportPhotos[0].bytes)
                                    .file(TEST_REVIEW_REQUEST_PHOTO, reportPhotos[1].bytes),
                            ).andExpect(status().isBadRequest)
                    }
                }

                val mockPhoto = mockk<MockMultipartFile>()
                `when`("파일의 크기가 1MB를 초과하면") {
                    every { mockPhoto.size } returns 1024 * 1024 + 1
                    every { mockPhoto.name } returns TEST_REVIEW_REQUEST_PHOTO
                    every { mockPhoto.inputStream } returns createTestImageFile(WEBP).inputStream
                    every { reviewCommandService.createReview(any(), any(), any()) } just runs
                    then("400 반환") {
                        mockMvc
                            .perform(
                                multipartWithAuth(requestPath)
                                    .file(
                                        createMultipartFile(
                                            TEST_REVIEW_CREATE_REQUEST_NAME,
                                            objectMapper
                                                .writeValueAsBytes(reportRequest)
                                                .inputStream(),
                                        ),
                                    ).file(mockPhoto),
                            ).andExpect(status().isBadRequest)
                        verify(exactly = 0) {
                            reviewCommandService.createReview(
                                any(),
                                any(),
                                any(),
                            )
                        }
                    }
                }
            }

            given("DELETE $requestPath/{reviewId}") {
                `when`("음수인 리뷰 ID가 들어올 경우") {
                    then("400을 반환한다.") {
                        val it =
                            mockMvc
                                .perform(
                                    deleteWithAuth("$requestPath/-1"),
                                )
                        it.andExpect(status().isBadRequest)
                    }
                }

                every {
                    reviewCommandService.deleteReview(any(), any())
                } just runs
                `when`("정상적인 요청이 들어올 경우") {
                    then("해당 리뷰를 삭제한다.") {
                        mockMvc
                            .perform(
                                deleteWithAuth("$requestPath/$TEST_REVIEW_ID"),
                            ).andExpect(status().isNoContent)
                    }
                }
            }

            given("PATCH $requestPath/{reviewId}") {
                val reportRequest = createTestReviewUpdateRequest()
                val reportPhotos = createMockPhotoList(WEBP)
                every {
                    reviewCommandService.updateReview(any(), any(), any(), any())
                } returns Unit
                `when`("정상적인 데이터가 들어올 경우") {
                    then("리뷰가 수정된다.") {
                        mockMvc
                            .perform(
                                multipartPatchWithAuth("$requestPath/$TEST_REVIEW_ID")
                                    .file(
                                        createMultipartFile(
                                            TEST_REVIEW_UPDATE_REQUEST_NAME,
                                            objectMapper
                                                .writeValueAsBytes(reportRequest)
                                                .inputStream(),
                                        ),
                                    ).file(TEST_REVIEW_REQUEST_PHOTO, reportPhotos[0].bytes)
                                    .file(TEST_REVIEW_REQUEST_PHOTO, reportPhotos[1].bytes),
                            ).andExpect(status().isNoContent)
                    }
                }

                `when`("리뷰 id가 양수가 아닌 경우") {
                    then("400 반환") {
                        mockMvc
                            .perform(
                                multipartPatchWithAuth("$requestPath/$TEST_INVALID_REVIEW_ID")
                                    .file(
                                        createMultipartFile(
                                            TEST_REVIEW_UPDATE_REQUEST_NAME,
                                            objectMapper
                                                .writeValueAsBytes(reportRequest)
                                                .inputStream(),
                                        ),
                                    ).file(TEST_REVIEW_REQUEST_PHOTO, reportPhotos[0].bytes)
                                    .file(TEST_REVIEW_REQUEST_PHOTO, reportPhotos[1].bytes),
                            ).andExpect(status().isBadRequest)
                    }
                }
            }
        },
    )
