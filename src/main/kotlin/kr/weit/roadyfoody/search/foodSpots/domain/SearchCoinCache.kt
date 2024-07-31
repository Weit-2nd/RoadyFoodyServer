package kr.weit.roadyfoody.search.foodSpots.domain

import jakarta.persistence.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("searchCoin", timeToLive = 86400) // 86400초 = 24시간)
class SearchCoinCache(
    @Id val id: String,
    @Indexed
    val userId: Long,
    val longitude: Double,
    val latitude: Double,
    val radius: Int,
) {
    companion object {
        fun of(
            userId: Long,
            longitude: Double,
            latitude: Double,
            radius: Int,
        ): SearchCoinCache =
            SearchCoinCache(
                id = "$userId:$longitude:$latitude",
                userId = userId,
                longitude = longitude,
                latitude = latitude,
                radius = radius,
            )
    }
}
