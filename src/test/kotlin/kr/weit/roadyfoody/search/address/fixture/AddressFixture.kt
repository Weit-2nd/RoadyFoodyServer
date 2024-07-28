package kr.weit.roadyfoody.search.address.fixture

import kr.weit.roadyfoody.search.address.dto.AddressResponseWrapper
import kr.weit.roadyfoody.search.address.dto.AddressSearchResponse
import kr.weit.roadyfoody.search.address.dto.AddressSearchResponses
import kr.weit.roadyfoody.search.address.dto.Point2AddressWrapper
import kr.weit.roadyfoody.search.address.dto.RoadAddressResponse
import org.springframework.core.io.ClassPathResource
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper

object AddressFixture {
    private val objectMapper = ObjectMapper()

    private const val ADDRESS_RESPONSE_SIZE_10 = "payload/address-search-response.json"
    private const val ADDRESS_RESPONSE_SIZE_0 = "payload/address-search-response-size-0.json"
    private const val POINT_TO_ADDRESS_RESPONSE = "payload/point-to-address-response.json"

    private fun loadAddressJson(resourcePath: String): AddressResponseWrapper {
        val resource = ClassPathResource(resourcePath)
        return objectMapper.readValue(resource.inputStream, AddressResponseWrapper::class.java)
    }

    private fun loadPointToAddressJson(resourcePath: String): Point2AddressWrapper {
        val resource = ClassPathResource(resourcePath)
        return objectMapper.readValue(resource.inputStream, Point2AddressWrapper::class.java)
    }

    fun loadAddressResponseSize10(): AddressResponseWrapper = loadAddressJson(ADDRESS_RESPONSE_SIZE_10)

    fun loadAddressResponseSize0(): AddressResponseWrapper = loadAddressJson(ADDRESS_RESPONSE_SIZE_0)

    fun loadPoint2AddressResponse(): Point2AddressWrapper = loadPointToAddressJson(POINT_TO_ADDRESS_RESPONSE)

    fun createSearchResponses(): AddressSearchResponses =
        AddressSearchResponses(
            listOf(
                AddressSearchResponse(
                    addressName = "addressName",
                    roadAddressName = "roadAddressName",
                    longitude = 1.0,
                    latitude = 1.0,
                    tel = "tel",
                    placeName = "주소1",
                ),
                AddressSearchResponse(
                    addressName = "addressName",
                    roadAddressName = "roadAddressName",
                    longitude = 1.0,
                    latitude = 1.0,
                    tel = "tel",
                    placeName = "주소2",
                ),
            ),
        )

    fun createRoadAddressResponse(): RoadAddressResponse =
        RoadAddressResponse(
            addressName = "경기도 안성시 죽산면 죽산초교길 69-4",
            region1DepthName = "경기",
            region2DepthName = "안성시",
            region3DepthName = "죽산면",
            roadName = "죽산초교길",
            undergroundYn = "N",
            mainBuildingNo = "69",
            subBuildingNo = "4",
            buildingName = "무지개아파트",
            zoneNo = "17519",
        )
}
