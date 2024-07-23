package kr.weit.roadyfoody.foodSpots.application.service

import jakarta.persistence.EntityManager
import kr.weit.roadyfoody.foodSpots.application.dto.ReportRequest
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsFoodCategory
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsPhoto
import kr.weit.roadyfoody.foodSpots.domain.ReportFoodCategory
import kr.weit.roadyfoody.foodSpots.repository.FoodCategoryRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSportsOperationHoursRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsFoodCategoryRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsHistoryRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsPhotoRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsRepository
import kr.weit.roadyfoody.foodSpots.repository.ReportFoodCategoryRepository
import kr.weit.roadyfoody.foodSpots.repository.ReportOperationHoursRepository
import kr.weit.roadyfoody.foodSpots.repository.getFoodCategories
import kr.weit.roadyfoody.global.service.ImageService
import kr.weit.roadyfoody.mission.domain.RewardPoint
import kr.weit.roadyfoody.user.application.UserCommandService
import kr.weit.roadyfoody.user.domain.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService

@Service
class FoodSpotsCommandService(
    private val foodSpotsRepository: FoodSpotsRepository,
    private val foodSpotsHistoryRepository: FoodSpotsHistoryRepository,
    private val foodSpotsPhotoRepository: FoodSpotsPhotoRepository,
    private val reportOperationHoursRepository: ReportOperationHoursRepository,
    private val foodSportsOperationHoursRepository: FoodSportsOperationHoursRepository,
    private val foodCategoryRepository: FoodCategoryRepository,
    private val reportFoodCategoryRepository: ReportFoodCategoryRepository,
    private val foodSpotsCategoryRepository: FoodSpotsFoodCategoryRepository,
    private val imageService: ImageService,
    private val executor: ExecutorService,
    private val userCommandService: UserCommandService,
    private val entityManager: EntityManager,
) {
    @Transactional
    fun createReport(
        user: User,
        reportRequest: ReportRequest,
        photos: List<MultipartFile>?,
    ) {
        val foodSpotsInfo = reportRequest.toFoodSpotsEntity()
        foodSpotsRepository.save(foodSpotsInfo)
        val foodStoreHistory = reportRequest.toFoodSpotsHistoryEntity(foodSpotsInfo, user)
        foodSpotsHistoryRepository.save(foodStoreHistory)
        val foodCategories = foodCategoryRepository.getFoodCategories(reportRequest.foodCategories)
        reportOperationHoursRepository.saveAll(
            reportRequest.toReportOperationHoursEntity(
                foodStoreHistory,
            ),
        )
        foodSportsOperationHoursRepository.saveAll(
            reportRequest.toOperationHoursEntity(
                foodSpotsInfo,
            ),
        )
        reportFoodCategoryRepository.saveAll(
            foodCategories.map {
                ReportFoodCategory(
                    foodStoreHistory,
                    it,
                )
            },
        )
        foodSpotsCategoryRepository.saveAll(
            foodCategories.map {
                FoodSpotsFoodCategory(
                    foodSpotsInfo,
                    it,
                )
            },
        )

        val generatorPhotoNameMap = photos?.associateBy { imageService.generateImageName(it) } ?: emptyMap()

        generatorPhotoNameMap
            .map {
                FoodSpotsPhoto.of(foodStoreHistory, it.key)
            }.also { foodSpotsPhotoRepository.saveAll(it) }

        entityManager.flush()
        userCommandService.increaseCoin(user.id, RewardPoint.FIRST_REPORT.point)

        generatorPhotoNameMap
            .map {
                CompletableFuture.supplyAsync({
                    imageService.upload(
                        it.key,
                        it.value,
                    )
                }, executor)
            }.forEach { it.join() }
    }

    @Transactional
    fun deleteWithdrawUserReport(user: User) {
        foodSpotsHistoryRepository.findByUser(user).also {
            if (it.isNotEmpty()) {
                reportOperationHoursRepository.deleteByFoodSpotsHistoryIn(it)
                reportFoodCategoryRepository.deleteByFoodSpotsHistoryIn(it)
                val photo =
                    foodSpotsPhotoRepository
                        .findByHistoryIn(it)
                        .onEach { photo -> imageService.remove(photo.fileName) }
                foodSpotsPhotoRepository.deleteAll(photo)
                foodSpotsHistoryRepository.deleteAll(it)
            }
        }
    }
}
