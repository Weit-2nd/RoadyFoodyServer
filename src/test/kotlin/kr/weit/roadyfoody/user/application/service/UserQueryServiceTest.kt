package kr.weit.roadyfoody.user.application.service

import TEST_REVIEW_PHOTO_URL
import createMockSliceReview
import createMockSliceReviewLike
import createTestReviewPhoto
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOTS_LAST_ID
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOTS_PHOTO_URL
import kr.weit.roadyfoody.foodSpots.fixture.TEST_FOOD_SPOTS_SIZE
import kr.weit.roadyfoody.foodSpots.fixture.TEST_REST_DAILY_REPORT_CREATION_COUNT
import kr.weit.roadyfoody.foodSpots.fixture.createMockSliceFoodHistory
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodSpotsPhoto
import kr.weit.roadyfoody.foodSpots.fixture.createTestReportFoodCategory
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsHistoryRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsPhotoRepository
import kr.weit.roadyfoody.foodSpots.repository.ReportFoodCategoryRepository
import kr.weit.roadyfoody.foodSpots.repository.getByHistoryId
import kr.weit.roadyfoody.foodSpots.repository.getHistoriesByUser
import kr.weit.roadyfoody.global.TEST_LAST_ID
import kr.weit.roadyfoody.global.TEST_PAGE_SIZE
import kr.weit.roadyfoody.global.service.ImageService
import kr.weit.roadyfoody.review.repository.FoodSpotsReviewPhotoRepository
import kr.weit.roadyfoody.review.repository.FoodSpotsReviewRepository
import kr.weit.roadyfoody.review.repository.ReviewLikeRepository
import kr.weit.roadyfoody.review.repository.getByReview
import kr.weit.roadyfoody.user.exception.UserNotFoundException
import kr.weit.roadyfoody.user.fixture.TEST_USER_ID
import kr.weit.roadyfoody.user.fixture.TEST_USER_PROFILE_IMAGE_URL
import kr.weit.roadyfoody.user.fixture.createTestUser
import kr.weit.roadyfoody.user.repository.UserRepository
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.data.redis.core.RedisTemplate
import java.util.Optional
import java.util.concurrent.ExecutorService

