package kr.weit.roadyfoody.badge.domain

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import kr.weit.roadyfoody.badge.domain.Badge.BEGINNER
import kr.weit.roadyfoody.badge.domain.Badge.EXPERT
import kr.weit.roadyfoody.badge.domain.Badge.PRO
import kr.weit.roadyfoody.badge.domain.Badge.SUPER

class BadgeTest :
    BehaviorSpec({
        given("getBadge 테스트") {
            `when`("리뷰가 PRO 조건을 하나라도 충족하지 않는 경우") {
                then("BEGINNER 를 반환해야 한다") {
                    forAll(
                        row(PRO.totalReviewsRequired - 1, PRO.highRatedReviewsRequired - 1),
                        row(PRO.totalReviewsRequired - 1, PRO.highRatedReviewsRequired),
                        row(PRO.totalReviewsRequired, PRO.highRatedReviewsRequired - 1),
                    ) { numOfReviews, numOfHighRatedReviews ->
                        val result = Badge.getBadge(numOfReviews, numOfHighRatedReviews)
                        result shouldBe BEGINNER
                    }
                }
            }

            `when`("리뷰가 PRO 조건을 모두 충족하는 경우") {
                then("PRO 를 반환해야 한다") {
                    val numOfReviews = PRO.totalReviewsRequired
                    val numOfHighRatedReviews = PRO.highRatedReviewsRequired

                    val result = Badge.getBadge(numOfReviews, numOfHighRatedReviews)
                    result shouldBe PRO
                }
            }

            `when`("리뷰가 SUPER 조건을 하나라도 충족하지 않는 경우") {
                then("PRO 를 반환해야 한다") {
                    forAll(
                        row(SUPER.totalReviewsRequired - 1, SUPER.highRatedReviewsRequired - 1),
                        row(SUPER.totalReviewsRequired - 1, SUPER.highRatedReviewsRequired),
                        row(SUPER.totalReviewsRequired, SUPER.highRatedReviewsRequired - 1),
                    ) { numOfReviews, numOfHighRatedReviews ->
                        val result = Badge.getBadge(numOfReviews, numOfHighRatedReviews)
                        result shouldBe PRO
                    }
                }
            }

            `when`("리뷰가 SUPER 조건을 모두 충족하는 경우") {
                then("SUPER 를 반환해야 한다") {
                    val numOfReviews = SUPER.totalReviewsRequired
                    val numOfHighRatedReviews = SUPER.highRatedReviewsRequired

                    val result = Badge.getBadge(numOfReviews, numOfHighRatedReviews)
                    result shouldBe SUPER
                }
            }

            `when`("리뷰가 EXPERT 조건을 하나라도 충족하지 않는 경우") {
                then("SUPER 를 반환해야 한다") {
                    forAll(
                        row(EXPERT.totalReviewsRequired - 1, EXPERT.highRatedReviewsRequired - 1),
                        row(EXPERT.totalReviewsRequired - 1, EXPERT.highRatedReviewsRequired),
                        row(EXPERT.totalReviewsRequired, EXPERT.highRatedReviewsRequired - 1),
                    ) { numOfReviews, numOfHighRatedReviews ->
                        val result = Badge.getBadge(numOfReviews, numOfHighRatedReviews)
                        result shouldBe SUPER
                    }
                }
            }

            `when`("리뷰가 EXPERT 조건을 모두 충족하는 경우") {
                then("EXPERT 를 반환해야 한다") {
                    val numOfReviews = EXPERT.totalReviewsRequired
                    val numOfHighRatedReviews = EXPERT.highRatedReviewsRequired
                    val result = Badge.getBadge(numOfReviews, numOfHighRatedReviews)
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
    })
