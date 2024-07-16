package kr.weit.roadyfoody.review.repository

import kr.weit.roadyfoody.review.domain.FoodSpotsReviewPhoto
import org.springframework.data.jpa.repository.JpaRepository

interface FoodSpotsReviewPhotoRepository : JpaRepository<FoodSpotsReviewPhoto, Long>
