package kr.weit.roadyfoody.search.address.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "도로명 주소 검색 결과")
data class Point2AddressResponse(
    @Schema(description = "도로명 주소(optional)", example = "경기도 안성시 죽산면 죽산초교길 69-4")
    val roadAddressName: String?,
    @Schema(description = "지번 주소", example = "경기 안성시 죽산면 죽산리 343-1")
    val addressName: String,
    @Schema(description = "위도", example = "37.0789561558879")
    val latitude: Double,
    @Schema(description = "경도", example = "127.423084873712")
    val longitude: Double,
) {
    companion object {
        fun from(
            document: Point2AddressData,
            latitude: Double,
            longitude: Double,
        ): Point2AddressResponse =
            Point2AddressResponse(
                addressName = document.address.addressName,
                roadAddressName = document.roadAddress?.addressName,
                latitude = latitude,
                longitude = longitude,
            )
    }
}
