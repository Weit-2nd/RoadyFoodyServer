package kr.weit.roadyfoody.tourism.application.service

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kr.weit.roadyfoody.tourism.application.util.MockRandomNumberGenerator
import kr.weit.roadyfoody.tourism.config.TourismProperties
import kr.weit.roadyfoody.tourism.fixture.TourismFixture
import kr.weit.roadyfoody.tourism.presentation.client.TourismClientInterface

private const val TOURISM_CONTENT_ID = 12

private const val FESTIVAL_CONTENT_ID = 15

private const val TEN = 10

private const val ZERO = 0

private const val FOUR = 4

class TourismServiceTest :
    BehaviorSpec({
        val tourismProperties =
            TourismProperties(
                apiKey = "apiKey",
                mobileApp = "mobileApp",
                mobileOs = "mobileOs",
            )

        val tourismClientInterface = mockk<TourismClientInterface>()
        val mockRandomNumberGenerator =
            MockRandomNumberGenerator(
                setOf(
                    1,
                    3,
                    4,
                    5,
                    7,
                    10,
                    13,
                    15,
                    17,
                    18,
                ),
            )
        val tourismService = TourismService(tourismProperties, tourismClientInterface, mockRandomNumberGenerator)

        given("searchTourism 테스트") {
            `when`("관광지와 행사의 검색 결과가 10을 넘는 경우") {

                val tourResponseWrapper = TourismFixture.loadTourResponseSize10()
                val festivalResponseWrapper = TourismFixture.loadFestivalResponseSize9()

                every {
                    tourismClientInterface.searchTourismKeyword(
                        tourismProperties.apiKey,
                        tourismProperties.mobileApp,
                        tourismProperties.mobileOs,
                        TEN,
                        "keyword",
                        TOURISM_CONTENT_ID,
                    )
                } returns tourResponseWrapper

                every {
                    tourismClientInterface.searchTourismKeyword(
                        tourismProperties.apiKey,
                        tourismProperties.mobileApp,
                        tourismProperties.mobileOs,
                        TEN,
                        "keyword",
                        FESTIVAL_CONTENT_ID,
                    )
                } returns festivalResponseWrapper

                val searchResponses = tourismService.searchTourism(TEN, "keyword")

                then("관광지와 행사의 검색 결과를 랜덤하게 반환한다.") {
                    searchResponses.items.size shouldBe TEN
                    val expectedTitles =
                        listOf(
                            "관광지 1",
                            "관광지 3",
                            "관광지 4",
                            "관광지 5",
                            "관광지 7",
                            "관광지 10",
                            "관광지 13",
                            "관광지 15",
                            "관광지 17",
                            "관광지 18",
                        )

                    for ((index, title) in expectedTitles.withIndex()) {
                        searchResponses.items[index].title shouldBe title
                    }
                }
            }
            `when`("관광지와 행사의 검색 결과가 10 이하인 경우") {
                val tourResponseWrapper = TourismFixture.loadTourResponseSize2()
                val festivalResponseWrapper = TourismFixture.loadFestivalResponseSize2()

                every {
                    tourismClientInterface.searchTourismKeyword(
                        tourismProperties.apiKey,
                        tourismProperties.mobileApp,
                        tourismProperties.mobileOs,
                        TEN,
                        "keyword",
                        TOURISM_CONTENT_ID,
                    )
                } returns tourResponseWrapper

                every {
                    tourismClientInterface.searchTourismKeyword(
                        tourismProperties.apiKey,
                        tourismProperties.mobileApp,
                        tourismProperties.mobileOs,
                        TEN,
                        "keyword",
                        FESTIVAL_CONTENT_ID,
                    )
                } returns festivalResponseWrapper

                val searchResponses = tourismService.searchTourism(TEN, "keyword")

                then("관광지와 행사의 검색 결과를 랜덤하게 반환한다.") {
                    searchResponses.items.size shouldBe FOUR
                    val expectedTitles =
                        listOf(
                            "관광지 0",
                            "관광지 1",
                            "행사 0",
                            "행사 1",
                        )

                    for ((index, title) in expectedTitles.withIndex()) {
                        searchResponses.items[index].title shouldBe title
                    }
                }
            }
            `when`("관광지와 행사의 검색 결과가 없는 경우") {
                val tourResponseWrapper = TourismFixture.loadTourResponseSize0()
                val festivalResponseWrapper = TourismFixture.loadFestivalResponseSize0()

                every {
                    tourismClientInterface.searchTourismKeyword(
                        tourismProperties.apiKey,
                        tourismProperties.mobileApp,
                        tourismProperties.mobileOs,
                        TEN,
                        "keyword",
                        TOURISM_CONTENT_ID,
                    )
                } returns tourResponseWrapper

                every {
                    tourismClientInterface.searchTourismKeyword(
                        tourismProperties.apiKey,
                        tourismProperties.mobileApp,
                        tourismProperties.mobileOs,
                        TEN,
                        "keyword",
                        FESTIVAL_CONTENT_ID,
                    )
                } returns festivalResponseWrapper

                val searchResponses = tourismService.searchTourism(TEN, "keyword")

                then("빈 리스트를 반환한다.") {
                    searchResponses.items.size shouldBe ZERO
                }
            }
        }
    })
