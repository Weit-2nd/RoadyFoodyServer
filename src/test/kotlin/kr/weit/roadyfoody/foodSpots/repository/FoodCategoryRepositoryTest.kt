package kr.weit.roadyfoody.foodSpots.repository

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kr.weit.roadyfoody.foodSpots.domain.FoodCategory
import kr.weit.roadyfoody.foodSpots.exception.CategoriesNotFoundException
import kr.weit.roadyfoody.foodSpots.fixture.TEST_INVALID_FOOD_CATEGORY_ID
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodCategories
import kr.weit.roadyfoody.support.annotation.RepositoryTest

@RepositoryTest
class FoodCategoryRepositoryTest(
    private val foodCategoryRepository: FoodCategoryRepository,
) : DescribeSpec({

        lateinit var givenCategories: List<FoodCategory>

        beforeEach {
            givenCategories =
                foodCategoryRepository.saveAll(
                    createTestFoodCategories(),
                )
        }

        describe("getCategories 메소드는") {
            context("존재하는 id를 받는 경우") {
                it("해당 id에 해당되는 카테고리들을 반환") {
                    val categories = foodCategoryRepository.getFoodCategories(setOf(givenCategories[0].id, givenCategories[1].id))
                    categories shouldBe listOf(givenCategories[0], givenCategories[1])
                }
            }

            context("존재하지 않는 카테고리 id가 1개라도 있는 경우") {
                it("CategoriesNotFoundException 예외를 던진다") {
                    shouldThrow<CategoriesNotFoundException> {
                        foodCategoryRepository.getFoodCategories(
                            setOf(TEST_INVALID_FOOD_CATEGORY_ID),
                        )
                    }
                }
            }
        }
    })
