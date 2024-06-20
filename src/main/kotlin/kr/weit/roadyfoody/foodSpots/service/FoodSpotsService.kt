package kr.weit.roadyfoody.foodSpots.service

import kr.weit.roadyfoody.foodSpots.dto.ReportHistoriesResponse
import kr.weit.roadyfoody.foodSpots.dto.ReportRequest
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsHistoryRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsHistorySortType
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsRepository
import kr.weit.roadyfoody.global.dto.SliceResponse
import kr.weit.roadyfoody.user.exception.UserNotFoundException
import kr.weit.roadyfoody.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class FoodSpotsService(
    private val foodSpotsRepository: FoodSpotsRepository,
    private val foodSpotsHistoryRepository: FoodSpotsHistoryRepository,
    private val userRepository: UserRepository,
) {
    fun createReport(
        userId: Long,
        reportRequest: ReportRequest,
        photos: List<MultipartFile>,
    ) {
        val foodStoreInfo = reportRequest.toFoodSpotsEntity()
        foodSpotsRepository.save(foodStoreInfo)
        val user = userRepository.findById(userId).orElseThrow { throw UserNotFoundException("$userId ID 의 사용자는 존재하지 않습니다.") }
        val foodStoreHistory =
            reportRequest.toFoodSpotsHistoryEntity(foodStoreInfo, user)
        foodSpotsHistoryRepository.save(foodStoreHistory)
    }

    fun getFoodSpots(
        userId: Long,
        size: Int,
        sortType: FoodSpotsHistorySortType,
        lastId: Long?,
    ): SliceResponse<ReportHistoriesResponse> {
        val user = userRepository.findById(userId).orElseThrow { throw UserNotFoundException("$userId ID 의 사용자는 존재하지 않습니다.") }
        val reportResponse =
            foodSpotsHistoryRepository
                .findSliceByUserOrderBySortType(
                    user,
                    size,
                    sortType,
                    lastId,
                ).map { ReportHistoriesResponse(it) }
        return SliceResponse(size, reportResponse)
    }
}
