package kr.weit.roadyfoody.foodSpots.repository

import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import kr.weit.roadyfoody.foodSpots.domain.FoodSpots
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsHistory
import kr.weit.roadyfoody.foodSpots.exception.FoodSpotsHistoryNotFoundException
import kr.weit.roadyfoody.global.utils.findList
import kr.weit.roadyfoody.global.utils.getSlice
import kr.weit.roadyfoody.ranking.dto.UserRanking
import kr.weit.roadyfoody.user.domain.Profile
import kr.weit.roadyfoody.user.domain.User
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository

fun FoodSpotsHistoryRepository.getHistoriesByUser(
    user: User,
    size: Int,
    lastId: Long?,
): Slice<FoodSpotsHistory> = findSliceByUser(user, size, lastId)

fun FoodSpotsHistoryRepository.getByHistoryId(historyId: Long): FoodSpotsHistory =
    findById(historyId).orElseThrow { FoodSpotsHistoryNotFoundException() }

fun FoodSpotsHistoryRepository.getByFoodSpots(foodSpots: FoodSpots): List<FoodSpotsHistory> = findByFoodSpots(foodSpots)

interface FoodSpotsHistoryRepository :
    JpaRepository<FoodSpotsHistory, Long>,
    CustomFoodSpotsHistoryRepository {
    fun findByUser(user: User): List<FoodSpotsHistory>

    fun findByFoodSpots(foodSpots: FoodSpots): List<FoodSpotsHistory>
}

interface CustomFoodSpotsHistoryRepository {
    fun findSliceByUser(
        user: User,
        size: Int,
        lastId: Long?,
    ): Slice<FoodSpotsHistory>

    fun findAllUserReportCount(): List<UserRanking>
}

class CustomFoodSpotsHistoryRepositoryImpl(
    private val kotlinJdslJpqlExecutor: KotlinJdslJpqlExecutor,
) : CustomFoodSpotsHistoryRepository {
    override fun findSliceByUser(
        user: User,
        size: Int,
        lastId: Long?,
    ): Slice<FoodSpotsHistory> {
        val pageable = Pageable.ofSize(size)
        return kotlinJdslJpqlExecutor.getSlice(pageable) {
            select(entity(FoodSpotsHistory::class))
                .from(entity(FoodSpotsHistory::class))
                .whereAnd(
                    if (lastId != null) {
                        path(FoodSpotsHistory::id).lessThan(lastId)
                    } else {
                        null
                    },
                    path(FoodSpotsHistory::user).equal(user),
                ).orderBy(path(FoodSpotsHistory::id).desc())
        }
    }

    override fun findAllUserReportCount(): List<UserRanking> =
        kotlinJdslJpqlExecutor
            .findList {
                val userIdPath = path(FoodSpotsHistory::user)(User::id)
                val userNicknamePath = path(FoodSpotsHistory::user)(User::profile)(Profile::nickname)
                val profileUrlPath = path(FoodSpotsHistory::user)(User::profile)(Profile::profileImageName)
                val historyIdPath = path(FoodSpotsHistory::id)
                val createdAtPath = path(FoodSpotsHistory::createdDateTime)

                selectNew<UserRanking>(
                    userNicknamePath,
                    count(historyIdPath),
                    userIdPath,
                    profileUrlPath,
                ).from(entity(FoodSpotsHistory::class))
                    .groupBy(userIdPath, userNicknamePath, profileUrlPath)
                    .orderBy(
                        count(historyIdPath).desc(),
                        max(createdAtPath).asc(),
                    )
            }
}
