package kr.weit.roadyfoody.review.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import kr.weit.roadyfoody.common.domain.BaseTimeEntity
import kr.weit.roadyfoody.user.domain.User

@Entity
@Table(
    name = "review_likes",
    indexes = [
        Index(name = "review_likes_review_id_index", columnList = "review_id"),
        Index(name = "review_likes_user_id_index", columnList = "user_id"),
    ],
    uniqueConstraints = [
        UniqueConstraint(
            name = "review_likes_review_id_user_id_unique",
            columnNames = ["review_id", "user_id"],
        ),
    ],
)
@SequenceGenerator(
    name = "REVIEW_LIKES_SEQ_GENERATOR",
    sequenceName = "REVIEW_LIKES_SEQ",
    initialValue = 19,
    allocationSize = 1,
)
class ReviewLike(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REVIEW_LIKES_SEQ_GENERATOR")
    val id: Long = 0L,
    @ManyToOne
    @JoinColumn(name = "review_id")
    val review: FoodSpotsReview,
    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User,
) : BaseTimeEntity() {
    constructor(review: FoodSpotsReview, user: User) : this(0L, review, user)
}
