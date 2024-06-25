package kr.weit.roadyfoody.search.address.application.service

import kr.weit.roadyfoody.search.address.application.dto.AddressResponseWrapper
import kr.weit.roadyfoody.search.address.application.dto.AddressSearchResponse
import kr.weit.roadyfoody.search.address.application.dto.AddressSearchResponses
import kr.weit.roadyfoody.search.address.config.KakaoProperties
import kr.weit.roadyfoody.search.address.presentation.client.KakaoAddressClientInterface
import org.springframework.stereotype.Service

const val KAKAO_AK = "KakaoAK "

@Service
class AddressSearchService(
    private val kakaoProperties: KakaoProperties,
    private val kakaoAddressClientInterface: KakaoAddressClientInterface,
) {
    fun searchAddress(
        keyword: String,
        size: Int,
    ): AddressSearchResponses {
        var encodedKeyword = keyword.replace(" ", "")

        val originalResponse = kakaoAddressClientInterface.searchAddress(KAKAO_AK + kakaoProperties.apiKey, encodedKeyword, size)

        return convertResponse(originalResponse)
    }

    private fun convertResponse(originalResponse: AddressResponseWrapper): AddressSearchResponses {
        val items =
            originalResponse.documents.map {
                AddressSearchResponse.from(it)
            }
        return AddressSearchResponses(items = items)
    }
}
