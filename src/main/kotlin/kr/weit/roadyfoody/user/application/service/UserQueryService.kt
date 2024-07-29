package kr.weit.roadyfoody.user.application.service

import kr.weit.roadyfoody.global.service.ImageService
import kr.weit.roadyfoody.user.application.dto.UserInfoResponse
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.repository.UserRepository
import kr.weit.roadyfoody.user.repository.getByUserId
import org.springframework.stereotype.Service

@Service
class UserQueryService(
    private val userRepository: UserRepository,
    private val imageService: ImageService,
) {
    fun getUserInfo(user: User): UserInfoResponse {
        val user = userRepository.getByUserId(user.id)
        val profileImageUrl = user.profile.profileImageName?.let { imageService.getDownloadUrl(it) }

        return UserInfoResponse.of(
            user.profile.nickname,
            profileImageUrl,
            user.coin,
        )
    }
}
