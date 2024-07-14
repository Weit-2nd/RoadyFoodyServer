package kr.weit.roadyfoody.foodSpots.repository

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.querymodel.jpql.select.SelectQuery
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import kr.weit.roadyfoody.common.domain.query.SearchDsl
import kr.weit.roadyfoody.foodSpots.domain.FoodCategory
import kr.weit.roadyfoody.foodSpots.domain.FoodSpots
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsFoodCategory
import kr.weit.roadyfoody.global.utils.findList
import kr.weit.roadyfoody.review.exception.FoodSpotsNotFoundException
import org.springframework.data.jpa.repository.JpaRepository

fun FoodSpotsRepository.getByFoodSpotsId(foodSpotsId: Long): FoodSpots = findById(foodSpotsId).orElseThrow { FoodSpotsNotFoundException() }

interface FoodSpotsRepository :
    JpaRepository<FoodSpots, Long>,
    CustomFoodSpotsRepository

interface CustomFoodSpotsRepository {
    fun findFoodSpotsByPointWithinRadius(
        centerLongitude: Double,
        centerLatitude: Double,
        radius: Int,
        name: String?,
        categoryIds: List<Long>,
    ): List<FoodSpots>
}

class CustomFoodSpotsRepositoryImpl(
    private val executor: KotlinJdslJpqlExecutor,
) : CustomFoodSpotsRepository {
    // 카테고리 //가게이름
    override fun findFoodSpotsByPointWithinRadius(
        centerLongitude: Double,
        centerLatitude: Double,
        radius: Int,
        name: String?,
        categoryIds: List<Long>,
    ): List<FoodSpots> {
        val filteringFoodSpotIds: List<Long> = filteringByCategoryIds(categoryIds)

        val targetQuery: SelectQuery<FoodSpots> =
            jpql(SearchDsl) {
                select(entity(FoodSpots::class))
                    .from(entity(FoodSpots::class))
                    .whereAnd(
                        withinDistance(radius, centerLongitude, centerLatitude),
                        filteringFoodSpotIds.takeIf { categoryIds.isNotEmpty() }?.let {
                            entity(FoodSpots::class).foodSpotIdIn(it)
                        },
                        name.takeIf { !it.isNullOrBlank() }?.let {
                            containsName(it).greaterThan(0)
                        },
                    ).orderBy(getDistance(centerLongitude, centerLatitude).asc())
            }
        return executor.findList {
            targetQuery
        }
    }

    private fun filteringByCategoryIds(categoryIds: List<Long>): List<Long> {
        val query: SelectQuery<Long> =
            jpql(SearchDsl) {
                val baseQuery =
                    selectDistinctNew<Long>(
                        path(FoodSpotsFoodCategory::foodSpots)(FoodSpots::id),
                    ).from(
                        entity(FoodSpotsFoodCategory::class),
                    )

                if (categoryIds.isNotEmpty()) {
                    baseQuery
                        .where(
                            entity(FoodSpotsFoodCategory::class).foodCategoryIn(categoryIds),
                        ).groupBy(
                            path(FoodSpotsFoodCategory::foodSpots)(FoodSpots::id),
                        ).having(
                            count(path(FoodSpotsFoodCategory::foodCategory)(FoodCategory::id))
                                .greaterThanOrEqualTo(categoryIds.size.toLong()),
                        )
                } else {
                    baseQuery
                }.orderBy(
                    path(FoodSpotsFoodCategory::foodSpots)(FoodSpots::id).desc(),
                )
            }

        return executor.findList {
            query
        }
    }
}
