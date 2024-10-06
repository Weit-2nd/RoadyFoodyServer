package kr.weit.roadyfoody.search.foodSpots.repository

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kr.weit.roadyfoody.search.foodSpots.fixture.createFoodSpotsSearchHistory
import kr.weit.roadyfoody.search.foodSpots.fixture.createFoodSpotsSearchKeywords
import kr.weit.roadyfoody.support.annotation.ServiceIntegrateTest

// repository와 같은 기능을 하지만 JPA Repository가 아니라서 IntegrateTest로 분류
@ServiceIntegrateTest
class FoodSpotsSearchHistoryRepositoryTest(
    private val foodSpotsSearchHistoryRepository: FoodSpotsSearchHistoryRepository,
) : DescribeSpec({
        lateinit var keywordList: List<String>

        beforeSpec {
            keywordList = createFoodSpotsSearchKeywords()
            foodSpotsSearchHistoryRepository.saveAll(
                listOf(
                    createFoodSpotsSearchHistory(keywordList[0]),
                    createFoodSpotsSearchHistory(keywordList[0]),
                    createFoodSpotsSearchHistory(keywordList[1]),
                ),
            )
        }

        describe("getRecentPopularSearches") {
            context("최근 인기 검색어를 조회하는 경우") {
                it("24시간동안 검색된 인기 검색어를 반환한다.") {
                    val result = foodSpotsSearchHistoryRepository.getRecentPopularSearches()
                    result shouldBe keywordList
                }
            }
        }
    })
