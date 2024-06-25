package kr.weit.roadyfoody.search.tourism.application.service

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kr.weit.roadyfoody.search.tourism.config.TourismProperties
import kr.weit.roadyfoody.search.tourism.fixture.TourismFixture
import kr.weit.roadyfoody.search.tourism.presentation.client.TourismClientInterface
import java.util.concurrent.ExecutorService

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
        val executor = mockk<ExecutorService>()

        val tourismService = TourismService(tourismProperties, tourismClientInterface, executor)

        given("searchTourism 테스트") {
            every { executor.execute(any()) } answers {
                firstArg<Runnable>().run()
            }
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
                        TOUR_CONTENT_ID,
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
                            "관광지 0",
                            "행사 0",
                            "관광지 1",
                            "행사 1",
                            "관광지 2",
                            "행사 2",
                            "관광지 3",
                            "행사 3",
                            "관광지 4",
                            "행사 4",
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
                        TOUR_CONTENT_ID,
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

                then("관광지와 행사의 검색 결과를 그대로 반환한다.") {
                    searchResponses.items.size shouldBe FOUR
                    val expectedTitles =
                        listOf(
                            "관광지 0",
                            "행사 0",
                            "관광지 1",
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
                        TOUR_CONTENT_ID,
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
                    searchResponses.items.shouldBeEmpty()
                }
            }
        }
    })
