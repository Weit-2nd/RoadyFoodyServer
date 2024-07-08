package kr.weit.roadyfoody.search.foodSpots.presentation.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import kr.weit.roadyfoody.foodSpots.fixture.createFoodSpotsSearchResponses
import kr.weit.roadyfoody.search.foodSpots.application.FoodSpotsSearchService
import kr.weit.roadyfoody.support.annotation.ControllerTest
import org.mockito.Mockito.`when`
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(FoodSpotsSearchController::class)
@ControllerTest
class FoodSpotsSearchControllerTest(
    @MockkBean private val foodSpotsSearchService: FoodSpotsSearchService,
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
) : BehaviorSpec({
        val requestPath = "/api/v1/food-spots"

        given("GET searchFoodSpots API 호출") {
            every {
                foodSpotsSearchService.searchFoodSpots(any())
            } returns createFoodSpotsSearchResponses()
            `when`("정상적인 요청이 들어온 경우") {

                mockMvc.perform(
                    get("$requestPath/search")
                        .contentType("application/json")
                        .param("centerLongitude", "127.074667")
                        .param("centerLatitude", "37.147030")
                        .param("radius", "500")
                        .param("name", "pot2")
                        .param("categoryIds", "1,2"),
                )
                    .andExpect(status().isOk)
                    .andExpect(
                        content().json(
                            objectMapper.writeValueAsString(
                                createFoodSpotsSearchResponses(),
                            ),
                        ),
                    )
            }
        }
    })
