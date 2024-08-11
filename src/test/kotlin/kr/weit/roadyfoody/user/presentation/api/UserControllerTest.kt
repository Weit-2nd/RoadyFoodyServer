package kr.weit.roadyfoody.user.presentation.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.weit.roadyfoody.auth.fixture.PROFILE_IMAGE_FILE_NAME
import kr.weit.roadyfoody.common.dto.SliceResponse
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOTS_HAS_NEXT
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOTS_LAST_ID
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOTS_SIZE
import kr.weit.roadyfoody.global.TEST_LAST_ID
import kr.weit.roadyfoody.global.TEST_NON_POSITIVE_ID
import kr.weit.roadyfoody.global.TEST_NON_POSITIVE_SIZE
import kr.weit.roadyfoody.global.TEST_PAGE_SIZE
import kr.weit.roadyfoody.support.annotation.ControllerTest
import kr.weit.roadyfoody.support.utils.ImageFormat
import kr.weit.roadyfoody.support.utils.createTestImageFile
import kr.weit.roadyfoody.support.utils.deleteWithAuth
import kr.weit.roadyfoody.support.utils.getWithAuth
import kr.weit.roadyfoody.support.utils.multipartPatchWithAuth
import kr.weit.roadyfoody.support.utils.patchWithAuth
import kr.weit.roadyfoody.user.application.service.UserCommandService
import kr.weit.roadyfoody.user.application.service.UserQueryService
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.fixture.TEST_MAX_LENGTH_NICKNAME
import kr.weit.roadyfoody.user.fixture.TEST_MIN_LENGTH_NICKNAME
import kr.weit.roadyfoody.user.fixture.TEST_USER_ID
import kr.weit.roadyfoody.user.fixture.createTestSliceResponseUserReview
import kr.weit.roadyfoody.user.fixture.createTestUserInfoResponse
import kr.weit.roadyfoody.user.fixture.createTestUserNicknameRequest
import kr.weit.roadyfoody.user.fixture.createTestUserReportHistoriesResponse
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.multipart.MultipartFile

