package kr.weit.roadyfoody.auth.application.service

import kr.weit.roadyfoody.auth.application.dto.DuplicatedNicknameResponse
import kr.weit.roadyfoody.auth.application.dto.KakaoUserResponse
import kr.weit.roadyfoody.auth.exception.InvalidTokenException
import kr.weit.roadyfoody.auth.presentation.client.KakaoLoginClientInterface
import kr.weit.roadyfoody.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class AuthQueryService(
    private val kakaoLoginClientInterface: KakaoLoginClientInterface,
    private val userRepository: UserRepository,
) {
    fun requestKakaoUserInfo(socialAccessToken: String): KakaoUserResponse =
        runCatching {
            kakaoLoginClientInterface.requestUserInfo(socialAccessToken)
        }.getOrElse {
            throw InvalidTokenException()
        }

    fun checkDuplicatedNickname(nickname: String): DuplicatedNicknameResponse =
        DuplicatedNicknameResponse(
            userRepository.existsByProfileNickname(nickname),
        )
}
