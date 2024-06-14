package kr.weit.roadyfoody.tourism.dto

import com.fasterxml.jackson.databind.annotation.JsonDeserialize

data class ResponseWrapper(
    val response: Response,
)

data class Response(
    val header: Header,
    val body: Body,
)

data class Header(
    val resultCode: String,
    val resultMsg: String,
)

data class Body(
    @JsonDeserialize(using = ItemsDeserializer::class)
    val items: Items?,
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int,
)

data class Items(
    val item: List<TourismItem> = emptyList(),
)

data class TourismItem(
    val addr1: String,
    val addr2: String,
    val areacode: String,
    val booktour: String,
    val cat1: String,
    val cat2: String,
    val cat3: String,
    val contentid: String,
    val contenttypeid: String,
    val createdtime: String,
    val firstimage: String,
    val firstimage2: String,
    val cpyrhtDivCd: String,
    val mapx: String,
    val mapy: String,
    val mlevel: String,
    val modifiedtime: String,
    val sigungucode: String,
    val tel: String,
    val title: String,
)
