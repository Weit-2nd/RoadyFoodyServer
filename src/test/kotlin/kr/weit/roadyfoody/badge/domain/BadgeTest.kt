package kr.weit.roadyfoody.badge.domain

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import kr.weit.roadyfoody.badge.domain.Badge.BEGINNER
import kr.weit.roadyfoody.badge.domain.Badge.Companion.BEGINNER_RATE_OVER_6_REVIEWS_THRESHOLD
import kr.weit.roadyfoody.badge.domain.Badge.Companion.BEGINNER_REVIEWS_THRESHOLD
import kr.weit.roadyfoody.badge.domain.Badge.Companion.PRO_RATE_OVER_6_REVIEWS_THRESHOLD
import kr.weit.roadyfoody.badge.domain.Badge.Companion.PRO_REVIEWS_THRESHOLD
import kr.weit.roadyfoody.badge.domain.Badge.Companion.SUPER_RATE_OVER_6_REVIEWS_THRESHOLD
import kr.weit.roadyfoody.badge.domain.Badge.Companion.SUPER_REVIEWS_THRESHOLD
import kr.weit.roadyfoody.badge.domain.Badge.EXPERT
import kr.weit.roadyfoody.badge.domain.Badge.PRO
import kr.weit.roadyfoody.badge.domain.Badge.SUPER
import kr.weit.roadyfoody.rewards.domain.RewardType

