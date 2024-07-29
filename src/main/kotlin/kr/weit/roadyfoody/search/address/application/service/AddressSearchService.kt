package kr.weit.roadyfoody.search.address.application.service

import kr.weit.roadyfoody.common.exception.ErrorCode
import kr.weit.roadyfoody.common.exception.RoadyFoodyBadRequestException
import kr.weit.roadyfoody.search.address.config.KakaoProperties
import kr.weit.roadyfoody.search.address.dto.AddressResponseWrapper
import kr.weit.roadyfoody.search.address.dto.AddressSearchResponse
import kr.weit.roadyfoody.search.address.dto.AddressSearchResponses
import kr.weit.roadyfoody.search.address.dto.Point2AddressResponse
import kr.weit.roadyfoody.search.address.presentation.client.KakaoAddressClientInterface
import kr.weit.roadyfoody.search.address.presentation.client.KakaoPointClientInterface
import org.springframework.stereotype.Service
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

const val KAKAO_AK = "KakaoAK "

@Service
class AddressSearchService(
    private val kakaoProperties: KakaoProperties,
    private val kakaoAddressClientInterface: KakaoAddressClientInterface,
    private val kakaoPointClientInterface: KakaoPointClientInterface,
) {
    fun searchAddress(
        keyword: String,
        size: Int,
    ): AddressSearchResponses {
        val encodedKeyword: String = URLEncoder.encode(keyword.trim(), StandardCharsets.UTF_8)
        val originalResponse = kakaoAddressClientInterface.searchAddress(encodedKeyword, size)
        return convertResponse(originalResponse)
    }

    private fun convertResponse(originalResponse: AddressResponseWrapper): AddressSearchResponses {
        val items =
            originalResponse.documents.map {
                AddressSearchResponse.from(it)
            }
        return AddressSearchResponses(items = items)
    }

    fun searchPoint2Address(
        longitude: String,
        latitude: String,
    ): Point2AddressResponse {
        val originalResponse = kakaoPointClientInterface.searchPointToAddress(longitude, latitude)
        if (originalResponse.documents.isEmpty()) {
            throw RoadyFoodyBadRequestException(ErrorCode.INVALID_POINT_TO_ADDRESS)
        }
        return Point2AddressResponse.from(originalResponse.documents[0], latitude, longitude)
    }
}
