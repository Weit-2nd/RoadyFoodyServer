package kr.weit.roadyfoody.tourism.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

data class ResponseWrapper(
    @JsonProperty("response") val response: Response,
)

data class Response(
    @JsonProperty("header") val header: Header,
    @JsonProperty("body") val body: Body,
)

data class Header(
    @JsonProperty("resultCode") val resultCode: String,
    @JsonProperty("resultMsg") val resultMsg: String,
)

data class Body(
    @JsonProperty("items") @JsonDeserialize(using = ItemsDeserializer::class) val items: Items,
    @JsonProperty("numOfRows") val numOfRows: Int,
    @JsonProperty("pageNo") val pageNo: Int,
    @JsonProperty("totalCount") val totalCount: Int,
)

data class Items(
    @JsonProperty("item") val item: List<TourismItem> = emptyList(),
)

data class TourismItem(
    @JsonProperty("addr1") val addr1: String,
    @JsonProperty("addr2") val addr2: String,
    @JsonProperty("areacode") val areacode: String,
    @JsonProperty("booktour") val booktour: String,
    @JsonProperty("cat1") val cat1: String,
    @JsonProperty("cat2") val cat2: String,
    @JsonProperty("cat3") val cat3: String,
    @JsonProperty("contentid") val contentid: String,
    @JsonProperty("contenttypeid") val contenttypeid: String,
    @JsonProperty("createdtime") val createdtime: String,
    @JsonProperty("firstimage") val firstimage: String,
    @JsonProperty("firstimage2") val firstimage2: String,
    @JsonProperty("cpyrhtDivCd") val cpyrhtDivCd: String,
    @JsonProperty("mapx") val mapx: Double,
    @JsonProperty("mapy") val mapy: Double,
    @JsonProperty("mlevel") val mlevel: String,
    @JsonProperty("modifiedtime") val modifiedtime: String,
    @JsonProperty("sigungucode") val sigungucode: String,
    @JsonProperty("tel") val tel: String,
    @JsonProperty("title") val title: String,
)
