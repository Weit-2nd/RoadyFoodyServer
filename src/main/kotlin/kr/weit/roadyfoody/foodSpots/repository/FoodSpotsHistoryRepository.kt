package kr.weit.roadyfoody.foodSpots.repository

import com.linecorp.kotlinjdsl.QueryFactory
import com.linecorp.kotlinjdsl.listQuery
import com.linecorp.kotlinjdsl.query.spec.OrderSpec
import com.linecorp.kotlinjdsl.query.spec.predicate.PredicateSpec
import com.linecorp.kotlinjdsl.querydsl.CriteriaQueryDsl
import com.linecorp.kotlinjdsl.querydsl.expression.col
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsHistory
import kr.weit.roadyfoody.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

fun FoodSpotsHistoryRepository.getFoodSpotsHistoryList(
    user: User,
    size: Int,
    sortType: FoodSpotsHistorySortType,
    lastId: Long?,
): List<FoodSpotsHistory> = findSliceByUserOrderBySortType(user, size, sortType, lastId)

@Repository
interface FoodSpotsHistoryRepository :
    JpaRepository<FoodSpotsHistory, Long>,
    CustomFoodSpotsHistoryRepository

interface CustomFoodSpotsHistoryRepository {
    fun findSliceByUserOrderBySortType(
        user: User,
        size: Int,
        sortType: FoodSpotsHistorySortType,
        lastId: Long?,
    ): List<FoodSpotsHistory>
}

class FoodSpotsHistoryRepositoryImpl(
    private val queryFactory: QueryFactory,
) : CustomFoodSpotsHistoryRepository {
    override fun findSliceByUserOrderBySortType(
        user: User,
        size: Int,
        sortType: FoodSpotsHistorySortType,
        lastId: Long?,
    ): List<FoodSpotsHistory> =
        queryFactory.listQuery {
            baseSearchQuery(size, sortType, lastId)
            where(col(FoodSpotsHistory::user).equal(user))
        }

    private fun CriteriaQueryDsl<FoodSpotsHistory>.baseSearchQuery(
        size: Int,
        sortType: FoodSpotsHistorySortType,
        lastId: Long?,
    ) {
        select(entity(FoodSpotsHistory::class))
        from(entity(FoodSpotsHistory::class))
        where(dynamicPredicateFoodSpotsHistorySortType(sortType, lastId))
        orderBy(dynamicOrderingByFoodSpotsHistorySortType(sortType))
        limit(size + 1)
    }

    private fun <T> CriteriaQueryDsl<T>.dynamicPredicateFoodSpotsHistorySortType(
        sortType: FoodSpotsHistorySortType,
        lastId: Long?,
    ): PredicateSpec =
        if (lastId != null) {
            when (sortType) {
                FoodSpotsHistorySortType.LATEST -> col(FoodSpotsHistory::id).lessThan(lastId)
            }
        } else {
            PredicateSpec.empty
        }

    private fun <T> CriteriaQueryDsl<T>.dynamicOrderingByFoodSpotsHistorySortType(sortType: FoodSpotsHistorySortType): List<OrderSpec> =
        when (sortType) {
            FoodSpotsHistorySortType.LATEST -> listOf(col(FoodSpotsHistory::id).desc())
        }
}

enum class FoodSpotsHistorySortType(
    val description: String,
) {
    LATEST("최신순"),
}
