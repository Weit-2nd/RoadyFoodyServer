package kr.weit.roadyfoody.auth.application.service

import kr.weit.roadyfoody.auth.domain.SocialAccessToken
import kr.weit.roadyfoody.auth.presentation.client.KakaoClientInterface
import org.springframework.stereotype.Service

@Service
class AuthQueryService(
    private val kakaoClientInterface: KakaoClientInterface,
) {
    fun requestKakaoUserInfo(socialAccessToken: SocialAccessToken) = kakaoClientInterface.requestUserInfo(socialAccessToken)
}
