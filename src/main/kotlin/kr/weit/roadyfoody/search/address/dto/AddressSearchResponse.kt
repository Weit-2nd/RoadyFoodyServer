package kr.weit.roadyfoody.search.address.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "주소 검색 결과 항목")
data class AddressSearchResponse(
    @Schema(description = "장소명", example = "명륜진사갈비 본사")
    val placeName: String,
    @Schema(description = "주소", example = "서울 송파구 가락동 113")
    val addressName: String,
    @Schema(description = "도로명 주소", example = "서울 송파구 중대로12길 6")
    val roadAddressName: String,
    @Schema(description = "경도", example = "127.12312219099")
    val longitude: Double,
    @Schema(description = "위도", example = "37.4940529587731")
    val latitude: Double,
    @Schema(description = "전화번호", example = "1566-3607", nullable = true)
    val tel: String?,
) {
    companion object {
        fun from(document: Document): AddressSearchResponse =
            AddressSearchResponse(
                placeName = document.placeName,
                addressName = document.addressName,
                roadAddressName = document.roadAddressName,
                longitude = document.x.toDouble(),
                latitude = document.y.toDouble(),
                tel = document.phone,
            )
    }
}

@Schema(description = "주소 검색 결과 응답")
data class AddressSearchResponses(
    @Schema(description = "주소 검색 결과 항목 리스트")
    val items: List<AddressSearchResponse>,
)
