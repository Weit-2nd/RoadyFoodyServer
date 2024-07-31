package kr.weit.roadyfoody.user.presentation.api

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import kr.weit.roadyfoody.common.dto.SliceResponse
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOTS_HAS_NEXT
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOTS_LAST_ID
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOTS_SIZE
import kr.weit.roadyfoody.global.TEST_LAST_ID
import kr.weit.roadyfoody.global.TEST_NON_POSITIVE_ID
import kr.weit.roadyfoody.global.TEST_NON_POSITIVE_SIZE
import kr.weit.roadyfoody.global.TEST_PAGE_SIZE
import kr.weit.roadyfoody.support.annotation.ControllerTest
import kr.weit.roadyfoody.support.utils.getWithAuth
import kr.weit.roadyfoody.user.application.service.UserQueryService
import kr.weit.roadyfoody.user.fixture.TEST_USER_ID
import kr.weit.roadyfoody.user.fixture.createTestSliceResponseUserReview
import kr.weit.roadyfoody.user.fixture.createTestUserInfoResponse
import kr.weit.roadyfoody.user.fixture.createTestUserReportHistoriesResponse
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ControllerTest
@WebMvcTest(UserController::class)
class UserControllerTest(
    @MockkBean private val userQueryService: UserQueryService,
    private val mockMvc: MockMvc,
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
    })
