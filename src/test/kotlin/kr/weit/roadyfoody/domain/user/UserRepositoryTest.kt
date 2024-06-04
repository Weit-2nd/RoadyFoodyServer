package kr.weit.roadyfoody.domain.user

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kr.weit.roadyfoody.support.annotation.RepositoryTest

@RepositoryTest
class UserRepositoryTest(
    private val userRepository: UserRepository,
) : DescribeSpec({

        lateinit var givenUser: User
        val nonexistentId = 0L
        val nonexistentNickname = "JohnDoe"

        beforeEach {
            givenUser = userRepository.save(User(nickname = "existentNick"))
        }

        describe("getByUserId 메소드는") {
            context("존재하는 id 를 받는 경우") {
                it("일치하는 유저를 반환한다.") {
                    val user = userRepository.getByUserId(givenUser.id)
                    user.id shouldBe givenUser.id
                }
            }

            context("존재하지 않는 id 를 받는 경우") {
                it("IllegalArgumentException 을 반환한다.") {
                    val ex =
                        shouldThrow<IllegalArgumentException> {
                            userRepository.getByUserId(nonexistentId)
                        }
                    ex.message shouldBe "$nonexistentId ID 의 사용자는 존재하지 않습니다."
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
                it("IllegalArgumentException 을 반환한다.") {
                    val ex =
                        shouldThrow<IllegalArgumentException> {
                            userRepository.getByNickname(nonexistentNickname)
                        }
                    ex.message shouldBe "$nonexistentNickname 닉네임의 사용자는 존재하지 않습니다."
                }
            }
        }

        describe("existsById 메소드는") {
            context("존재하는 id 를 받는 경우") {
                it("true 를 반환한다.") {
                    val exists = userRepository.existsById(givenUser.id)
                    exists shouldBe true
                }
            }

            context("존재하지 않는 id 를 받는 경우") {
                it("false 를 반환한다.") {
                    val exists = userRepository.existsById(nonexistentId)
                    exists shouldBe false
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
                    val exists = userRepository.existsByNickname(nonexistentNickname)
                    exists shouldBe false
                }
            }
        }
    })
