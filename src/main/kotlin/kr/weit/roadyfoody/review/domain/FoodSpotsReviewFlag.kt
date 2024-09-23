package kr.weit.roadyfoody.review.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import kr.weit.roadyfoody.common.domain.BaseTimeEntity
import kr.weit.roadyfoody.user.domain.User

@Entity
@Table(
    name = "food_spots_review_flags",
    uniqueConstraints = [UniqueConstraint(columnNames = ["review_id", "user_id"])],
)
@SequenceGenerator(name = "FOOD_SPOTS_REVIEW_FLAGS_SEQ", sequenceName = "FOOD_SPOTS_REVIEW_FLAGS_SEQ", allocationSize = 1, initialValue = 1)
class FoodSpotsReviewFlag(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FOOD_SPOTS_REVIEW_FLAGS_SEQ")
    @Column(name = "id", nullable = false)
    val id: Long = 0,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    val review: FoodSpotsReview,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
) : BaseTimeEntity()
