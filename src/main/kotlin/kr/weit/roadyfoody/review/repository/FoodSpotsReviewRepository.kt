package kr.weit.roadyfoody.review.repository

import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicate
import com.linecorp.kotlinjdsl.querymodel.jpql.sort.Sortable
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import kr.weit.roadyfoody.badge.domain.Badge
import kr.weit.roadyfoody.foodSpots.application.dto.CountRate
import kr.weit.roadyfoody.foodSpots.application.dto.ReviewAggregatedInfoResponse
import kr.weit.roadyfoody.foodSpots.domain.FoodSpots
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsHistory
import kr.weit.roadyfoody.global.utils.findList
import kr.weit.roadyfoody.global.utils.findMutableList
import kr.weit.roadyfoody.global.utils.getSlice
import kr.weit.roadyfoody.ranking.dto.UserRanking
import kr.weit.roadyfoody.review.domain.FoodSpotsReview
import kr.weit.roadyfoody.review.domain.ReviewLike
import kr.weit.roadyfoody.review.exception.FoodSpotsReviewNotFoundException
import kr.weit.roadyfoody.user.domain.Profile
import kr.weit.roadyfoody.user.domain.User
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

fun FoodSpotsReviewRepository.getReviewByReviewId(reviewId: Long): FoodSpotsReview =
    findById(reviewId).orElseThrow {
        FoodSpotsReviewNotFoundException()
    }

interface FoodSpotsReviewRepository :
    JpaRepository<FoodSpotsReview, Long>,
    CustomFoodSpotsReviewRepository {
    fun findByUser(user: User): List<FoodSpotsReview>
}

interface CustomFoodSpotsReviewRepository {
    fun sliceByUser(
        user: User,
        size: Int,
        lastId: Long?,
    ): Slice<FoodSpotsReview>

    fun sliceByFoodSpots(
        foodSpotsId: Long,
        size: Int,
        lastId: Long?,
        sortType: ReviewSortType,
        badge: Badge? = null,
    ): Slice<FoodSpotsReview>

    fun getReviewAggregatedInfo(foodSpots: FoodSpots): ReviewAggregatedInfoResponse

    fun findAllUserReviewCount(): List<UserRanking>

    fun findAllUserLikeCount(): List<UserRanking>

    fun getRatingCount(foodSpotsId: Long): List<CountRate>

    fun findAllUserTotalCount(): List<UserRanking>
}

