package kr.weit.roadyfoody.user.repository

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kr.weit.roadyfoody.support.annotation.RepositoryTest
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.exception.UserNotFoundException
import kr.weit.roadyfoody.user.fixture.TEST_NONEXISTENT_ID
import kr.weit.roadyfoody.user.fixture.TEST_NONEXISTENT_NICKNAME
import kr.weit.roadyfoody.user.fixture.createTestUser1

@RepositoryTest
class UserRepositoryTest(
    private val userRepository: UserRepository,
) : DescribeSpec({

        lateinit var givenUser: User

        beforeEach {
            givenUser = userRepository.save(createTestUser1())
        }

        describe("getByUserId 메소드는") {
            context("존재하는 id 를 받는 경우") {
                it("일치하는 유저를 반환한다.") {
                    val user = userRepository.getByUserId(givenUser.id)
                    user.id shouldBe givenUser.id
                }
            }

            context("존재하지 않는 id 를 받는 경우") {
                it("UserNotFoundException 을 반환한다.") {
                    val ex =
                        shouldThrow<UserNotFoundException> {
                            userRepository.getByUserId(TEST_NONEXISTENT_ID)
                        }
                    ex.message shouldBe "$TEST_NONEXISTENT_ID ID 의 사용자는 존재하지 않습니다."
                }
            }
        }

        describe("getByNickname 메소드는") {
            context("존재하는 nickname 을 받는 경우") {
                it("일치하는 유저를 반환한다.") {
                    val user = userRepository.getByNickname(givenUser.nickname)
                    user.nickname shouldBe givenUser.nickname
                }
            }

            context("존재하지 않는 nickname 을 받는 경우") {
                it("UserNotFoundException 을 반환한다.") {
                    val ex =
                        shouldThrow<UserNotFoundException> {
                            userRepository.getByNickname(TEST_NONEXISTENT_NICKNAME)
                        }
                    ex.message shouldBe "$TEST_NONEXISTENT_NICKNAME 닉네임의 사용자는 존재하지 않습니다."
                }
            }
        }

        describe("existsByNickname 메소드는") {
            context("존재하는 nickname 을 받는 경우") {
                it("true 를 반환한다.") {
                    val exists = userRepository.existsByNickname(givenUser.nickname)
                    exists shouldBe true
                }
            }

            context("존재하지 않는 nickname 을 받는 경우") {
                it("false 를 반환한다.") {
                    val exists = userRepository.existsByNickname(TEST_NONEXISTENT_NICKNAME)
                    exists shouldBe false
                }
            }
        }
    })
