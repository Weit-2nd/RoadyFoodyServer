package kr.weit.roadyfoody.user.presentation.api

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import kr.weit.roadyfoody.support.annotation.ControllerTest
import kr.weit.roadyfoody.support.utils.getWithAuth
import kr.weit.roadyfoody.user.application.service.UserQueryService
import kr.weit.roadyfoody.user.fixture.createTestUserInfoResponse
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
    })
