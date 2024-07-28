package kr.weit.roadyfoody.search.address.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "도로명 주소 검색 결과")
data class RoadAddressResponse(
    val addressName: String,
    val region1DepthName: String,
    val region2DepthName: String,
    val region3DepthName: String,
    val roadName: String,
    val undergroundYn: String,
    val mainBuildingNo: String,
    val subBuildingNo: String,
    val buildingName: String,
    val zoneNo: String,
) {
    companion object {
        fun from(document: Point2AddressData): RoadAddressResponse =
            RoadAddressResponse(
                addressName = document.roadAddress.addressName,
                region1DepthName = document.roadAddress.region1DepthName,
                region2DepthName = document.roadAddress.region2DepthName,
                region3DepthName = document.roadAddress.region3DepthName,
                roadName = document.roadAddress.roadName,
                undergroundYn = document.roadAddress.undergroundYn,
                mainBuildingNo = document.roadAddress.mainBuildingNo,
                subBuildingNo = document.roadAddress.subBuildingNo,
                buildingName = document.roadAddress.buildingName,
                zoneNo = document.roadAddress.zoneNo,
            )
    }
}
