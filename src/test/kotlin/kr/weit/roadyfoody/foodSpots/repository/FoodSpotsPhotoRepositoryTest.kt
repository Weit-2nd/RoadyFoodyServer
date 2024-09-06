package kr.weit.roadyfoody.foodSpots.repository

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kr.weit.roadyfoody.foodSpots.domain.FoodSpots
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsHistory
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsPhoto
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodHistory
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodSpots
import kr.weit.roadyfoody.foodSpots.fixture.createTestFoodSpotsPhoto
import kr.weit.roadyfoody.support.annotation.RepositoryTest
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.fixture.createTestUser
import kr.weit.roadyfoody.user.repository.UserRepository

@RepositoryTest
class FoodSpotsPhotoRepositoryTest(
    private val foodSpotsPhotoRepository: FoodSpotsPhotoRepository,
    private val foodSpotsHistoryRepository: FoodSpotsHistoryRepository,
    private val foodSpotsRepository: FoodSpotsRepository,
    private val userRepository: UserRepository,
) : DescribeSpec({
        lateinit var user: User
        lateinit var foodSpots: FoodSpots
        lateinit var otherFoodSpots: FoodSpots
        lateinit var foodSpotsHistory: FoodSpotsHistory
        lateinit var notExistPhotoFoodSpotsHistory: FoodSpotsHistory
        lateinit var foodSpotsPhoto: FoodSpotsPhoto
        lateinit var otherFoodSpotsPhoto: FoodSpotsPhoto
        beforeEach {
            user = userRepository.save(createTestUser(0L))
            foodSpots = foodSpotsRepository.save(createTestFoodSpots())
            otherFoodSpots = foodSpotsRepository.save(createTestFoodSpots())
            foodSpotsHistory = foodSpotsHistoryRepository.save(createTestFoodHistory(foodSpots = foodSpots, user = user))
            notExistPhotoFoodSpotsHistory =
                foodSpotsHistoryRepository.save(
                    createTestFoodHistory(
                        foodSpots = otherFoodSpots,
                        user = user,
                    ),
                )
            foodSpotsPhoto = createTestFoodSpotsPhoto(foodSpotsHistory)
            otherFoodSpotsPhoto = createTestFoodSpotsPhoto(foodSpotsHistory)
            foodSpotsPhotoRepository.saveAll(
                listOf(
                    foodSpotsPhoto,
                    otherFoodSpotsPhoto,
                ),
            )
        }

        describe("getByHistoryId 메소드는") {
            context("존재하는 historyId 를 받는 경우") {
                it("일치하는 FoodSpotsPhoto 리스트를 반환한다.") {
                    val result = foodSpotsPhotoRepository.getByHistoryId(foodSpotsHistory.id)
                    result.map { it.id } shouldBe listOf(foodSpotsPhoto.id, otherFoodSpotsPhoto.id)
                }
            }

            context("존재하지 않는 historyId 를 받는 경우") {
                it("빈 리스트를 반환한다.") {
                    val result = foodSpotsPhotoRepository.getByHistoryId(0L)
                    result.size shouldBe 0
                }
            }
        }

        describe("findByHistoryIn 메소드는") {
            context("존재하는 history 리스트를 받는 경우") {
                it("일치하는 FoodSpotsPhoto 리스트를 반환한다.") {
                    val result = foodSpotsPhotoRepository.findByHistoryIn(listOf(foodSpotsHistory))
                    result.size shouldBe 2
                }
            }

            context("사진이 존재하지 않는 history가 포함된 리스트를 받는 경우") {
                it("일치하는 FoodSpotsPhoto 리스트를 반환한다.") {
                    val result =
                        foodSpotsPhotoRepository.findByHistoryIn(
                            listOf(
                                foodSpotsHistory,
                                notExistPhotoFoodSpotsHistory,
                            ),
                        )
                    result.size shouldBe 2
                }
            }
        }

        describe("deleteByHistoryId 메소드는") {
            context("존재하는 report history 리스트를 받는 경우") {
                it("해당 historyId 에 해당하는 FoodSpotsPhoto 를 삭제한다.") {
                    foodSpotsPhotoRepository.findAll().size shouldBe 2
                    foodSpotsPhotoRepository.deleteAll(listOf(foodSpotsPhoto, otherFoodSpotsPhoto))
                    foodSpotsPhotoRepository.findAll().size shouldBe 0
                }
            }
        }

        describe("findOneByFoodSpots 메소드는") {
            context("존재하는 foodSpotsId 를 받는 경우") {
                it("해당 FoodSpotsPhoto 를 반환한다.") {
                    val result = foodSpotsPhotoRepository.findOneByFoodSpots(foodSpots.id)
                    result.shouldNotBeNull()
                }
            }

            context("존재하지 않는 foodSpotsId 를 받는 경우") {
                it("null 을 반환한다.") {
                    val result = foodSpotsPhotoRepository.findOneByFoodSpots(0L)
                    result.shouldBeNull()
                }
            }
        }
    })
