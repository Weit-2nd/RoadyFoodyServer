package kr.weit.roadyfoody.user.application.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import kr.weit.roadyfoody.auth.fixture.PROFILE_IMAGE_FILE_NAME
import kr.weit.roadyfoody.badge.domain.Badge
import kr.weit.roadyfoody.common.exception.ErrorCode
import kr.weit.roadyfoody.common.exception.RoadyFoodyBadRequestException
import kr.weit.roadyfoody.global.service.ImageService
import kr.weit.roadyfoody.user.fixture.TEST_MAX_LENGTH_NICKNAME
import kr.weit.roadyfoody.user.fixture.TEST_USER_ID
import kr.weit.roadyfoody.user.fixture.TEST_USER_PROFILE_IMAGE_NAME
import kr.weit.roadyfoody.user.fixture.createTestUser
import kr.weit.roadyfoody.user.repository.UserRepository
import org.springframework.web.multipart.MultipartFile
import java.util.Optional

class UserCommandServiceTest :
    BehaviorSpec({
        val userRepository = mockk<UserRepository>()
        val imageService = mockk<ImageService>()
        val userCommandService = UserCommandService(userRepository, imageService)
        val user = createTestUser()

        given("decreaseCoin 테스트") {
            val minusCoin = 100
            val expectedCoin = user.coin - minusCoin
            every { userRepository.findById(TEST_USER_ID) } returns Optional.of(user)
            `when`("코인을 감소시키면") {
                userCommandService.decreaseCoin(user.id, minusCoin)
                then("코인이 감소한다.") {
                    user.coin shouldBe expectedCoin
                }
            }

            `when`("코인이 부족하면") {
                val minusCoin = 1000
                every { userRepository.findById(TEST_USER_ID) } returns Optional.of(user)
                then("에러가 발생한다.") {
                    val ex =
                        shouldThrow<RoadyFoodyBadRequestException> {
                            userCommandService.decreaseCoin(user.id, minusCoin)
                        }
                    ex.message shouldBe ErrorCode.COIN_NOT_ENOUGH.errorMessage
                }
            }
        }

        given("increaseCoin 테스트") {
            val plusCoin = 100
            val expectedCoin = user.coin + plusCoin
            every { userRepository.findById(TEST_USER_ID) } returns Optional.of(user)
            `when`("코인을 증가시키면") {
                userCommandService.increaseCoin(user.id, plusCoin)
                then("코인이 증가한다.") {
                    user.coin shouldBe expectedCoin
                }
            }
        }

        given("updateNickname 테스트") {
            val nickname = TEST_MAX_LENGTH_NICKNAME
            `when`("닉네임을 변경하면") {
                every { userRepository.save(user) } returns user
                every { userRepository.existsByProfileNickname(nickname) } returns false
                user.profile.nickname shouldNotBe nickname
                userCommandService.updateNickname(user, nickname)
                then("닉네임이 변경된다.") {
                    user.profile.nickname shouldBe nickname
                }
            }

            `when`("닉네임이 이미 존재하면") {
                every { userRepository.existsByProfileNickname(nickname) } returns true
                then("NICKNAME_ALREADY_EXISTS 에러가 발생한다.") {
                    val ex =
                        shouldThrow<RoadyFoodyBadRequestException> {
                            userCommandService.updateNickname(user, nickname)
                        }
                    ex.message shouldBe ErrorCode.NICKNAME_ALREADY_EXISTS.errorMessage
                }
            }
        }

        given("updateProfileImage 테스트") {
            val profileImage = mockk<MultipartFile>()
            var imageName = TEST_USER_PROFILE_IMAGE_NAME
            `when`("기본 프로필에서 프로필을 변경하면") {
                user.changeProfileImageName()
                every { imageService.generateImageName(profileImage) } returns imageName
                every { userRepository.save(user) } returns user
                every { imageService.upload(imageName, profileImage) } returns Unit
                user.profile.profileImageName shouldBe null
                userCommandService.updateProfileImage(user, profileImage)
                then("프로필 이미지가 변경된다.") {
                    user.profile.profileImageName shouldBe imageName
                }
            }

            `when`("기존 프로필 이미지가 존재하면") {
                val beforeProfile = user.profile.profileImageName
                imageName = "${PROFILE_IMAGE_FILE_NAME}_new"
                every { imageService.generateImageName(any()) } returns imageName
                every { userRepository.save(user) } returns user
                every { imageService.upload(imageName, profileImage) } returns Unit
                every { imageService.remove(beforeProfile!!) } returns Unit
                user.profile.profileImageName shouldBe beforeProfile
                userCommandService.updateProfileImage(user, profileImage)
                then("기존 프로필 이미지가 삭제되고 새로운 이미지가 등록된다.") {
                    user.profile.profileImageName shouldBe imageName
                }
            }
        }

        given("deleteProfileImage 테스트") {
            `when`("프로필 이미지를 삭제하면") {
                val imageName = user.profile.profileImageName
                every { userRepository.save(user) } returns user
                every { imageService.remove(imageName!!) } returns Unit
                user.profile.profileImageName shouldBe imageName
                userCommandService.deleteProfileImage(user)
                then("프로필 이미지가 삭제된다.") {
                    user.profile.profileImageName shouldBe null
                }
            }

            `when`("프로필 이미지가 존재하지 않으면") {
                user.changeProfileImageName()
                then("PROFILE_IMAGE_NOT_EXISTS 에러가 발생한다.") {
                    user.profile.profileImageName shouldBe null
                    val ex =
                        shouldThrow<RoadyFoodyBadRequestException> {
                            userCommandService.deleteProfileImage(user)
                        }
                    ex.message shouldBe ErrorCode.PROFILE_IMAGE_NOT_EXISTS.errorMessage
                }
            }
        }

        given("changeBadgeNewTx 테스트") {
            `when`("뱃지를 변경하면") {
                val user = createTestUser(badge = Badge.BEGINNER)
                val newBadge = Badge.PRO
                every { userRepository.findById(any()) } returns Optional.of(user)
                userCommandService.changeBadgeNewTx(user.id, newBadge)
                then("새로운 뱃지로 변경된다.") {
                    user.badge shouldBe newBadge
                }
            }
        }
    })
