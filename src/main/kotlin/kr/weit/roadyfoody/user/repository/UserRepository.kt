package kr.weit.roadyfoody.user.repository

import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.exception.UserNotFoundException
import org.springframework.data.jpa.repository.JpaRepository

fun UserRepository.getByUserId(userId: Long): User = findById(userId).orElseThrow { UserNotFoundException("$userId ID 의 사용자는 존재하지 않습니다.") }

fun UserRepository.getByNickname(nickname: String): User =
    findByNickname(nickname) ?: throw UserNotFoundException("$nickname 닉네임의 사용자는 존재하지 않습니다.")

interface UserRepository : JpaRepository<User, Long> {
    fun findByNickname(nickname: String): User?

    fun existsByNickname(nickname: String): Boolean
}
