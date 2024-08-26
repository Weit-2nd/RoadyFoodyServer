package kr.weit.roadyfoody.badge.repository

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kr.weit.roadyfoody.badge.domain.Badge
import kr.weit.roadyfoody.badge.domain.UserPromotionRewardHistory
import kr.weit.roadyfoody.badge.fixture.TEST_INVALID_PROMOTION_REWARD_HISTORY_ID
import kr.weit.roadyfoody.badge.fixture.createTestUserPromotionRewardHistory
import kr.weit.roadyfoody.support.annotation.RepositoryTest
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.fixture.createTestUser
import kr.weit.roadyfoody.user.repository.UserRepository

@RepositoryTest
class UserPromotionRewardHistoryRepositoryTest(
    private val userRepository: UserRepository,
    private val userPromotionRewardHistoryRepository: UserPromotionRewardHistoryRepository,
) : DescribeSpec({
        lateinit var givenUser: User
        lateinit var givenUserPromotionRewardHistory: UserPromotionRewardHistory

        beforeEach {
            givenUser = userRepository.save(createTestUser())
            givenUserPromotionRewardHistory =
                userPromotionRewardHistoryRepository.save(createTestUserPromotionRewardHistory(user = givenUser))
        }

        describe("existsByUserIdAndBadge 테스트") {
            context("사용자 프로모션 보상 히스토리가 존재할 때") {

                it("true 를 반환해야 한다") {
                    val result =
                        userPromotionRewardHistoryRepository.existsByUserIdAndBadge(
                            givenUserPromotionRewardHistory.user.id,
                            givenUserPromotionRewardHistory.badge,
                        )

                    result shouldBe true
                }
            }

            context("사용자 프로모션 보상 히스토리가 존재하지 않을 때") {
                it("false 를 반환해야 한다") {
                    val result =
                        userPromotionRewardHistoryRepository.existsByUserIdAndBadge(
                            TEST_INVALID_PROMOTION_REWARD_HISTORY_ID,
                            Badge.BEGINNER,
                        )

                    result shouldBe false
                }
            }
        }

        describe("deleteAllByUser 테스트") {
            context("존재하는 user 를 받는 경우") {
                it("해당 user 와 관련된 UserPromotionRewardHistory 을 모두 삭제한다.") {
                    userPromotionRewardHistoryRepository.deleteAllByUser(givenUser)
                    val result = userPromotionRewardHistoryRepository.findAll()
                    result.shouldBeEmpty()
                }
            }
        }
    })
