package kr.weit.roadyfoody.admin.presentation.api

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import kr.weit.roadyfoody.admin.application.service.AdminCommandService
import kr.weit.roadyfoody.admin.application.service.AdminQueryService
import kr.weit.roadyfoody.admin.dto.UserAccessTokenResponse
import kr.weit.roadyfoody.admin.fixture.createTestSimpleUserInfoResponses
import kr.weit.roadyfoody.auth.fixture.TEST_ACCESS_TOKEN
import kr.weit.roadyfoody.support.annotation.ControllerTest
import kr.weit.roadyfoody.user.fixture.TEST_USER_ID
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(AdminController::class)
@ControllerTest
class AdminControllerTest(
    @MockkBean private val adminCommandService: AdminCommandService,
    @MockkBean private val adminQueryService: AdminQueryService,
    private val mockMvc: MockMvc,
) : BehaviorSpec({
        val requestUrl = "/api/v1/admin"
        given("GET $requestUrl/users 테스트") {
            `when`("정상적으로 요청하면") {
                every { adminQueryService.getUserInfoList(any()) } returns createTestSimpleUserInfoResponses()
                then("200 상태코드를 반환한다.") {
                    mockMvc
                        .perform(get("$requestUrl/users"))
                        .andExpect(status().isOk)
                }
            }
        }

        given("GET $requestUrl/users/{userId}/token 테스트") {
            `when`("정상적으로 요청하면") {
                every { adminCommandService.getUserAccessToken(TEST_USER_ID) } returns UserAccessTokenResponse(TEST_ACCESS_TOKEN)
                then("200 상태코드를 반환한다.") {
                    mockMvc
                        .perform(get("$requestUrl/users/$TEST_USER_ID/token"))
                        .andExpect(status().isOk)
                }
            }
        }
    })
