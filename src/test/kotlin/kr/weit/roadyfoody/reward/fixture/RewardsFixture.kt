package kr.weit.roadyfoody.reward.fixture

import kr.weit.roadyfoody.common.dto.SliceResponse
import kr.weit.roadyfoody.foodSpots.domain.*
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodHistory
import kr.weit.roadyfoody.global.TEST_PAGE_SIZE
import kr.weit.roadyfoody.reward.application.dto.RewardsResponse
import kr.weit.roadyfoody.reward.domain.RewardReason
import kr.weit.roadyfoody.reward.domain.Rewards
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.fixture.createTestUser
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import java.time.LocalDateTime

fun createRewardsResponse() : SliceResponse<RewardsResponse> {
    return SliceResponse(
        contents = listOf(
                RewardsResponse(
                    id = 0L,
                    rewardType = "지급",
                    rewardPoint = 100,
                    rewardReason = "리포트 업데이트",
                    createdAt = LocalDateTime.now()
                )
        ),
        hasNext = false
    )
}

fun createTestRewards(
    user : User = createTestUser(0L),
    foodSpotsHistory : FoodSpotsHistory = createTestFoodHistory(0L),
    rewardPoint: Int = 100,
    rewardType: Boolean = true,
    rewardReason: RewardReason =RewardReason.REPORT_CREATE,
): Rewards {
    return Rewards(
        user = user,
        foodSpotsHistory = foodSpotsHistory,
        rewardPoint = rewardPoint,
        rewardType = rewardType,
        rewardReason = rewardReason
    )
}

fun createSliceRewards(): Slice<Rewards> =
    SliceImpl(
        listOf(createTestRewards()),
        Pageable.ofSize(TEST_PAGE_SIZE),
        false,
    )