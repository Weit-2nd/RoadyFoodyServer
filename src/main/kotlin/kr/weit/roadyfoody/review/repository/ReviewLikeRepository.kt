package kr.weit.roadyfoody.review.repository

import kr.weit.roadyfoody.review.domain.ReviewLike
import kr.weit.roadyfoody.review.domain.ReviewLikeId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReviewLikeRepository : JpaRepository<ReviewLike, ReviewLikeId>
