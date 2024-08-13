package kr.weit.roadyfoody.rewards.presentation.api

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import kr.weit.roadyfoody.rewards.application.service.RewardsQueryService
import kr.weit.roadyfoody.rewards.fixture.createRewardsResponse
import kr.weit.roadyfoody.support.annotation.ControllerTest
import kr.weit.roadyfoody.support.utils.getWithAuth
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ControllerTest
@WebMvcTest(RewardsController::class)
class RewardsControllerTest(
    @MockkBean private val rewardsQueryService: RewardsQueryService,
    private val mockMvc: MockMvc,
) : BehaviorSpec(
    {
        val requestPath = "/api/v1/rewards"

        given("GET $requestPath/user Test") {
            val response = createRewardsResponse()
            every {
                rewardsQueryService.getUserRewards(any(), any())
            } returns response

            `when`("정상 요청시") {
                then("유저의 리워드 리스트가 조회된다.") {
                    val result = mockMvc.perform(
                        getWithAuth("$requestPath/user")
                            .param("size", "10")
                            .param("page", "0")
                    ).andExpect(status().isOk)

                    result.andExpect(jsonPath("$.contents[0].rewardType").value("지급"))
                    result.andExpect(jsonPath("$.contents[0].rewardPoint").value(100))
                    result.andExpect(jsonPath("$.contents[0].rewardReason").value("리포트 업데이트"))
                    result.andExpect(jsonPath("$.contents[0].createdAt").isNotEmpty)
                    result.andExpect(jsonPath("$.hasNext").value(false))
                }
            }
        }
    }
)