package kr.weit.roadyfoody.foodSpots.service

import kr.weit.roadyfoody.common.dto.SliceResponse
import kr.weit.roadyfoody.foodSpots.dto.ReportCategoryResponse
import kr.weit.roadyfoody.foodSpots.dto.ReportHistoriesResponse
import kr.weit.roadyfoody.foodSpots.dto.ReportPhotoResponse
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsHistoryRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsPhotoRepository
import kr.weit.roadyfoody.foodSpots.repository.ReportFoodCategoryRepository
import kr.weit.roadyfoody.foodSpots.repository.getByHistoryId
import kr.weit.roadyfoody.foodSpots.repository.getHistoriesByUser
import kr.weit.roadyfoody.global.service.ImageService
import kr.weit.roadyfoody.user.repository.UserRepository
import kr.weit.roadyfoody.user.repository.getByUserId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FoodSpotsQueryService(
    private val userRepository: UserRepository,
    private val foodSpotsHistoryRepository: FoodSpotsHistoryRepository,
    private val foodSpotsPhotoRepository: FoodSpotsPhotoRepository,
    private val reportFoodCategoryRepository: ReportFoodCategoryRepository,
    private val imageService: ImageService,
) {
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
