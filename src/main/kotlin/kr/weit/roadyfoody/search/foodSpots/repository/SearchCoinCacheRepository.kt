package kr.weit.roadyfoody.search.foodSpots.repository

import kr.weit.roadyfoody.search.foodSpots.domain.SearchCoinCache
import org.springframework.data.repository.CrudRepository

interface SearchCoinCacheRepository : CrudRepository<SearchCoinCache, String> {
    fun findByUserId(userId: Long): List<SearchCoinCache>
}
