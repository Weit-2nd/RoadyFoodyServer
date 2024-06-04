package kr.weit.roadyfoody.domain.user

import org.springframework.data.jpa.repository.JpaRepository

fun UserRepository.getByUserId(userId: Long): User {
    return findById(userId).orElseThrow { IllegalArgumentException("$userId ID 의 사용자는 존재하지 않습니다.") }
}

fun UserRepository.getByNickname(nickname: String): User {
    return findByNickname(nickname) ?: throw IllegalArgumentException("$nickname 닉네임의 사용자는 존재하지 않습니다.")
}

interface UserRepository : JpaRepository<User, Long> {
    fun findByNickname(nickname: String): User?

    fun existsByNickname(nickname: String): Boolean
}
