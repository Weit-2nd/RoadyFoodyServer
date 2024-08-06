package kr.weit.roadyfoody.search.address.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class Point2AddressWrapper(
    @JsonProperty("documents") val documents: List<Point2AddressData>,
    @JsonProperty("meta") val meta: Total,
)

data class RoadAddress(
    @JsonProperty("address_name") val addressName: String,
    @JsonProperty("region_1depth_name") val region1DepthName: String,
    @JsonProperty("region_2depth_name") val region2DepthName: String,
    @JsonProperty("region_3depth_name") val region3DepthName: String,
    @JsonProperty("road_name") val roadName: String,
    @JsonProperty("underground_yn") val undergroundYn: String,
    @JsonProperty("main_building_no") val mainBuildingNo: String,
    @JsonProperty("sub_building_no") val subBuildingNo: String,
    @JsonProperty("building_name") val buildingName: String,
    @JsonProperty("zone_no") val zoneNo: String,
)

data class Address(
    @JsonProperty("address_name") val addressName: String,
    @JsonProperty("region_1depth_name") val region1DepthName: String,
    @JsonProperty("region_2depth_name") val region2DepthName: String,
    @JsonProperty("region_3depth_name") val region3DepthName: String,
    @JsonProperty("mountain_yn") val mountainYn: String,
    @JsonProperty("main_address_no") val mainAddressNo: String,
    @JsonProperty("sub_address_no") val subAddressNo: String,
)

data class Point2AddressData(
    @JsonProperty("road_address") val roadAddress: RoadAddress?,
    @JsonProperty("address") val address: Address,
)

data class Total(
    @JsonProperty("total_count") val totalCount: Int,
)