class BadgeTest :
    BehaviorSpec({
        given("getBadge 테스트") {
            `when`("리뷰가 PRO 조건을 하나라도 충족하지 않는 경우") {
                then("BEGINNER 를 반환해야 한다") {
                    forAll(
                        row(BEGINNER_REVIEWS_THRESHOLD, BEGINNER_RATE_OVER_6_REVIEWS_THRESHOLD),
                        row(BEGINNER_REVIEWS_THRESHOLD + 1, BEGINNER_RATE_OVER_6_REVIEWS_THRESHOLD),
                        row(BEGINNER_REVIEWS_THRESHOLD, BEGINNER_RATE_OVER_6_REVIEWS_THRESHOLD + 1),
                    ) { numOfReviews, numOfReviewsRateOver6 ->
                        val result = Badge.getBadge(numOfReviews, numOfReviewsRateOver6)
                        result shouldBe BEGINNER
                    }
                }
            }

            `when`("리뷰가 PRO 조건을 모두 충족하는 경우") {
                then("PRO 를 반환해야 한다") {
                    val numOfReviews = BEGINNER_REVIEWS_THRESHOLD + 1
                    val numOfReviewsRateOver6 = BEGINNER_RATE_OVER_6_REVIEWS_THRESHOLD + 1
                    val result = Badge.getBadge(numOfReviews, numOfReviewsRateOver6)
                    result shouldBe PRO
                }
            }

            `when`("리뷰가 SUPER 조건을 하나라도 충족하지 않는 경우") {
                then("PRO 를 반환해야 한다") {
                    forAll(
                        row(PRO_REVIEWS_THRESHOLD, PRO_RATE_OVER_6_REVIEWS_THRESHOLD),
                        row(PRO_REVIEWS_THRESHOLD + 1, PRO_RATE_OVER_6_REVIEWS_THRESHOLD),
                        row(PRO_REVIEWS_THRESHOLD, PRO_RATE_OVER_6_REVIEWS_THRESHOLD + 1),
                    ) { numOfReviews, numOfReviewsRateOver6 ->
                        val result = Badge.getBadge(numOfReviews, numOfReviewsRateOver6)
                        result shouldBe PRO
                    }
                }
            }

            `when`("리뷰가 SUPER 조건을 모두 충족하는 경우") {
                then("SUPER 를 반환해야 한다") {
                    val numOfReviews = PRO_REVIEWS_THRESHOLD + 1
                    val numOfReviewsRateOver6 = PRO_RATE_OVER_6_REVIEWS_THRESHOLD + 1
                    val result = Badge.getBadge(numOfReviews, numOfReviewsRateOver6)
                    result shouldBe SUPER
                }
            }

            `when`("리뷰가 EXPERT 조건을 하나라도 충족하지 않는 경우") {
                then("SUPER 를 반환해야 한다") {
                    forAll(
                        row(SUPER_REVIEWS_THRESHOLD, SUPER_RATE_OVER_6_REVIEWS_THRESHOLD),
                        row(SUPER_REVIEWS_THRESHOLD + 1, SUPER_RATE_OVER_6_REVIEWS_THRESHOLD),
                        row(SUPER_REVIEWS_THRESHOLD, SUPER_RATE_OVER_6_REVIEWS_THRESHOLD + 1),
                    ) { numOfReviews, numOfReviewsRateOver6 ->
                        val result = Badge.getBadge(numOfReviews, numOfReviewsRateOver6)
                        result shouldBe SUPER
                    }
                }
            }

            `when`("리뷰가 EXPERT 조건을 모두 충족하는 경우") {
                then("EXPERT 를 반환해야 한다") {
                    val numOfReviews = SUPER_REVIEWS_THRESHOLD + 1
                    val numOfReviewsRateOver6 = SUPER_RATE_OVER_6_REVIEWS_THRESHOLD + 1
                    val result = Badge.getBadge(numOfReviews, numOfReviewsRateOver6)
                    result shouldBe EXPERT
                }
            }
        }

        given("isDemoted 테스트") {
            `when`("이전 뱃지가 더 높은 경우") {
                then("true 를 반환해야 한다") {
                    Badge.isDemoted(PRO, BEGINNER) shouldBe true
                }
            }

            `when`("이전 뱃지와 같은 경우") {
                then("false 를 반환해야 한다") {
                    Badge.isDemoted(BEGINNER, BEGINNER) shouldBe false
                }
            }

            `when`("이전 뱃지가 더 낮은 경우") {
                then("false 를 반환해야 한다") {
                    Badge.isDemoted(BEGINNER, PRO) shouldBe false
                }
            }
        }

        given("calculateBonusAmount 테스트") {
            `when`("뱃지가 BEGINNER 인 경우") {
                then("${Badge.BEGINNER_BONUS} 을 반환해야 한다") {
                    Badge.calculateBonusAmount(BEGINNER) shouldBe Badge.BEGINNER_BONUS
                }
            }

            `when`("뱃지가 PRO 인 경우") {
                then("${Badge.PRO_BONUS} 을 반환해야 한다") {
                    Badge.calculateBonusAmount(PRO) shouldBe Badge.PRO_BONUS
                }
            }

            `when`("뱃지가 SUPER 인 경우") {
                then("${Badge.SUPER_BONUS} 을 반환해야 한다") {
                    Badge.calculateBonusAmount(SUPER) shouldBe Badge.SUPER_BONUS
                }
            }

            `when`("뱃지가 EXPERT 인 경우") {
                then("${Badge.EXPERT_BONUS} 을 반환해야 한다") {
                    Badge.calculateBonusAmount(EXPERT) shouldBe Badge.EXPERT_BONUS
                }
            }
        }

        given("convertToReportType 테스트") {
            `when`("뱃지가 BEGINNER 인 경우") {
                then("BEGINNER_GIFT 를 반환해야 한다") {
                    Badge.convertToReportType(BEGINNER) shouldBe RewardType.BEGINNER_GIFT
                }
            }

            `when`("뱃지가 PRO 인 경우") {
                then("PRO_GIFT 를 반환해야 한다") {
                    Badge.convertToReportType(PRO) shouldBe RewardType.PRO_GIFT
                }
            }

            `when`("뱃지가 SUPER 인 경우") {
                then("SUPER_GIFT 를 반환해야 한다") {
                    Badge.convertToReportType(SUPER) shouldBe RewardType.SUPER_GIFT
                }
            }

            `when`("뱃지가 EXPERT 인 경우") {
                then("EXPERT_GIFT 를 반환해야 한다") {
                    Badge.convertToReportType(EXPERT) shouldBe RewardType.EXPERT_GIFT
                }
            }
        }
    })
