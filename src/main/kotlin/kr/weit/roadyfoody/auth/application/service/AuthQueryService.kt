package kr.weit.roadyfoody.auth.application.service

import kr.weit.roadyfoody.auth.dto.KakaoUserResponse
import kr.weit.roadyfoody.auth.exception.InvalidTokenException
import kr.weit.roadyfoody.auth.presentation.client.KakaoClientInterface
import org.springframework.stereotype.Service

@Service
class AuthQueryService(
    private val kakaoClientInterface: KakaoClientInterface,
) {
    fun requestKakaoUserInfo(socialAccessToken: String): KakaoUserResponse =
        runCatching {
            kakaoClientInterface.requestUserInfo(socialAccessToken)
        }.getOrElse {
            throw InvalidTokenException()
        }
}
