package kr.weit.roadyfoody.admin.application.service

import kr.weit.roadyfoody.admin.dto.SimpleUserInfoResponse
import kr.weit.roadyfoody.admin.dto.SimpleUserInfoResponses
import kr.weit.roadyfoody.user.repository.UserRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class AdminQueryService(
    private val userRepository: UserRepository,
) {
    fun getUserInfoList(pageable: Pageable): SimpleUserInfoResponses {
        val users = userRepository.findAll(pageable).content
        return SimpleUserInfoResponses(
            users.map(SimpleUserInfoResponse::from),
        )
    }
}