@ControllerTest
@WebMvcTest(UserController::class)
class UserControllerTest(
    @MockkBean private val userQueryService: UserQueryService,
    @MockkBean private val userCommandService: UserCommandService,
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
) : BehaviorSpec({
        val requestPath = "/api/v1/users"

        given("GET $requestPath/me 테스트") {
            `when`("로그인한 유저의 정보를 조회하는 경우") {
                every { userQueryService.getUserInfo(any()) } returns createTestUserInfoResponse()
                then("로그인한 유저의 정보를 반환한다.") {
                    mockMvc
                        .perform(getWithAuth("$requestPath/me"))
                        .andExpect(status().isOk)
                }
            }

            `when`("로그인 정보 없이 유저의 정보를 조회하는 경우") {
                every { userQueryService.getUserInfo(any()) } returns createTestUserInfoResponse()
                then("로그인한 유저의 정보를 반환한다.") {
                    mockMvc
                        .perform(get("$requestPath/me"))
                        .andExpect(status().isUnauthorized)
                }
            }
        }
        given("GET $requestPath/{userId}/food-spots/histories Test") {
            val response =
                SliceResponse(
                    listOf(createTestUserReportHistoriesResponse()),
                    TEST_FOOD_SPOTS_HAS_NEXT,
                )
            every {
                userQueryService.getReportHistories(any(), any(), any())
            } returns response
            `when`("정상적인 요청이 들어올 경우") {
                then("해당 유저의 리포트 이력을 반환한다.") {
                    mockMvc
                        .perform(
                            getWithAuth("$requestPath/$TEST_USER_ID/food-spots/histories")
                                .param("size", TEST_FOOD_SPOTS_SIZE.toString())
                                .param("lastId", TEST_FOOD_SPOTS_LAST_ID.toString()),
                        ).andExpect(status().isOk)
                }
            }

            `when`("size와 lastId가 없는 경우") {
                every {
                    userQueryService.getReportHistories(any(), any(), any())
                } returns response
                then("기본값으로 해당 유저의 리포트 이력을 반환한다.") {
                    mockMvc
                        .perform(
                            getWithAuth("$requestPath/$TEST_USER_ID/food-spots/histories"),
                        ).andExpect(status().isOk)
                }
            }

            `when`("size가 양수가 아닌 경우") {
                then("400을 반환") {
                    mockMvc
                        .perform(
                            getWithAuth("$requestPath/$TEST_USER_ID/food-spots/histories")
                                .param("size", "0"),
                        ).andExpect(status().isBadRequest)
                }
            }

            `when`("lastId가 양수가 아닌 경우") {
                then("400을 반환") {
                    mockMvc
                        .perform(
                            getWithAuth("$requestPath/$TEST_USER_ID/food-spots/histories")
                                .param("lastId", "-1"),
                        ).andExpect(status().isBadRequest)
                }
            }
        }

        given("GET $requestPath/{userId}/reviews Test") {
            val response = createTestSliceResponseUserReview()
            every {
                userQueryService.getUserReviews(any(), any(), any())
            } returns response
            `when`("정상적인 데이터가 들어올 경우") {
                then("유저의 리뷰 리스트가 조회된다.") {
                    mockMvc
                        .perform(
                            getWithAuth("$requestPath/$TEST_USER_ID/reviews")
                                .param("size", "$TEST_PAGE_SIZE")
                                .param("lastId", "$TEST_LAST_ID"),
                        ).andExpect(status().isOk)
                }
            }

            `when`("userId가 양수가 아닌 경우") {
                then("400 반환") {
                    mockMvc
                        .perform(
                            getWithAuth("$requestPath/$TEST_NON_POSITIVE_ID/reviews")
                                .param("size", "$TEST_PAGE_SIZE")
                                .param("lastId", "$TEST_LAST_ID"),
                        ).andExpect(status().isBadRequest)
                }
            }

            `when`("조회할 개수가 양수가 아닌 경우") {
                then("400 반환") {
                    mockMvc
                        .perform(
                            getWithAuth("$requestPath/$TEST_USER_ID/reviews")
                                .param("size", "$TEST_NON_POSITIVE_SIZE")
                                .param("lastId", "$TEST_LAST_ID"),
                        ).andExpect(status().isBadRequest)
                }
            }

            `when`("마지막 ID가 양수가 아닌 경우") {
                then("400 반환") {
                    mockMvc
                        .perform(
                            getWithAuth("$requestPath/$TEST_USER_ID/reviews")
                                .param("size", "$TEST_PAGE_SIZE")
                                .param("lastId", "$TEST_NON_POSITIVE_ID"),
                        ).andExpect(status().isBadRequest)
                }
            }
        }

        given("PATCH $requestPath/nickname Test") {
            var request = createTestUserNicknameRequest()
            `when`("정상적인 데이터가 들어올 경우") {
                every { userCommandService.updateNickname(any(), any()) } returns Unit
                then("닉네임이 변경된다.") {
                    mockMvc
                        .perform(
                            patchWithAuth("$requestPath/nickname")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON),
                        ).andExpect(status().isNoContent)
                    verify(exactly = 1) {
                        userCommandService.updateNickname(
                            any<User>(),
                            any<String>(),
                        )
                    }
                }
            }

            `when`("닉네임이 최소 길이보다 작은 경우") {
                request = request.copy(nickname = TEST_MIN_LENGTH_NICKNAME.dropLast(1))
                then("400 반환") {
                    mockMvc
                        .perform(
                            patchWithAuth("$requestPath/nickname")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON),
                        ).andExpect(status().isBadRequest)
                    verify(exactly = 0) {
                        userCommandService.updateNickname(
                            any<User>(),
                            any<String>(),
                        )
                    }
                }
            }

            `when`("닉네임이 최대 길이보다 큰 경우") {
                request = request.copy(nickname = TEST_MAX_LENGTH_NICKNAME + "a")
                then("400 반환") {
                    mockMvc
                        .perform(
                            patchWithAuth("$requestPath/nickname")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON),
                        ).andExpect(status().isBadRequest)
                    verify(exactly = 0) {
                        userCommandService.updateNickname(
                            any<User>(),
                            any<String>(),
                        )
                    }
                }
            }

            `when`("닉네임에 특수문자가 들어간 경우") {
                request = request.copy(nickname = "$TEST_MIN_LENGTH_NICKNAME! @#")
                then("400 반환") {
                    mockMvc
                        .perform(
                            patchWithAuth("$requestPath/nickname")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON),
                        ).andExpect(status().isBadRequest)
                    verify(exactly = 0) {
                        userCommandService.updateNickname(
                            any<User>(),
                            any<String>(),
                        )
                    }
                }
            }

            `when`("닉네임에 이모지가 들어간 경우") {
                request = request.copy(nickname = "${TEST_MIN_LENGTH_NICKNAME}☺️")
                then("400 반환") {
                    mockMvc
                        .perform(
                            patchWithAuth("$requestPath/nickname")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON),
                        ).andExpect(status().isBadRequest)
                    verify(exactly = 0) {
                        userCommandService.updateNickname(
                            any<User>(),
                            any<String>(),
                        )
                    }
                }
            }
        }

        given("PATCH $requestPath Test") {
            `when`("정상적인 데이터가 들어올 경우") {
                every { userCommandService.updateProfileImage(any(), any()) } returns Unit
                then("프로필이 변경된다.") {
                    mockMvc
                        .perform(
                            multipartPatchWithAuth("$requestPath/profile")
                                .file("profileImage", createTestImageFile(ImageFormat.WEBP).bytes),
                        ).andExpect(status().isNoContent)
                    verify(exactly = 1) {
                        userCommandService.updateProfileImage(
                            any<User>(),
                            any<MultipartFile>(),
                        )
                    }
                }
            }

            `when`("프로필 사진을 업로드하지 않은 경우") {
                then("400 반환") {
                    mockMvc
                        .perform(
                            multipartPatchWithAuth("$requestPath/profile"),
                        ).andExpect(status().isBadRequest)
                    verify(exactly = 0) {
                        userCommandService.updateProfileImage(
                            any<User>(),
                            any<MultipartFile>(),
                        )
                    }
                }
            }

            `when`("프로필 사진이 WEBP가 아닌 경우") {
                then("400 반환") {
                    mockMvc
                        .perform(
                            multipartPatchWithAuth("$requestPath/profile")
                                .file("profileImage", createTestImageFile(ImageFormat.JPEG).bytes),
                        ).andExpect(status().isBadRequest)
                    verify(exactly = 0) {
                        userCommandService.updateProfileImage(
                            any<User>(),
                            any<MultipartFile>(),
                        )
                    }
                }
            }

            `when`("프로필 사진의 크기가 1MB를 초과하는 경우") {
                val mockFile: MockMultipartFile = mockk<MockMultipartFile>()
                every { mockFile.size } returns 1024 * 1024 + 1
                every { mockFile.name } returns PROFILE_IMAGE_FILE_NAME
                every { mockFile.inputStream } returns createTestImageFile(ImageFormat.WEBP).inputStream
                then("400 반환") {
                    mockMvc
                        .perform(
                            multipartPatchWithAuth("$requestPath/profile")
                                .file(mockFile),
                        ).andExpect(status().isBadRequest)
                    verify(exactly = 0) {
                        userCommandService.updateProfileImage(
                            any<User>(),
                            any<MultipartFile>(),
                        )
                    }
                }
            }
        }

        given("DELETE $requestPath/profile Test") {
            `when`("정상적인 요청이 들어올 경우") {
                every { userCommandService.deleteProfileImage(any()) } returns Unit
                then("프로필 사진이 삭제된다.") {
                    mockMvc
                        .perform(
                            deleteWithAuth("$requestPath/profile"),
                        ).andExpect(status().isNoContent)
                    verify(exactly = 1) { userCommandService.deleteProfileImage(any<User>()) }
            }
        }
    }
    })
