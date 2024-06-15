package kr.weit.roadyfoody.useragreedterm.repository

import kr.weit.roadyfoody.useragreedterm.domain.UserAgreedTerm
import org.springframework.data.jpa.repository.JpaRepository

interface UserAgreedTermRepository : JpaRepository<UserAgreedTerm, Long>
