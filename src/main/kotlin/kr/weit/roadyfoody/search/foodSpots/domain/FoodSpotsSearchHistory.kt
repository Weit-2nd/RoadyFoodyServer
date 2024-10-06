package kr.weit.roadyfoody.search.foodSpots.domain

import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.util.Date

@Document(indexName = "#{@environment.getProperty('open-search.indices.food-spots-search-history')}")
data class FoodSpotsSearchHistory(
    @Id
    @GeneratedValue
    val id: String? = null,
    @Field(name = "keyword", type = FieldType.Keyword)
    val keyword: String,
    @Field(name = "@timestamp", type = FieldType.Date)
    val createdDateTime: Date = Date(),
)
