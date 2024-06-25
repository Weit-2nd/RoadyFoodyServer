package kr.weit.roadyfoody.search.address.fixture

import kr.weit.roadyfoody.search.address.application.dto.AddressResponseWrapper
import kr.weit.roadyfoody.search.address.application.dto.AddressSearchResponse
import kr.weit.roadyfoody.search.address.application.dto.AddressSearchResponses
import org.springframework.core.io.ClassPathResource
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper

object AddressFixture {
    private val objectMapper = ObjectMapper()

    private const val ADDRESS_RESPONSE_SIZE_10 = "payload/address-search-response.json"
    private const val ADDRESS_RESPONSE_SIZE_0 = "payload/address-search-response-size-0.json"

    private fun loadResponse(resourcePath: String): AddressResponseWrapper {
        val resource = ClassPathResource(resourcePath)
        return objectMapper.readValue(resource.inputStream, AddressResponseWrapper::class.java)
    }

    fun loadAddressResponseSize10(): AddressResponseWrapper {
        return loadResponse(ADDRESS_RESPONSE_SIZE_10)
    }

    fun loadAddressResponseSize0(): AddressResponseWrapper {
        return loadResponse(ADDRESS_RESPONSE_SIZE_0)
    }

    fun createSearchResponses(): AddressSearchResponses {
        return AddressSearchResponses(
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
    }
}
