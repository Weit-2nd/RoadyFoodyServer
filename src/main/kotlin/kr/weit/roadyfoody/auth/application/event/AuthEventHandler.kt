package kr.weit.roadyfoody.auth.application.event

import kr.weit.roadyfoody.auth.security.jwt.JwtUtil
import kr.weit.roadyfoody.foodSpots.application.service.FoodSpotsCommandService
import kr.weit.roadyfoody.global.service.ImageService
import kr.weit.roadyfoody.review.application.service.ReviewCommandService
import kr.weit.roadyfoody.rewards.repository.RewardsRepository
import kr.weit.roadyfoody.user.repository.UserRepository
import kr.weit.roadyfoody.user.repository.getByUserId
import kr.weit.roadyfoody.useragreedterm.repository.UserAgreedTermRepository
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class AuthEventHandler(
    private val userRepository: UserRepository,
    private val userAgreedTermRepository: UserAgreedTermRepository,
    private val imageService: ImageService,
    private val foodSpotsCommandService: FoodSpotsCommandService,
    private val reviewCommandService: ReviewCommandService,
    private val jwtUtil: JwtUtil,
    private val rewardsRepository: RewardsRepository,
) {
    // TODO : 각 도메인 개발 상황에 따라 기능을 추가해주세요.
    @EventListener(AuthLeaveEvent::class)
    fun handleAuthLeaveEvent(event: AuthLeaveEvent) {
        val user = userRepository.getByUserId(event.userId)
        userAgreedTermRepository.deleteAllByUser(user)
        foodSpotsCommandService.deleteWithdrawUserReport(user)
        reviewCommandService.deleteWithdrewUserReview(user)
        rewardsRepository.deleteAllByUser(user)
        userRepository.delete(user)
        val profileImageName = user.profile.profileImageName
        profileImageName?.let {
            imageService.remove(profileImageName)
        }
        jwtUtil.removeCachedRefreshToken(user.id)
    }
}
