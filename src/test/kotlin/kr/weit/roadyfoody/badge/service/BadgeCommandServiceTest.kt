package kr.weit.roadyfoody.badge.service

import createTestFoodSpotsReviews
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.should
import io.kotest.matchers.types.beInstanceOf
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kr.weit.roadyfoody.badge.domain.Badge
import kr.weit.roadyfoody.badge.fixture.createTestUserPromotionRewardHistory
import kr.weit.roadyfoody.badge.repository.UserPromotionRewardHistoryRepository
import kr.weit.roadyfoody.review.repository.FoodSpotsReviewRepository
import kr.weit.roadyfoody.rewards.fixture.createTestRewards
import kr.weit.roadyfoody.rewards.repository.RewardsRepository
import kr.weit.roadyfoody.user.application.service.UserCommandService
import kr.weit.roadyfoody.user.fixture.createTestUser
import kr.weit.roadyfoody.user.repository.UserRepository
import java.util.Optional

class BadgeCommandServiceTest :
    BehaviorSpec({
        val userCommandService = mockk<UserCommandService>()
        val foodSpotsReviewRepository = mockk<FoodSpotsReviewRepository>()
        val userPromotionRewardHistoryRepository = mockk<UserPromotionRewardHistoryRepository>()
        val rewardsRepository = mockk<RewardsRepository>()
        val userRepository = mockk<UserRepository>()
        val badgeCommandService =
            BadgeCommandService(
                userCommandService,
                foodSpotsReviewRepository,
                userPromotionRewardHistoryRepository,
                rewardsRepository,
                userRepository,
            )

        afterEach { clearAllMocks() }

        given("tryChangeBadgeAndIfPromotedGiveBonus 테스트") {
            beforeContainer {
                every { userPromotionRewardHistoryRepository.save(any()) } returns createTestUserPromotionRewardHistory()
                every { rewardsRepository.save(any()) } returns createTestRewards()
                every { userCommandService.increaseCoin(any(), any()) } just runs
            }
            `when`("회원의 뱃지가 승급되며 이전에 보상을 받은 적이 없는 경우") {
                val user = createTestUser(badge = Badge.BEGINNER)
                val reviews =
                    createTestFoodSpotsReviews(
                        otherRate = Badge.HIGH_RATING_CONDITION,
                        sizeOfAllReviews = Badge.PRO.totalReviewsRequired,
                        sizeOfHighRatedReviews = Badge.PRO.highRatedReviewsRequired,
                    )
                every { userRepository.findById(any()) } returns Optional.of(user)
                every { userPromotionRewardHistoryRepository.existsByUserIdAndBadge(any(), any()) } returns false
                every { foodSpotsReviewRepository.findByUser(any()) } returns reviews
                then("뱃지가 승급되고 보상이 지급되어야 한다") {
                    badgeCommandService.tryChangeBadgeAndIfPromotedGiveBonus(user.id)

                    user.badge should beInstanceOf(Badge.PRO::class)

                    verify(exactly = 1) {
                        userPromotionRewardHistoryRepository.save(any())
                        rewardsRepository.save(any())
                        userCommandService.increaseCoin(any(), any())
                    }
                }
            }

            `when`("회원의 뱃지가 승급되며 이전에 보상을 받은 적이 있는 경우") {
                val user = createTestUser(badge = Badge.BEGINNER)
                val reviews =
                    createTestFoodSpotsReviews(
                        otherRate = Badge.HIGH_RATING_CONDITION,
                        sizeOfAllReviews = Badge.PRO.totalReviewsRequired,
                        sizeOfHighRatedReviews = Badge.PRO.highRatedReviewsRequired,
                    )
                every { userRepository.findById(any()) } returns Optional.of(user)
                every { foodSpotsReviewRepository.findByUser(any()) } returns reviews
                every { userPromotionRewardHistoryRepository.existsByUserIdAndBadge(any(), any()) } returns true
                then("뱃지가 승급되고 보상이 지급되지 않아야 한다") {
                    badgeCommandService.tryChangeBadgeAndIfPromotedGiveBonus(user.id)

                    user.badge should beInstanceOf(Badge.PRO::class)

                    verify(exactly = 0) {
                        userPromotionRewardHistoryRepository.save(any())
                        rewardsRepository.save(any())
                        userCommandService.increaseCoin(any(), any())
                    }
                }
            }

            `when`("회원의 뱃지가 유지되는 경우") {
                val user = createTestUser(badge = Badge.BEGINNER)
                val reviews =
                    createTestFoodSpotsReviews(
                        otherRate = Badge.HIGH_RATING_CONDITION - 1,
                        sizeOfAllReviews = Badge.BEGINNER.totalReviewsRequired,
                        sizeOfHighRatedReviews = Badge.BEGINNER.highRatedReviewsRequired,
                    )
                every { userRepository.findById(any()) } returns Optional.of(user)
                every { foodSpotsReviewRepository.findByUser(any()) } returns reviews
                then("뱃지가 변하지 않고 보상이 지급되지 않아야 한다") {
                    badgeCommandService.tryChangeBadgeAndIfPromotedGiveBonus(user.id)

                    user.badge should beInstanceOf(Badge.BEGINNER::class)

                    verify(exactly = 0) {
                        userPromotionRewardHistoryRepository.save(any())
                        rewardsRepository.save(any())
                        userCommandService.increaseCoin(any(), any())
                    }
                }
            }

            `when`("회원의 뱃지가 강등될 경우") {
                val user = createTestUser(badge = Badge.PRO)
                val reviewsForDemotion =
                    createTestFoodSpotsReviews(
                        otherRate = Badge.HIGH_RATING_CONDITION - 1,
                        sizeOfAllReviews = Badge.PRO.totalReviewsRequired,
                        sizeOfHighRatedReviews = Badge.PRO.highRatedReviewsRequired - 1,
                    )
                every { userRepository.findById(any()) } returns Optional.of(user)
                every { foodSpotsReviewRepository.findByUser(any()) } returns reviewsForDemotion
                then("뱃지가 강등되고 보상이 지급되지 않아야 한다") {
                    badgeCommandService.tryChangeBadgeAndIfPromotedGiveBonus(user.id)

                    user.badge should beInstanceOf(Badge.BEGINNER::class)

                    verify(exactly = 0) {
                        userPromotionRewardHistoryRepository.save(any())
                        rewardsRepository.save(any())
                        userCommandService.increaseCoin(any(), any())
                    }
                }
            }
        }
    })
