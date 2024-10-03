package kr.weit.roadyfoody.search.foodSpots.repository

import kr.weit.roadyfoody.search.foodSpots.domain.FoodSpotsSearchHistory
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder
import org.opensearch.data.client.orhlc.OpenSearchAggregations
import org.opensearch.index.query.BoolQueryBuilder
import org.opensearch.index.query.QueryBuilders.boolQuery
import org.opensearch.index.query.QueryBuilders.rangeQuery
import org.opensearch.search.aggregations.AggregationBuilders
import org.opensearch.search.aggregations.bucket.terms.ParsedStringTerms
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.time.ZoneId

@Repository
interface FoodSpotsSearchHistoryRepository :
    ElasticsearchRepository<FoodSpotsSearchHistory, Long>,
    CustomFoodSpotsSearchHistoryRepository

interface CustomFoodSpotsSearchHistoryRepository {
    fun getRecentPopularSearches(): List<String>
}

class CustomFoodSpotsSearchHistoryRepositoryImpl(
    private val elasticsearchOperations: ElasticsearchOperations,
) : CustomFoodSpotsSearchHistoryRepository {
    private val top10 = 10

    override fun getRecentPopularSearches(): List<String> {
        val aggregation =
            AggregationBuilders.terms("GroupByFoodSpotsKeyword").field("keyword").size(top10)
        val query =
            NativeSearchQueryBuilder()
                .withQuery(
                    getSearchCondition(),
                ).withAggregations(aggregation)
                .build()

        val searchResult =
            (
                elasticsearchOperations
                    .search(
                        query,
                        FoodSpotsSearchHistory::class.java,
                    ).aggregations as OpenSearchAggregations
            ).aggregations()
                .get<ParsedStringTerms>("GroupByFoodSpotsKeyword")
        return searchResult.buckets.map { it.key as String }
    }

    private fun getSearchCondition(): BoolQueryBuilder {
        val now =
            LocalDateTime
                .now()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        val oneHoursAgo =
            LocalDateTime
                .now()
                .minusHours(1)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

        return boolQuery()
            .must(
                rangeQuery("@timestamp")
                    .gte(oneHoursAgo)
                    .lte(now),
            )
    }
}
