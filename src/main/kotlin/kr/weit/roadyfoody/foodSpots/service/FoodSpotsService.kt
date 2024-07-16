package kr.weit.roadyfoody.foodSpots.service

import kr.weit.roadyfoody.common.dto.SliceResponse
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsFoodCategory
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsPhoto
import kr.weit.roadyfoody.foodSpots.domain.ReportFoodCategory
import kr.weit.roadyfoody.foodSpots.dto.ReportCategoryResponse
import kr.weit.roadyfoody.foodSpots.dto.ReportHistoriesResponse
import kr.weit.roadyfoody.foodSpots.dto.ReportPhotoResponse
import kr.weit.roadyfoody.foodSpots.dto.ReportRequest
import kr.weit.roadyfoody.foodSpots.repository.FoodCategoryRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSportsOperationHoursRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsFoodCategoryRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsHistoryRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsPhotoRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsRepository
import kr.weit.roadyfoody.foodSpots.repository.ReportFoodCategoryRepository
import kr.weit.roadyfoody.foodSpots.repository.ReportOperationHoursRepository
import kr.weit.roadyfoody.foodSpots.repository.getByHistoryId
import kr.weit.roadyfoody.foodSpots.repository.getFoodCategories
import kr.weit.roadyfoody.foodSpots.repository.getHistoriesByUser
import kr.weit.roadyfoody.global.service.ImageService
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.repository.UserRepository
import kr.weit.roadyfoody.user.repository.getByUserId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService

@Service
class FoodSpotsService(
    private val foodSpotsRepository: FoodSpotsRepository,
    private val foodSpotsHistoryRepository: FoodSpotsHistoryRepository,
    private val foodSpotsPhotoRepository: FoodSpotsPhotoRepository,
    private val userRepository: UserRepository,
    private val reportOperationHoursRepository: ReportOperationHoursRepository,
    private val foodSportsOperationHoursRepository: FoodSportsOperationHoursRepository,
    private val foodCategoryRepository: FoodCategoryRepository,
    private val reportFoodCategoryRepository: ReportFoodCategoryRepository,
    private val foodSpotsCategoryRepository: FoodSpotsFoodCategoryRepository,
    private val imageService: ImageService,
    private val executor: ExecutorService,
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
        reportOperationHoursRepository.saveAll(reportRequest.toReportOperationHoursEntity(foodStoreHistory))
        foodSportsOperationHoursRepository.saveAll(reportRequest.toOperationHoursEntity(foodSpotsInfo))
        reportFoodCategoryRepository.saveAll(foodCategories.map { ReportFoodCategory(foodStoreHistory, it) })
        foodSpotsCategoryRepository.saveAll(foodCategories.map { FoodSpotsFoodCategory(foodSpotsInfo, it) })

        photos?.let {
            val generatorPhotoNameMap = photos.associateBy { imageService.generateImageName(it) }

            generatorPhotoNameMap
                .map {
                    FoodSpotsPhoto.of(foodStoreHistory, it.key)
                }.also { foodSpotsPhotoRepository.saveAll(it) }

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
    }

    @Transactional(readOnly = true)
    fun getReportHistories(
        userId: Long,
        size: Int,
        lastId: Long?,
    ): SliceResponse<ReportHistoriesResponse> {
        val user = userRepository.getByUserId(userId)
        val reportResponse =
            foodSpotsHistoryRepository.getHistoriesByUser(user, size, lastId).map {
                val reportPhotoResponse =
                    foodSpotsPhotoRepository.getByHistoryId(it.id).map { photo ->
                        ReportPhotoResponse(photo, imageService.getDownloadUrl(photo.fileName))
                    }
                val reportCategoryResponse =
                    reportFoodCategoryRepository.getByHistoryId(it.id).map { category ->
                        ReportCategoryResponse(category)
                    }
                ReportHistoriesResponse(it, reportPhotoResponse, reportCategoryResponse)
            }
        return SliceResponse(reportResponse)
    }
}
