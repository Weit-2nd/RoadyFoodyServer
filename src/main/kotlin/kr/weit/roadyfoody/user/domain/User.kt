package kr.weit.roadyfoody.user.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import kr.weit.roadyfoody.badge.domain.Badge
import kr.weit.roadyfoody.common.domain.BaseModifiableEntity
import kr.weit.roadyfoody.common.exception.ErrorCode
import kr.weit.roadyfoody.common.exception.RoadyFoodyBadRequestException
import kr.weit.roadyfoody.user.utils.NICKNAME_REGEX
import kr.weit.roadyfoody.user.utils.NICKNAME_REGEX_DESC

@Entity
@Table(
    name = "users",
    indexes = [
        Index(name = "users_badge_idx", columnList = "badge"),
    ],
)
@SequenceGenerator(name = "USERS_SEQ_GENERATOR", sequenceName = "USERS_SEQ", initialValue = 1, allocationSize = 1)
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USERS_SEQ_GENERATOR")
    @Column(updatable = false, nullable = false)
    val id: Long = 0L,
    @Column(length = 48, nullable = false, updatable = false, unique = true)
    val socialId: String,
    @Embedded
    var profile: Profile,
    @Column(nullable = false)
    var coin: Int,
    @Enumerated(EnumType.STRING)
    var badge: Badge,
) : BaseModifiableEntity() {
    init {
        require(NICKNAME_REGEX.matches(profile.nickname)) { NICKNAME_REGEX_DESC }
    }

    companion object {
        fun of(
            socialId: String,
            nickname: String,
            profileImageName: String? = null,
            coin: Int = 0,
            badge: Badge = Badge.BEGINNER,
        ): User = User(socialId = socialId, profile = Profile(nickname, profileImageName), coin = coin, badge = badge)
    }

    fun decreaseCoin(minusCoin: Int): Int {
        require(coin >= minusCoin) {
            throw RoadyFoodyBadRequestException(ErrorCode.COIN_NOT_ENOUGH)
        }
        this.coin -= minusCoin
        return this.coin
    }

    fun increaseCoin(plusCoin: Int): Int {
        this.coin += plusCoin
        return this.coin
    }

    fun changeNickname(nickname: String) {
        profile = profile.changeNickname(nickname)
    }

    fun changeProfileImageName(profileImageName: String? = null) {
        profile = profile.changeProfileImageName(profileImageName)
    }

    fun changeBadge(badge: Badge) {
        this.badge = badge
    }
}

@Embeddable
class Profile(
    @Column(length = 48, nullable = false, unique = true)
    val nickname: String,
    @Column(length = 50)
    val profileImageName: String? = null,
) {
    fun changeProfileImageName(profileImageName: String? = null) = Profile(this.nickname, profileImageName)

    fun changeNickname(nickname: String) = Profile(nickname, this.profileImageName)
}
