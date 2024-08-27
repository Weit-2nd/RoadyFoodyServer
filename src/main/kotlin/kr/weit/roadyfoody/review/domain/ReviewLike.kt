package kr.weit.roadyfoody.review.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import kr.weit.roadyfoody.common.domain.BaseTimeEntity
import kr.weit.roadyfoody.user.domain.User

@Entity
@Table(
    name = "review_likes",
    indexes = [
        Index(name = "review_likes_review_id_index", columnList = "review_id"),
        Index(name = "review_likes_user_id_index", columnList = "user_id"),
    ],
)
@IdClass(ReviewLikeId::class)
data class ReviewLike(
    @Id
    @ManyToOne
    @JoinColumn(name = "review_id")
    val review: FoodSpotsReview,
    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User,
) : BaseTimeEntity()
