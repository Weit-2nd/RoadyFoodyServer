package kr.weit.roadyfoody.foodSpots.application.service

import jakarta.persistence.EntityManager
import kr.weit.roadyfoody.common.exception.ErrorCode
import kr.weit.roadyfoody.common.exception.RoadyFoodyBadRequestException
import kr.weit.roadyfoody.foodSpots.application.dto.FoodSpotsUpdateRequest
import kr.weit.roadyfoody.foodSpots.application.dto.ReportRequest
import kr.weit.roadyfoody.foodSpots.domain.FoodSpots
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsFoodCategory
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsHistory
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsOperationHours
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsPhoto
import kr.weit.roadyfoody.foodSpots.domain.ReportFoodCategory
import kr.weit.roadyfoody.foodSpots.domain.ReportOperationHours
import kr.weit.roadyfoody.foodSpots.repository.FoodCategoryRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSportsOperationHoursRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsFoodCategoryRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsHistoryRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsPhotoRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsRepository
import kr.weit.roadyfoody.foodSpots.repository.ReportFoodCategoryRepository
import kr.weit.roadyfoody.foodSpots.repository.ReportOperationHoursRepository
import kr.weit.roadyfoody.foodSpots.repository.getByFoodSpotsId
import kr.weit.roadyfoody.foodSpots.repository.getFoodCategories
import kr.weit.roadyfoody.global.service.ImageService
import kr.weit.roadyfoody.global.utils.CoordinateUtils.Companion.createCoordinate
import kr.weit.roadyfoody.mission.domain.RewardPoint
import kr.weit.roadyfoody.user.application.UserCommandService
import kr.weit.roadyfoody.user.domain.User
import org.redisson.api.RedissonClient
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.Duration
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
    private val redissonClient: RedissonClient,
) {
    companion object {
        private const val FOOD_SPOTS_OPEN_SCHEDULER_LOCK = "foodSpotsOpenSchedulerLock"
        private val FOOD_SPOTS_OPEN_SCHEDULER_LOCK_DURATION: Duration =
            Duration.ofHours(23) + Duration.ofMinutes(50)
    }

    @Transactional
    fun createReport(
        user: User,
        reportRequest: ReportRequest,
        photos: List<MultipartFile>?,
    ) {
        val foodSpots = reportRequest.toFoodSpotsEntity()
        storeFoodSpots(
            foodSpots,
            reportRequest.foodCategories,
            reportRequest.toOperationHoursEntity(foodSpots),
        )

        val foodSpotsHistory = reportRequest.toFoodSpotsHistoryEntity(foodSpots, user)
        storeReport(
            foodSpotsHistory,
            reportRequest.foodCategories,
            reportRequest.toReportOperationHoursEntity(foodSpotsHistory),
        )

        val generatorPhotoNameMap =
            photos?.associateBy { imageService.generateImageName(it) } ?: emptyMap()

        generatorPhotoNameMap
            .map {
                FoodSpotsPhoto.of(foodSpotsHistory, it.key)
            }.also { foodSpotsPhotoRepository.saveAll(it) }

        entityManager.flush()

        userCommandService.increaseCoin(user.id, RewardPoint.FIRST_REPORT.point)

        generatorPhotoNameMap
            .map {
                CompletableFuture.supplyAsync({
                    imageService.upload(it.key, it.value)
                }, executor)
            }.forEach { it.join() }
    }

    private fun storeFoodSpots(
        foodSpots: FoodSpots,
        foodCategoryIds: Set<Long>,
        operationHoursList: List<FoodSpotsOperationHours>,
    ): FoodSpots {
        foodSpotsRepository.save(foodSpots)

        val foodCategories = foodCategoryRepository.getFoodCategories(foodCategoryIds)
        foodSpotsCategoryRepository.saveAll(
            foodCategories.map { FoodSpotsFoodCategory(foodSpots, it) },
        )

        foodSportsOperationHoursRepository.saveAll(operationHoursList)
        return foodSpots
    }

    @Transactional
    fun doUpdateReport(
        user: User,
        foodSpotsId: Long,
        request: FoodSpotsUpdateRequest,
    ) {
        val foodSpots = foodSpotsRepository.getByFoodSpotsId(foodSpotsId)

        val changed =
            run {
                val foodSpotsUpdated = updateFoodSpots(foodSpots, request)
                val categoriesUpdated = updateFoodSpotsCategories(foodSpots, request)
                val operationHoursUpdated = updateFoodSpotsOperationHours(foodSpots, request)
                foodSpotsUpdated || categoriesUpdated || operationHoursUpdated
            }

        if (!changed) {
            throw RoadyFoodyBadRequestException(ErrorCode.INVALID_CHANGE_VALUE)
        }

        val foodSpotsHistory = request.toFoodSpotsHistoryEntity(foodSpots, user)
        storeReport(
            foodSpotsHistory,
            // 카테고리나 운영시간을 미기재할 시 기존 FoodSpots 의 값을 그대로 사용
            request.foodCategories ?: foodSpots.foodCategoryList.map { it.foodCategory.id }.toSet(),
            request.toReportOperationHoursEntity(foodSpotsHistory)
                ?: foodSpots.operationHoursList.map {
                    ReportOperationHours(foodSpotsHistory, it.dayOfWeek, it.openingHours, it.closingHours)
                },
        )

        entityManager.flush()

        if (request.closed != null && request.closed) {
            userCommandService.increaseCoin(user.id, RewardPoint.CLOSED_REPORT.point)
        } else {
            userCommandService.increaseCoin(user.id, RewardPoint.REPORT.point)
        }
    }

    private fun storeReport(
        foodStoreHistory: FoodSpotsHistory,
        foodCategoryIds: Set<Long>,
        operationHoursList: List<ReportOperationHours>,
    ): FoodSpotsHistory {
        foodSpotsHistoryRepository.save(foodStoreHistory)

        val foodCategories = foodCategoryRepository.getFoodCategories(foodCategoryIds)
        reportFoodCategoryRepository.saveAll(
            foodCategories.map { ReportFoodCategory(foodStoreHistory, it) },
        )

        reportOperationHoursRepository.saveAll(operationHoursList)

        return foodStoreHistory
    }

    private fun updateFoodSpots(
        foodSpots: FoodSpots,
        request: FoodSpotsUpdateRequest,
    ): Boolean {
        var changed = false
        if (request.name != null && request.name != foodSpots.name) {
            foodSpots.name = request.name
            changed = true
        }
        if (request.longitude != null && request.latitude != null) {
            val point = createCoordinate(request.longitude, request.latitude)
            if (point != foodSpots.point) {
                foodSpots.point = point
                changed = true
            }
        }
        if (request.open != null && request.open != foodSpots.open) {
            foodSpots.open = request.open
            changed = true
        }
        if (request.closed != null && request.closed != foodSpots.storeClosure) {
            foodSpots.storeClosure = request.closed
            changed = true
        }
        return changed
    }

    private fun updateFoodSpotsCategories(
        foodSpots: FoodSpots,
        request: FoodSpotsUpdateRequest,
    ): Boolean {
        if (request.foodCategories == null) {
            return false
        }

        val currentFoodCategoryIds = foodSpots.foodCategoryList.map { it.foodCategory.id }.toSet()
        val newFoodCategoryIds = request.foodCategories

        val categoryIdsToRemove = currentFoodCategoryIds subtract newFoodCategoryIds
        val foodSpotsFoodCategoryToRemove = foodSpots.foodCategoryList.filter { it.foodCategory.id in categoryIdsToRemove }
        foodSpotsCategoryRepository.deleteAll(foodSpotsFoodCategoryToRemove)

        val categoryIdsToAdd = newFoodCategoryIds subtract currentFoodCategoryIds
        val foodCategoriesToAdd =
            if (categoryIdsToAdd.isNotEmpty()) {
                foodCategoryRepository.getFoodCategories(categoryIdsToAdd)
            } else {
                emptyList()
            }

        val foodSpotsFoodCategoriesToAdd = foodCategoriesToAdd.map { FoodSpotsFoodCategory(foodSpots, it) }
        foodSpotsCategoryRepository.saveAll(foodSpotsFoodCategoriesToAdd)

        val changed = categoryIdsToRemove.isNotEmpty() || categoryIdsToAdd.isNotEmpty()
        return changed
    }

    private fun updateFoodSpotsOperationHours(
        foodSpots: FoodSpots,
        request: FoodSpotsUpdateRequest,
    ): Boolean {
        val currentFoodSpotsOperationHours = foodSpots.operationHoursList.toSet()
        val newFoodSpotsOperationHours = request.toOperationHoursEntity(foodSpots) ?: return false

        val foodSpotsOperationHoursToRemove = currentFoodSpotsOperationHours subtract newFoodSpotsOperationHours
        foodSportsOperationHoursRepository.deleteAll(foodSpotsOperationHoursToRemove)

        val foodSpotsOperationHoursToAdd = newFoodSpotsOperationHours subtract currentFoodSpotsOperationHours
        foodSportsOperationHoursRepository.saveAll(foodSpotsOperationHoursToAdd)

        val changed = foodSpotsOperationHoursToRemove.isNotEmpty() || foodSpotsOperationHoursToAdd.isNotEmpty()
        return changed
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

    @Scheduled(cron = "0 0 0 * * *")
    fun setFoodSpotsOpen() {
        if (redissonClient
                .getBucket<String>(
                    FOOD_SPOTS_OPEN_SCHEDULER_LOCK,
                ).setIfAbsent(
                    FOOD_SPOTS_OPEN_SCHEDULER_LOCK,
                    FOOD_SPOTS_OPEN_SCHEDULER_LOCK_DURATION,
                )
        ) {
            foodSpotsRepository.updateOpeningStatus()
        }
    }
}
