package kr.weit.roadyfoody.search.foodSpots.presentation.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import kr.weit.roadyfoody.foodSpots.fixture.createFoodSpotsSearchResponses
import kr.weit.roadyfoody.search.foodSpots.application.service.FoodSpotsSearchService
import kr.weit.roadyfoody.search.foodSpots.fixture.createFoodSpotsPopularSearchesResponse
import kr.weit.roadyfoody.search.foodSpots.fixture.createRequiredCoinResponse
import kr.weit.roadyfoody.support.annotation.ControllerTest
import kr.weit.roadyfoody.support.utils.getWithAuth
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
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
                foodSpotsSearchService.searchFoodSpots(any(), any())
            } returns createFoodSpotsSearchResponses()
            `when`("정상적인 요청이 들어온 경우") {
                then("가게를 조회합니다.") {
                    mockMvc
                        .perform(
                            getWithAuth("$requestPath/search")
                                .contentType("application/json")
                                .param("centerLongitude", "127.074667")
                                .param("centerLatitude", "37.147030")
                                .param("radius", "500")
                                .param("name", "pot2")
                                .param("categoryIds", "1")
                                .param("categoryIds", "2"),
                        ).andExpect(status().isOk)
                        .andExpect(
                            content().json(
                                objectMapper.writeValueAsString(
                                    createFoodSpotsSearchResponses(),
                                ),
                            ),
                        )
                }
            }
        }
        given("GET getRequiredCoin API 호출") {
            every {
                foodSpotsSearchService.getRequiredCoin(any(), any())
            } returns createRequiredCoinResponse(200)

            `when`("정상적인 요청이 들어온 경우") {
                then("코인 소모량을 조회한다.") {
                    mockMvc
                        .perform(
                            getWithAuth("$requestPath/search/coin-required")
                                .contentType("application/json")
                                .param("centerLongitude", "127.074667")
                                .param("centerLatitude", "37.147030")
                                .param("radius", "1000"),
                        ).andExpect(status().isOk)
                        .andExpect(
                            content().json(
                                objectMapper.writeValueAsString(
                                    createRequiredCoinResponse(200),
                                ),
                            ),
                        )
                }
            }
        }

        given("GET getPopularSearches API 호출") {
            every {
                foodSpotsSearchService.getPopularSearches()
            } returns createFoodSpotsPopularSearchesResponse()
            `when`("정상적인 요청이 들어온 경우") {
                then("음식점 인기 검색어를 조회한다.") {
                    mockMvc
                        .perform(
                            getWithAuth("$requestPath/popular-searches")
                                .contentType("application/json"),
                        ).andExpect(status().isOk)
            }
        }
    }
    })
