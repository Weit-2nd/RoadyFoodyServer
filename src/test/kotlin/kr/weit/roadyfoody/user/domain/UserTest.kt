package kr.weit.roadyfoody.user.domain

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import kr.weit.roadyfoody.user.fixture.TEST_MAX_LENGTH_NICKNAME
import kr.weit.roadyfoody.user.fixture.TEST_MIN_LENGTH_NICKNAME
import kr.weit.roadyfoody.user.fixture.createTestUser
import kr.weit.roadyfoody.user.utils.NICKNAME_MAX_LENGTH
import kr.weit.roadyfoody.user.utils.NICKNAME_MIN_LENGTH
import kr.weit.roadyfoody.user.utils.NICKNAME_REGEX_DESC

class UserTest :
    BehaviorSpec({
        given("정상적인 닉네임을 입력한 경우") {
            `when`("User 를 생성하면") {
                then("User 을 반환한다.") {
                    forAll(
                        row(TEST_MIN_LENGTH_NICKNAME),
                        row("ㄱ".repeat(NICKNAME_MIN_LENGTH)),
                        row("testㄱㄴㄷ"),
                        row("ABCDEF"),
                        row("123456"),
                        row("테스터테스터"),
                        row("tester1234"),
                        row("테스터테스터123456"),
                        row("ㄱ".repeat(NICKNAME_MAX_LENGTH)),
                        row(TEST_MAX_LENGTH_NICKNAME),
                    ) {
                            nickname ->
                        createTestUser(nickname = nickname).profile.nickname shouldBe nickname
                    }
                }
            }
        }

        given("$NICKNAME_MIN_LENGTH 자 미만의 닉네임을 입력한 경우") {
            val nickname = TEST_MIN_LENGTH_NICKNAME.dropLast(1)
            `when`("User 를 생성하면") {
                then("IllegalArgumentException 을 던진다.") {
                    val ex =
                        shouldThrow<IllegalArgumentException> {
                            createTestUser(nickname = nickname)
                        }
                    ex.message shouldBe NICKNAME_REGEX_DESC
                }
            }
        }

        given("$NICKNAME_MAX_LENGTH 자 초과의 닉네임을 입력한 경우") {
            val nickname = TEST_MAX_LENGTH_NICKNAME + "a"
            `when`("User 를 생성하면") {
                then("IllegalArgumentException 을 던진다.") {
                    val ex =
                        shouldThrow<IllegalArgumentException> {
                            createTestUser(nickname = nickname)
                        }
                    ex.message shouldBe NICKNAME_REGEX_DESC
                }
            }
        }

        given("특수기호가 들어간 닉네임을 입력한 경우") {
            val nickname = "$TEST_MIN_LENGTH_NICKNAME! @#"
            `when`("User 를 생성하면") {
                then("IllegalArgumentException 을 던진다.") {
                    val ex =
                        shouldThrow<IllegalArgumentException> {
                            createTestUser(nickname = nickname)
                        }
                    ex.message shouldBe NICKNAME_REGEX_DESC
                }
            }
        }

        given("이모지가 들어간 닉네임을 입력한 경우") {
            val nickname = "${TEST_MIN_LENGTH_NICKNAME}☺️"
            `when`("User 를 생성하면") {
                then("IllegalArgumentException 을 던진다.") {
                    val ex =
                        shouldThrow<IllegalArgumentException> {
                            createTestUser(nickname = nickname)
                        }
                    ex.message shouldBe NICKNAME_REGEX_DESC
                }
            }
        }
    })
