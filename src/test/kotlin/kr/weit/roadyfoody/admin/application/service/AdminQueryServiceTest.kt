package kr.weit.roadyfoody.admin.application.service

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.mockk.every
import io.mockk.mockk
import kr.weit.roadyfoody.user.fixture.createTestUsers
import kr.weit.roadyfoody.user.repository.UserRepository
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

class AdminQueryServiceTest :
    BehaviorSpec({
        val userRepository = mockk<UserRepository>()
        val adminQueryService = AdminQueryService(userRepository)

        given("getUserInfoList 테스트") {
            val users = createTestUsers()
            val pageable = PageRequest.of(0, 10)
            `when`("유저 정보를 조회하면") {
                every { userRepository.findAll(any<Pageable>()) } returns PageImpl(users, pageable, 0)
                then("SimpleUserInfoResponses 를 반환한다.") {
                    val actual = adminQueryService.getUserInfoList(pageable).userInfo
                    actual.shouldHaveSize(users.size)
                }
            }
        }
    })
