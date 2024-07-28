package kr.weit.roadyfoody.search.address.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "도로명 주소 검색 결과")
data class RoadAddressResponse(
    @Schema(description = "전체 도로명 주소", example = "경기도 안성시 죽산면 죽산초교길 69-4")
    val addressName: String,
    @Schema(description = "광역시/도", example = "경기")
    val region1DepthName: String,
    @Schema(description = "시/군/구", example = "안성시")
    val region2DepthName: String,
    @Schema(description = "읍/면/동", example = "죽산면")
    val region3DepthName: String,
    @Schema(description = "도로명", example = "죽산초교길")
    val roadName: String,
    @Schema(description = "지하 여부", example = "N")
    val undergroundYn: String,
    @Schema(description = "건물 본 번호", example = "69")
    val mainBuildingNo: String,
    @Schema(description = "건물 부 번호", example = "4")
    val subBuildingNo: String,
    @Schema(description = "건물명", example = "무지개아파트")
    val buildingName: String,
    @Schema(description = "우편번호", example = "17519")
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
