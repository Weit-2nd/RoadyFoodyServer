package kr.weit.roadyfoody.foodSpots.application.service

import jakarta.persistence.EntityManager
import kr.weit.roadyfoody.common.exception.ErrorCode
import kr.weit.roadyfoody.common.exception.RoadyFoodyBadRequestException
import kr.weit.roadyfoody.foodSpots.application.dto.FoodSpotsUpdateRequest
import kr.weit.roadyfoody.foodSpots.application.dto.ReportRequest
import kr.weit.roadyfoody.foodSpots.application.service.event.ReportErrorCompensatingTxSync
import kr.weit.roadyfoody.foodSpots.domain.FoodSpots
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsFoodCategory
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsHistory
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsOperationHours
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsPhoto
import kr.weit.roadyfoody.foodSpots.domain.ReportFoodCategory
import kr.weit.roadyfoody.foodSpots.domain.ReportOperationHours
import kr.weit.roadyfoody.foodSpots.exception.AlreadyClosedFoodSpotsException
import kr.weit.roadyfoody.foodSpots.exception.NotFoodSpotsHistoriesOwnerException
import kr.weit.roadyfoody.foodSpots.exception.TooManyReportRequestException
import kr.weit.roadyfoody.foodSpots.repository.FoodCategoryRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSportsOperationHoursRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsFoodCategoryRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsHistoryRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsPhotoRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsRepository
import kr.weit.roadyfoody.foodSpots.repository.ReportFoodCategoryRepository
import kr.weit.roadyfoody.foodSpots.repository.ReportOperationHoursRepository
import kr.weit.roadyfoody.foodSpots.repository.getByFoodSpotsId
import kr.weit.roadyfoody.foodSpots.repository.getByHistoryId
import kr.weit.roadyfoody.foodSpots.repository.getFoodCategories
import kr.weit.roadyfoody.global.service.ImageService
import kr.weit.roadyfoody.global.utils.CoordinateUtils.Companion.createCoordinate
import kr.weit.roadyfoody.user.application.service.UserCommandService
import kr.weit.roadyfoody.user.domain.User
import org.redisson.api.RedissonClient
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronizationManager
import org.springframework.web.multipart.MultipartFile
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Date
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
    private val redisTemplate: RedisTemplate<String, String>,
) {
    companion object {
        private const val FOOD_SPOTS_OPEN_SCHEDULER_LOCK = "foodSpotsOpenSchedulerLock"
        private val FOOD_SPOTS_OPEN_SCHEDULER_LOCK_DURATION: Duration =
            Duration.ofHours(23) + Duration.ofMinutes(50)

        private const val FOOD_SPOTS_REPORT_LIMIT_PREFIX = "rofo:report-request-limit:"
        const val FOOD_SPOTS_REPORT_LIMIT_COUNT = 5

        fun getFoodSpotsReportCountKey(userId: Long) = "$FOOD_SPOTS_REPORT_LIMIT_PREFIX$userId"
    }

    @Transactional
    fun createReport(
        user: User,
        reportRequest: ReportRequest,
        photos: List<MultipartFile>?,
    ) {
        val key = getFoodSpotsReportCountKey(user.id)
        check(incrementAndCheckReportCount(key)) {
            throw TooManyReportRequestException()
        }

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

        userCommandService.increaseCoin(user.id, foodSpotsHistory.reportType.reportReward.point)

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
        val key = getFoodSpotsReportCountKey(user.id)
        check(incrementAndCheckReportCount(key)) {
            throw TooManyReportRequestException()
        }

        val foodSpots = foodSpotsRepository.getByFoodSpotsId(foodSpotsId)

        if (request.closed != null && request.closed && foodSpots.storeClosure) {
            throw AlreadyClosedFoodSpotsException()
        }

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

        userCommandService.increaseCoin(user.id, foodSpotsHistory.reportType.reportReward.point)
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

    private fun incrementAndCheckReportCount(key: String): Boolean {
        // redis 내에 존재하지 않을 시 1L 반환
        val count = redisTemplate.opsForValue().increment(key)!!

        TransactionSynchronizationManager.registerSynchronization(
            ReportErrorCompensatingTxSync(key, redisTemplate),
        )

        if (count > FOOD_SPOTS_REPORT_LIMIT_COUNT) {
            return false
        }
        if (count == 1L) {
            val tomorrowMidnight = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIDNIGHT)
            val expirationDate = Date.from(tomorrowMidnight.atZone(ZoneId.systemDefault()).toInstant())
            redisTemplate.expireAt(key, expirationDate)
        }
        return true
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

    @Transactional
    fun deleteFoodSpotsHistories(
        user: User,
        historyId: Long,
    ) {
        val foodSpotsHistory = foodSpotsHistoryRepository.getByHistoryId(historyId)
        if (foodSpotsHistory.user.id != user.id) {
            throw NotFoodSpotsHistoriesOwnerException("해당 음식점 리포트의 소유자가 아닙니다.")
        }

        val categories = reportFoodCategoryRepository.getByHistoryId(historyId)
        reportFoodCategoryRepository.deleteAll(categories)

        val operationHours = reportOperationHoursRepository.getByHistoryId(historyId)
        reportOperationHoursRepository.deleteAll(operationHours)

        val photos = foodSpotsPhotoRepository.getByHistoryId(historyId)
        foodSpotsPhotoRepository.deleteAll(photos)

        foodSpotsHistoryRepository.deleteById(historyId)

        entityManager.flush()

        userCommandService.decreaseCoin(user.id, foodSpotsHistory.reportType.reportReward.point)

        photos
            .map {
                CompletableFuture.supplyAsync({
                    imageService.remove(it.fileName)
                }, executor)
            }.forEach { it.join() }
    }
}
