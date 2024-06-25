package kr.weit.roadyfoody.search.address.application.service

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kr.weit.roadyfoody.global.KEYWORD
import kr.weit.roadyfoody.global.TEN
import kr.weit.roadyfoody.search.address.config.KakaoProperties
import kr.weit.roadyfoody.search.address.fixture.AddressFixture
import kr.weit.roadyfoody.search.address.presentation.client.KakaoAddressClientInterface

class AddressSearchServiceTest :
    BehaviorSpec({
        val kakaoProperties = KakaoProperties("apiKey")
        val kakaoAddressClientInterface = mockk<KakaoAddressClientInterface>()

        val addressService = AddressSearchService(kakaoProperties, kakaoAddressClientInterface)

        given("searchAddress 테스트") {
            `when`("정상적으로 주소 검색이 가능한 경우") {
                val addressResponseWrapper = AddressFixture.loadAddressResponseSize10()

                every {
                    kakaoAddressClientInterface.searchAddress(
                        KEYWORD,
                        TEN,
                    )
                } returns addressResponseWrapper

                val addressResponses = addressService.searchAddress(KEYWORD, TEN)

                then("주소 검색을 반환한다.") {
                    addressResponses.items.size shouldBe TEN
                    val expectedPlaceName =
                        listOf(
                            "주소0",
                            "주소1",
                            "주소2",
                            "주소3",
                            "주소4",
                            "주소5",
                            "주소6",
                            "주소7",
                            "주소8",
                            "주소9",
                        )

                    for ((index, placeName)in expectedPlaceName.withIndex()) {
                        addressResponses.items[index].placeName shouldBe placeName
                    }
                }
            }

            `when`("주소 검색 결과가 없는 경우") {
                val addressResponseWrapper = AddressFixture.loadAddressResponseSize0()

                every {
                    kakaoAddressClientInterface.searchAddress(
                        KEYWORD,
                        TEN,
                    )
                } returns addressResponseWrapper

                val addressResponses = addressService.searchAddress(KEYWORD, TEN)

                then("빈 리스트를 반환한다.") {
                    addressResponses.items.size shouldBe 0
                }
            }
        }
    })
