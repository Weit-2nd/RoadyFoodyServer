package kr.weit.roadyfoody.search.address.application.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kr.weit.roadyfoody.common.exception.ErrorCode
import kr.weit.roadyfoody.common.exception.RoadyFoodyBadRequestException
import kr.weit.roadyfoody.global.TEST_KEYWORD
import kr.weit.roadyfoody.global.TEST_PAGE_SIZE
import kr.weit.roadyfoody.search.address.config.KakaoProperties
import kr.weit.roadyfoody.search.address.fixture.AddressFixture
import kr.weit.roadyfoody.search.address.presentation.client.KakaoAddressClientInterface
import kr.weit.roadyfoody.search.address.presentation.client.KakaoPointClientInterface

class AddressSearchServiceTest :
    BehaviorSpec({
        val kakaoProperties = KakaoProperties("apiKey")
        val kakaoAddressClientInterface = mockk<KakaoAddressClientInterface>()
        val kakaoPointClientInterface = mockk<KakaoPointClientInterface>()
        val addressService = AddressSearchService(kakaoProperties, kakaoAddressClientInterface, kakaoPointClientInterface)

        given("searchAddress 테스트") {
            `when`("정상적으로 주소 검색이 가능한 경우") {
                val addressResponseWrapper = AddressFixture.loadAddressResponseSize10()

                every {
                    kakaoAddressClientInterface.searchAddress(
                        TEST_KEYWORD,
                        TEST_PAGE_SIZE,
                    )
                } returns addressResponseWrapper

                val addressResponses = addressService.searchAddress(TEST_KEYWORD, TEST_PAGE_SIZE)

                then("주소 검색을 반환한다.") {
                    addressResponses.items.shouldHaveSize(TEST_PAGE_SIZE)
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
                        TEST_KEYWORD,
                        TEST_PAGE_SIZE,
                    )
                } returns addressResponseWrapper

                val addressResponses = addressService.searchAddress(TEST_KEYWORD, TEST_PAGE_SIZE)

                then("빈 리스트를 반환한다.") {
                    addressResponses.items.shouldBeEmpty()
                }
            }
        }

        given("searchPoint2Address 테스트") {
            `when`("정상적으로 좌표를 주소로 변환할 수 있는 경우") {
                val point2AddressWrapper = AddressFixture.loadPoint2AddressResponse()

                every {
                    kakaoPointClientInterface.searchPointToAddress(
                        "127.0",
                        "37.0",
                    )
                } returns point2AddressWrapper

                val result = addressService.searchPoint2Address(127.0, 37.0)

                then("주소를 반환한다.") {
                    result.roadAddressName shouldBe "경기도 안성시 죽산면 죽산초교길 69-4"
                    result.addressName shouldBe "경기 안성시 죽산면 죽산리 343-1"
                }
            }

            `when`("주소를 찾을 수 없는 경우") {
                every {
                    kakaoPointClientInterface.searchPointToAddress(
                        "127.0",
                        "37.0",
                    )
                } returns AddressFixture.loadPoint2AddressResponse().copy(documents = emptyList())

                then("예외를 반환한다.") {
                    val exception =
                        shouldThrow<RoadyFoodyBadRequestException> {
                            addressService.searchPoint2Address(127.0, 37.0)
                        }
                    exception.errorCode shouldBe ErrorCode.INVALID_POINT_TO_ADDRESS
                }
            }
        }
    })
