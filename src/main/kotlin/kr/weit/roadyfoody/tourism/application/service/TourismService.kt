package kr.weit.roadyfoody.tourism.application.service

import kr.weit.roadyfoody.tourism.config.TourismProperties
import kr.weit.roadyfoody.tourism.dto.ResponseWrapper
import kr.weit.roadyfoody.tourism.presentation.client.TourismClientInterface
import org.springframework.stereotype.Service
import org.yaml.snakeyaml.util.UriEncoder

@Service
class TourismService(
    private val tourismProperties: TourismProperties,
    private val tourismClientInterface: TourismClientInterface,
) {
    fun searchTourism(
        pageNo: Int,
        numOfRows: Int,
        keyword: String,
    ): ResponseWrapper {
        val encodedKeyword: String = UriEncoder.encode(keyword)
        val encodedServiceKey: String = UriEncoder.encode(tourismProperties.apiKey)
        return tourismClientInterface.searchTourismKeyword(
            encodedServiceKey,
            tourismProperties.mobileApp,
            tourismProperties.mobileOs,
            pageNo,
            numOfRows,
            encodedKeyword,
        )
    }
}
