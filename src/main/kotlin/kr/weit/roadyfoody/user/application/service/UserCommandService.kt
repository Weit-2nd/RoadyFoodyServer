package kr.weit.roadyfoody.user.application.service

import kr.weit.roadyfoody.badge.domain.Badge
import kr.weit.roadyfoody.common.exception.ErrorCode
import kr.weit.roadyfoody.common.exception.RoadyFoodyBadRequestException
import kr.weit.roadyfoody.global.service.ImageService
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.repository.UserRepository
import kr.weit.roadyfoody.user.repository.getByUserId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class UserCommandService(
    private val userRepository: UserRepository,
    private val imageService: ImageService,
) {
    fun decreaseCoin(
        userId: Long,
        coin: Int,
    ) {
        val user = userRepository.getByUserId(userId)
        if (user.coin < coin) {
            throw RoadyFoodyBadRequestException(ErrorCode.COIN_NOT_ENOUGH)
        }
        user.decreaseCoin(coin)
    }

    fun increaseCoin(
        userId: Long,
        coin: Int,
    ) {
        val user = userRepository.getByUserId(userId)
        user.increaseCoin(coin)
    }

    @Transactional
    fun updateNickname(
        user: User,
        nickname: String,
    ) {
        if (userRepository.existsByProfileNickname(nickname)) {
            throw RoadyFoodyBadRequestException(ErrorCode.NICKNAME_ALREADY_EXISTS)
        }
        user.changeNickname(nickname)
        userRepository.save(user)
    }

    @Transactional
    fun updateProfileImage(
        user: User,
        profileImage: MultipartFile,
    ) {
        val beforeProfile = user.profile.profileImageName
        val imageName = imageService.generateImageName(profileImage)
        user.changeProfileImageName(imageName)
        userRepository.save(user)
        imageService.upload(imageName, profileImage)
        beforeProfile?.let {
            if (it != imageName) {
                imageService.remove(it)
            }
        }
    }

    @Transactional
    fun deleteProfileImage(user: User) {
        user.profile.profileImageName?.let { imageName ->
            user.changeProfileImageName()
            userRepository.save(user)
            imageService.remove(imageName)
        } ?: throw RoadyFoodyBadRequestException(ErrorCode.PROFILE_IMAGE_NOT_EXISTS)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun changeBadgeNewTx(
        userId: Long,
        badge: Badge,
    ): User = userRepository.getByUserId(userId).apply { changeBadge(badge) }
}
