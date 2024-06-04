package kr.weit.roadyfoody.domain.user

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import kr.weit.roadyfoody.support.regex.NICKNAME_REGEX_DESC

class UserTest :
    BehaviorSpec({
        given("정상적인 닉네임을 입력한 경우") {
            `when`("User 를 생성하면") {
                then("User 을 반환한다.") {
                    forAll(
                        row("abcdef"),
                        row("ABCDEF"),
                        row("123456"),
                        row("테스터테스터"),
                        row("tester1234"),
                        row("테스터테스터1234567890"),
                    ) {
                            nickname ->
                        User(nickname = nickname).nickname shouldBe nickname
                    }
                }
            }
        }

        given("6 자 미만의 닉네임을 입력한 경우") {
            val nickname = "12345"
            `when`("User 를 생성하면") {
                then("IllegalArgumentException 을 반환한다.") {
                    val ex =
                        shouldThrow<IllegalArgumentException> {
                            User(nickname = nickname)
                        }
                    ex.message shouldBe NICKNAME_REGEX_DESC
                }
            }
        }

        given("16 자 초과의 닉네임을 입력한 경우") {
            val nickname = "12345678901234567"
            `when`("User 를 생성하면") {
                then("IllegalArgumentException 을 반환한다.") {
                    val ex =
                        shouldThrow<IllegalArgumentException> {
                            User(nickname = nickname)
                        }
                    ex.message shouldBe NICKNAME_REGEX_DESC
                }
            }
        }

        given("특수기호가 들어간 닉네임을 입력한 경우") {
            val nickname = "a#bc_de"
            `when`("User 를 생성하면") {
                then("IllegalArgumentException 을 반환한다.") {
                    val ex =
                        shouldThrow<IllegalArgumentException> {
                            User(nickname = nickname)
                        }
                    ex.message shouldBe NICKNAME_REGEX_DESC
                }
            }
        }

        given("이모지가 들어간 닉네임을 입력한 경우") {
            val nickname = "abcde☺️"
            `when`("User 를 생성하면") {
                then("IllegalArgumentException 을 반환한다.") {
                    val ex =
                        shouldThrow<IllegalArgumentException> {
                            User(nickname = nickname)
                        }
                    ex.message shouldBe NICKNAME_REGEX_DESC
                }
            }
        }
    })
