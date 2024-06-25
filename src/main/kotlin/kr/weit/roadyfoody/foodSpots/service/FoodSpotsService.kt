package kr.weit.roadyfoody.foodSpots.service

import jakarta.transaction.Transactional
import kr.weit.roadyfoody.foodSpots.dto.ReportRequest
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsHistoryRepository
import kr.weit.roadyfoody.foodSpots.repository.FoodSpotsRepository
import kr.weit.roadyfoody.global.service.ImageService
import kr.weit.roadyfoody.user.repository.UserRepository
import kr.weit.roadyfoody.user.repository.getByUserId
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.concurrent.CompletableFuture

@Service
class FoodSpotsService(
    private val foodSpotsRepository: FoodSpotsRepository,
    private val foodSpotsHistoryRepository: FoodSpotsHistoryRepository,
    private val userRepository: UserRepository,
    private val imageService: ImageService,
) {
    @Transactional
    fun createReport(
        userId: Long,
        reportRequest: ReportRequest,
        photos: List<MultipartFile>,
    ) {
        val user = userRepository.getByUserId(userId)
        val foodStoreInfo = reportRequest.toFoodSpotsEntity()
        foodSpotsRepository.save(foodStoreInfo)
        val foodStoreHistory = reportRequest.toFoodSpotsHistoryEntity(foodStoreInfo, user)
        foodSpotsHistoryRepository.save(foodStoreHistory)
        photos.map { CompletableFuture.supplyAsync { imageService.upload(imageService.generateImageName(it), it) } }.forEach { it.join() }
    }
}