class CustomFoodSpotsReviewRepositoryImpl(
    private val kotlinJdslJpqlExecutor: KotlinJdslJpqlExecutor,
) : CustomFoodSpotsReviewRepository {
    override fun sliceByUser(
        user: User,
        size: Int,
        lastId: Long?,
    ): Slice<FoodSpotsReview> {
        val pageable = Pageable.ofSize(size)
        return kotlinJdslJpqlExecutor.getSlice(pageable) {
            select(entity(FoodSpotsReview::class))
                .from(entity(FoodSpotsReview::class))
                .whereAnd(
                    if (lastId != null) {
                        path(FoodSpotsReview::id).lessThan(lastId)
                    } else {
                        null
                    },
                    path(FoodSpotsReview::user).equal(user),
                ).orderBy(path(FoodSpotsReview::id).desc())
        }
    }

    override fun sliceByFoodSpots(
        foodSpotsId: Long,
        size: Int,
        lastId: Long?,
        sortType: ReviewSortType,
        badge: Badge?,
    ): Slice<FoodSpotsReview> {
        val pageable = Pageable.ofSize(size)
        return kotlinJdslJpqlExecutor.getSlice(pageable) {
            select(entity(FoodSpotsReview::class))
                .from(entity(FoodSpotsReview::class))
                .whereAnd(
                    dynamicLastId(sortType, lastId),
                    path(FoodSpotsReview::foodSpots)(FoodSpots::id).equal(foodSpotsId),
                    badge?.let { path(FoodSpotsReview::user)(User::badge).equal(badge) },
                ).orderBy(
                    *dynamicOrder(sortType),
                )
        }
    }

    override fun getReviewAggregatedInfo(foodSpots: FoodSpots): ReviewAggregatedInfoResponse =
        kotlinJdslJpqlExecutor
            .findAll {
                selectNew<ReviewAggregatedInfoResponse>(
                    avg(path(FoodSpotsReview::rate)),
                    count(path(FoodSpotsReview::id)),
                ).from(entity(FoodSpotsReview::class))
                    .whereAnd(
                        path(FoodSpotsReview::foodSpots).equal(foodSpots),
                    )
            }.first()!!

    override fun getRatingCount(foodSpotsId: Long): List<CountRate> {
        val countRates =
            kotlinJdslJpqlExecutor
                .findMutableList {
                    val ratePath = path(FoodSpotsReview::rate)
                    selectNew<CountRate>(
                        ratePath,
                        count(ratePath),
                    ).from(entity(FoodSpotsReview::class))
                        .whereAnd(
                            path(FoodSpotsReview::foodSpots)(FoodSpots::id).equal(foodSpotsId),
                        ).groupBy(ratePath)
                        .orderBy(ratePath.desc())
                }

        return fillMissingRatings(countRates)
    }

    override fun findAllUserReviewCount(): List<UserRanking> =
        kotlinJdslJpqlExecutor
            .findList {
                val userIdPath = path(FoodSpotsReview::user).path(User::id)
                val userNicknamePath = path(FoodSpotsReview::user).path(User::profile).path(Profile::nickname)
                val reviewIdPath = path(FoodSpotsReview::id)
                val createdAtPath = path(FoodSpotsReview::createdDateTime)

                selectNew<UserRanking>(
                    userNicknamePath,
                    count(reviewIdPath),
                ).from(entity(FoodSpotsReview::class))
                    .groupBy(userIdPath, userNicknamePath)
                    .orderBy(
                        count(reviewIdPath).desc(),
                        max(createdAtPath).asc(),
                    )
            }

    override fun findAllUserLikeCount(): List<UserRanking> =
        kotlinJdslJpqlExecutor
            .findList {
                val foodSpotsReview = entity(FoodSpotsReview::class, "foodSpotsReview")
                val userPath = foodSpotsReview(FoodSpotsReview::user)
                val userNicknamePath = userPath(User::profile)(Profile::nickname)
                val likeTotalPath = foodSpotsReview(FoodSpotsReview::likeTotal)
                val userIdPath = foodSpotsReview(FoodSpotsReview::user)(User::id)
                val createdAtPath = path(ReviewLike::createdDateTime)
                val reviewUserPath = path(ReviewLike::review)(FoodSpotsReview::user)

                val subQuery =
                    select<LocalDateTime>(
                        max(createdAtPath),
                    ).from(
                        entity(ReviewLike::class),
                    ).where(
                        reviewUserPath.eq(userPath),
                    ).asSubquery()

                selectNew<UserRanking>(
                    userNicknamePath,
                    sum(likeTotalPath),
                ).from(
                    foodSpotsReview,
                ).groupBy(
                    userIdPath,
                    userNicknamePath,
                ).orderBy(
                    sum(likeTotalPath).desc(),
                    subQuery.asc(),
                )
            }

    override fun findAllUserTotalCount(): List<UserRanking> =
        kotlinJdslJpqlExecutor
            .findList {
                val foodSpotsReview = entity(FoodSpotsReview::class, "foodSpotsReview")
                val foodSpotsHistory = entity(FoodSpotsHistory::class, "foodSpotsHistory")
                val reviewLike = entity(ReviewLike::class, "reviewLike")

                val subquery =
                    select<Long>(
                        coalesce(
                            count(
                                foodSpotsReview(FoodSpotsReview::id),
                            ).plus(sum(foodSpotsReview(FoodSpotsReview::likeTotal))),
                            0,
                        ),
                    ).from(
                        foodSpotsReview,
                    ).where(foodSpotsReview(FoodSpotsReview::user)(User::id).eq(entity(User::class)(User::id)))
                        .asSubquery()

                val subquery2 =
                    select<Long>(
                        coalesce(count(foodSpotsHistory(FoodSpotsHistory::id)), 0),
                    ).from(
                        foodSpotsHistory,
                    ).where(foodSpotsHistory(FoodSpotsHistory::user)(User::id).eq(entity(User::class)(User::id)))
                        .asSubquery()

                val latestDate =
                    expression(
                        LocalDateTime::class,
                        "GREATEST(" +
                            "COALESCE(MAX(foodSpotsReview.createdDateTime),TO_TIMESTAMP('1970-12-31 23:59:59'))," +
                            "COALESCE(MAX(foodSpotsHistory.createdDateTime),TO_TIMESTAMP('1970-12-31 23:59:59'))," +
                            "COALESCE(MAX(reviewLike.createdDateTime),TO_TIMESTAMP('1970-12-31 23:59:59')))",
                    )

                val total = expression(Long::class, "total")
                selectNew<UserRanking>(
                    path(User::profile)(Profile::nickname),
                    subquery2.plus(subquery).`as`(total),
                ).from(
                    entity(User::class),
                    leftJoin(foodSpotsHistory).on(foodSpotsHistory(FoodSpotsHistory::user)(User::id).eq(path(User::id))),
                    leftJoin(
                        reviewLike,
                    ).on(reviewLike(ReviewLike::review)(FoodSpotsReview::id).eq(foodSpotsHistory(FoodSpotsHistory::id))),
                    leftJoin(
                        foodSpotsReview,
                    ).on(foodSpotsReview(FoodSpotsReview::user)(User::id).eq(path(User::id))),
                ).groupBy(
                    path(User::id),
                    path(User::profile)(Profile::nickname),
                ).orderBy(
                    total.desc(),
                    latestDate.asc(),
                )
            }

    private fun Jpql.dynamicOrder(sortType: ReviewSortType): Array<Sortable> =
        when (sortType) {
            ReviewSortType.LATEST -> arrayOf(path(FoodSpotsReview::id).desc())
            ReviewSortType.HIGHEST ->
                arrayOf(
                    path(FoodSpotsReview::rate).desc(),
                    path(FoodSpotsReview::id).desc(),
                )
        }

    private fun Jpql.dynamicLastId(
        sortType: ReviewSortType,
        lastId: Long?,
    ): Predicate? =
        if (lastId != null) {
            when (sortType) {
                ReviewSortType.LATEST -> path(FoodSpotsReview::id).lessThan(lastId)
                ReviewSortType.HIGHEST -> {
                    val rate = getRateByLastId(lastId)
                    or(
                        and(
                            path(FoodSpotsReview::rate).equal(rate),
                            path(FoodSpotsReview::id).lessThan(lastId),
                        ),
                        path(FoodSpotsReview::rate).lessThan(rate),
                    )
                }
            }
        } else {
            null
        }

    private fun getRateByLastId(lastId: Long): Int =
        kotlinJdslJpqlExecutor
            .findAll {
                select(path(FoodSpotsReview::rate))
                    .from(entity(FoodSpotsReview::class))
                    .where(path(FoodSpotsReview::id).equal(lastId))
            }.firstNotNullOf { it }

    private fun fillMissingRatings(countRates: MutableList<CountRate>): List<CountRate> {
        var index = 0
        if (countRates.isEmpty()) {
            for (i in 5 downTo 1) {
                countRates.add(CountRate(i, 0))
            }
        } else {
            for (i in 5 downTo 1) {
                if (index < countRates.size) {
                    val rating = countRates[index].rating
                    if (rating == i) {
                        index++
                        continue
                    }
                }
                countRates.add(index, CountRate(i, 0))
                index++
            }
        }
        return countRates
    }
}

enum class ReviewSortType {
    LATEST,
    HIGHEST,
}
