package kr.weit.roadyfoody.foodSpots.presentation.api

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import kr.weit.roadyfoody.foodSpots.application.service.FoodCategoriesQueryService
import kr.weit.roadyfoody.foodSpots.fixture.createFoodCategoryResponse
import kr.weit.roadyfoody.support.annotation.ControllerTest
import kr.weit.roadyfoody.support.utils.getWithAuth
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(FoodCategoriesController::class)
@ControllerTest
class FoodCategoriesControllerTest(
    @MockkBean private val foodCategoriesQueryService: FoodCategoriesQueryService,
    mockMvc: MockMvc,
) : BehaviorSpec(
        {
            val requestPath = "/api/v1/food-categories"

            given("GET $requestPath Test") {
                val response = createFoodCategoryResponse()
                every {
                    foodCategoriesQueryService.getCategories()
                } returns listOf(response)
                `when`("정상적인 요청이 들어올 경우") {
                    then("카테고리 리스트를 반환한다.") {
                        mockMvc
                            .perform(
                                getWithAuth(requestPath),
                            ).andExpect(status().isOk)
                    }
                }
            }
        },
    )