class UserQueryServiceTest :
    BehaviorSpec({
        val userRepository = mockk<UserRepository>()
        val imageService = mockk<ImageService>()
        val foodSpotsHistoryRepository = mockk<FoodSpotsHistoryRepository>()
        val foodSpotsPhotoRepository = mockk<FoodSpotsPhotoRepository>()
        val reportFoodCategoryRepository = mockk<ReportFoodCategoryRepository>()
        val reviewRepository = mockk<FoodSpotsReviewRepository>()
        val reviewPhotoRepository = mockk<FoodSpotsReviewPhotoRepository>()
        val reviewLikeRepository = mockk<ReviewLikeRepository>()
        val redisTemplate = mockk<RedisTemplate<String, String>>()
        val executor = mockk<ExecutorService>()
        val cacheManager = mockk<CacheManager>()
        val userQueryService =
            UserQueryService(
                userRepository,
                imageService,
                foodSpotsHistoryRepository,
                foodSpotsPhotoRepository,
                reportFoodCategoryRepository,
                reviewRepository,
                reviewPhotoRepository,
                reviewLikeRepository,
                redisTemplate,
                executor,
                cacheManager,
            )
        val list = listOf("1:user2:20", "2:user3:15", "3:user1:10")
        val cache = mockk<Cache>()

        afterEach { clearAllMocks() }

        given("getUserInfo 테스트") {
            `when`("프로필사진이 존재하는 유저의 경우") {
                val user = createTestUser()

                every { userRepository.findById(any<Long>()) } returns Optional.of(user)
                every { imageService.getDownloadUrl(any<String>()) } returns TEST_USER_PROFILE_IMAGE_URL
                every { redisTemplate.opsForValue().get(any()) } returns TEST_REST_DAILY_REPORT_CREATION_COUNT.toString()
                every { cacheManager.getCache(any()) } returns cache
                every { cache.get(any(), List::class.java) } returns list

                then("프로필사진 URL 이 존재하는 응답을 반환한다.") {
                    val userInfoResponse = userQueryService.getUserInfo(user)
                    userInfoResponse.profileImageUrl shouldBe TEST_USER_PROFILE_IMAGE_URL
                    verify(exactly = 1) { imageService.getDownloadUrl(any<String>()) }
                }
            }

            `when`("프로필사진이 존재하지 않는 유저의 경우") {
                val user = createTestUser(profileImageName = null)
                every { userRepository.findById(any<Long>()) } returns Optional.of(user)
                every { imageService.getDownloadUrl(any<String>()) } returns TEST_USER_PROFILE_IMAGE_URL
                every { redisTemplate.opsForValue().get(any()) } returns TEST_REST_DAILY_REPORT_CREATION_COUNT.toString()
                every { cacheManager.getCache(any()) } returns cache
                every { cache.get(any(), List::class.java) } returns list

                then("프로필사진 URL 이 null 인 응답을 반환한다.") {
                    val userInfoResponse = userQueryService.getUserInfo(user)
                    userInfoResponse.profileImageUrl.shouldBeNull()
                    verify(exactly = 0) { imageService.getDownloadUrl(any<String>()) }
                }
            }
        }
        given("getReportHistories 테스트") {
            val user = createTestUser()
            every { userRepository.findById(TEST_USER_ID) } returns Optional.of(user)
            every {
                foodSpotsHistoryRepository.getHistoriesByUser(
                    user,
                    TEST_FOOD_SPOTS_SIZE,
                    TEST_FOOD_SPOTS_LAST_ID,
                )
            } returns createMockSliceFoodHistory()
            every { foodSpotsPhotoRepository.getByHistoryId(any()) } returns
                listOf(
                    createTestFoodSpotsPhoto(),
                )
            every { imageService.getDownloadUrl(any()) } returns TEST_FOOD_SPOTS_PHOTO_URL
            every { reportFoodCategoryRepository.getByHistoryId(any()) } returns
                listOf(
                    createTestReportFoodCategory(),
                )
            every { executor.execute(any()) } answers {
                firstArg<Runnable>().run()
            }
            `when`("정상적인 데이터가 들어올 경우") {
                then("정상적으로 조회되어야 한다.") {
                    userQueryService.getReportHistories(
                        TEST_USER_ID,
                        TEST_FOOD_SPOTS_SIZE,
                        TEST_FOOD_SPOTS_LAST_ID,
                    )
                    verify(exactly = 1) {
                        userRepository.findById(any())
                        reportFoodCategoryRepository.getByHistoryId(any())
                        foodSpotsHistoryRepository.getHistoriesByUser(any(), any(), any())
                        foodSpotsPhotoRepository.getByHistoryId(any())
                        imageService.getDownloadUrl(any())
                        executor.execute(any())
                    }
                }
            }

            `when`("사용자가 존재하지 않는 경우") {
                every { userRepository.findById(TEST_USER_ID) } returns Optional.empty()
                then("UserNotFoundException이 발생한다.") {
                    shouldThrow<UserNotFoundException> {
                        userQueryService.getReportHistories(
                            TEST_USER_ID,
                            TEST_FOOD_SPOTS_SIZE,
                            TEST_FOOD_SPOTS_LAST_ID,
                        )
                    }
                }
            }
        }

        given("getUserReviews 테스트") {
            every { userRepository.findById(any()) } returns Optional.of(createTestUser())
            every {
                reviewRepository.sliceByUser(
                    any(),
                    any(),
                    any(),
                )
            } returns createMockSliceReview()
            every { reviewPhotoRepository.getByReview(any()) } returns listOf(createTestReviewPhoto())
            every { imageService.getDownloadUrl(any()) } returns TEST_FOOD_SPOTS_PHOTO_URL
            every { executor.execute(any()) } answers {
                firstArg<Runnable>().run()
            }
            `when`("정상적인 데이터가 들어올 경우") {
                then("정상적으로 리뷰가 조회되어야 한다.") {
                    userQueryService.getUserReviews(
                        TEST_USER_ID,
                        TEST_PAGE_SIZE,
                        TEST_LAST_ID,
                    )
                    verify(exactly = 1) {
                        userRepository.findById(any())
                        reviewRepository.sliceByUser(any(), any(), any())
                        reviewPhotoRepository.getByReview(any())
                        imageService.getDownloadUrl(any())
                        executor.execute(any())
                    }
                }
            }

            `when`("유저가 없을 경우") {
                every { userRepository.findById(any()) } returns Optional.empty()
                then("UserNotFoundException 예외 발생") {
                    shouldThrow<UserNotFoundException> {
                        userQueryService.getUserReviews(
                            TEST_USER_ID,
                            TEST_PAGE_SIZE,
                            TEST_LAST_ID,
                        )
                    }
                }
            }
        }

        given("getLikeReviews 테스트") {
            `when`("정상적인 데이터가 들어올 경우") {
                val sliceReviewLike = createMockSliceReviewLike()
                val reviewPhoto = createTestReviewPhoto()
                val userProfile =
                    sliceReviewLike.content
                        .first()
                        .user.profile.profileImageName!!
                every { userRepository.findById(any()) } returns Optional.of(createTestUser())
                every {
                    reviewLikeRepository.sliceLikeReviews(
                        any(),
                        any(),
                        any(),
                    )
                } returns sliceReviewLike
                every { reviewPhotoRepository.getByReview(any()) } returns listOf(reviewPhoto)
                every { imageService.getDownloadUrl(reviewPhoto.fileName) } returns TEST_REVIEW_PHOTO_URL
                every { executor.execute(any()) } answers { firstArg<Runnable>().run() }
                every { imageService.getDownloadUrl(userProfile) } returns TEST_USER_PROFILE_IMAGE_URL
                then("정상적으로 좋아요한 리뷰가 조회되어야 한다.") {
                    userQueryService.getLikeReviews(
                        TEST_USER_ID,
                        TEST_PAGE_SIZE,
                        TEST_LAST_ID,
                    )
                    verify(exactly = 1) {
                        userRepository.findById(any())
                        reviewLikeRepository.sliceLikeReviews(any(), any(), any())
                        reviewPhotoRepository.getByReview(any())
                        executor.execute(any())
                    }
                    verify(exactly = 2) {
                        imageService.getDownloadUrl(any())
                    }
                }
            }
        }

        given("getUserStatistics 테스트") {
            `when`("userId가 들어 올 경우") {
                every { userRepository.findById(TEST_USER_ID) } returns Optional.of(createTestUser())
                every { foodSpotsHistoryRepository.countByUser(any()) } returns 1
                every { reviewRepository.countByUser(any()) } returns 1
                every { reviewLikeRepository.countByUser(any()) } returns 1
                then("정상적으로 조회되어야 한다.") {
                    userQueryService.getUserStatistics(TEST_USER_ID)
                    verify(exactly = 1) {
                        userRepository.findById(TEST_USER_ID)
                        foodSpotsHistoryRepository.countByUser(any())
                        reviewRepository.countByUser(any())
                        reviewLikeRepository.countByUser(any())
                    }
                }
            }
        }
    })
