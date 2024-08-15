package kr.weit.roadyfoody.rewards.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import kr.weit.roadyfoody.common.domain.BaseTimeEntity
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsHistory
import kr.weit.roadyfoody.user.domain.User

@Entity
@Table(
    name = "rewards",
)
@SequenceGenerator(
    name = "REWARDS_SEQ_GENERATOR",
    sequenceName = "REWARDS_SEQ",
    initialValue = 1,
    allocationSize = 1,
)
class Rewards(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REWARDS_SEQ_GENERATOR")
    val id: Long = 0L,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(updatable = false)
    val user: User,
    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(nullable = true)
    val foodSpotsHistory: FoodSpotsHistory?,
    @Column(nullable = false)
    val rewardPoint: Int,
    @Column(nullable = false)
    val coinReceived: Boolean,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val rewardType: RewardType,
) : BaseTimeEntity()
