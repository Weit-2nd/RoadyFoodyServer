package kr.weit.roadyfoody.reward.domain

import jakarta.persistence.*
import kr.weit.roadyfoody.common.domain.BaseTimeEntity
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsHistory
import kr.weit.roadyfoody.user.domain.User
import jakarta.persistence.Index

@Entity
@Table(
    name = "rewards",
    indexes = [
        Index(name = "rewards_user_id_index", columnList = "user_id"),
        Index(name = "rewards_food_spots_history_id_index", columnList = "food_spots_history_id")
    ]
)
@SequenceGenerator(
    name = "REWARDS_SEQ_GENERATOR",
    sequenceName = "REWARDS_SEQ",
    initialValue = 1,
    allocationSize = 1
)
class Rewards(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REWARDS_SEQ_GENERATOR")
    val id: Long = 0L,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(updatable = false)
    val user: User,
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    val foodSpotsHistory: FoodSpotsHistory,
    @Column(nullable = false)
    val rewardPoint: Int,
    @Column(nullable = false)
    val rewardType: Boolean,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val rewardReason: RewardReason,

) : BaseTimeEntity()
