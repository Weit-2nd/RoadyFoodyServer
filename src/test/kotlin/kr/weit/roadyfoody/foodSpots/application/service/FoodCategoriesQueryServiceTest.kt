package kr.weit.roadyfoody.foodSpots.application.service

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodCategory
import kr.weit.roadyfoody.foodSpots.repository.FoodCategoryRepository

class FoodCategoriesQueryServiceTest :
    BehaviorSpec(
        {
            val foodCategoryRepository = mockk<FoodCategoryRepository>()
            val foodCategoriesQueryService = FoodCategoriesQueryService(foodCategoryRepository)

            given("getCategories 테스트") {
                every { foodCategoryRepository.findAll() } returns listOf(createTestFoodCategory())
                `when`("정상적인 요청이 들어올 경우,") {
                    then("카테고리 리스트가 출력되어야한다") {
                        foodCategoriesQueryService.getCategories()
                    }
                }
            }
        },
    )
