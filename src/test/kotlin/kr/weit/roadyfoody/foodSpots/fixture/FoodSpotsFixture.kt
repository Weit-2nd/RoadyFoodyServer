package kr.weit.roadyfoody.foodSpots.fixture

import createMockSliceReview
import createTestReviewPhotoResponse
import kr.weit.roadyfoody.common.dto.SliceResponse
import kr.weit.roadyfoody.foodSpots.application.dto.FoodCategoryResponse
import kr.weit.roadyfoody.foodSpots.application.dto.FoodSpotsDetailResponse
import kr.weit.roadyfoody.foodSpots.application.dto.FoodSpotsReviewResponse
import kr.weit.roadyfoody.foodSpots.application.dto.FoodSpotsUpdateRequest
import kr.weit.roadyfoody.foodSpots.application.dto.OperationHoursRequest
import kr.weit.roadyfoody.foodSpots.application.dto.ReportCategoryResponse
import kr.weit.roadyfoody.foodSpots.application.dto.ReportHistoryDetailResponse
import kr.weit.roadyfoody.foodSpots.application.dto.ReportOperationHoursResponse
import kr.weit.roadyfoody.foodSpots.application.dto.ReportPhotoResponse
import kr.weit.roadyfoody.foodSpots.application.dto.ReportRequest
import kr.weit.roadyfoody.foodSpots.domain.DayOfWeek
import kr.weit.roadyfoody.foodSpots.domain.FoodCategory
import kr.weit.roadyfoody.foodSpots.domain.FoodSpots
import kr.weit.roadyfoody.foodSpots.domain.FoodSpots.Companion.SRID_WGS84
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsFoodCategory
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsHistory
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsOperationHours
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsPhoto
import kr.weit.roadyfoody.foodSpots.domain.ReportFoodCategory
import kr.weit.roadyfoody.foodSpots.domain.ReportOperationHours
import kr.weit.roadyfoody.foodSpots.domain.ReportType
import kr.weit.roadyfoody.foodSpots.utils.FOOD_SPOTS_NAME_MAX_LENGTH
import kr.weit.roadyfoody.global.utils.CoordinateUtils
import kr.weit.roadyfoody.search.foodSpots.domain.SearchCoinCache
import kr.weit.roadyfoody.search.foodSpots.dto.FoodSpotsSearchResponse
import kr.weit.roadyfoody.search.foodSpots.dto.FoodSpotsSearchResponses
import kr.weit.roadyfoody.search.foodSpots.dto.OperationStatus
import kr.weit.roadyfoody.support.utils.ImageFormat
import kr.weit.roadyfoody.support.utils.createTestImageFile
import kr.weit.roadyfoody.user.domain.User
import kr.weit.roadyfoody.user.fixture.createTestReviewerInfoResponse
import kr.weit.roadyfoody.user.fixture.createTestUser
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

const val TEST_FOOD_SPOT_NAME = "testFoodSpot"
const val TEST_FOOD_SPOT_NAME_EMPTY = ""
val TEST_FOOD_SPOT_NAME_TOO_LONG = "a".repeat(FOOD_SPOTS_NAME_MAX_LENGTH + 1)
const val TEST_FOOD_SPOT_NAME_INVALID_STR = "testFoodSpot|"
const val TEST_FOOD_SPOT_LATITUDE = 37.123456
const val TEST_FOOD_SPOT_LONGITUDE = 127.123456
val TEST_FOOD_SPOT_POINT: Point =
    GeometryFactory().createPoint(Coordinate(TEST_FOOD_SPOT_LONGITUDE, TEST_FOOD_SPOT_LATITUDE)).also { it.srid = SRID_WGS84 }
const val TEST_FOOD_SPOT_FOOD_TRUCK = true
const val TEST_FOOD_SPOT_OPEN = true
const val TEST_FOOD_SPOT_STORE_CLOSURE = false
const val TEST_PHOTO_NAME = "test_photo_name"
const val TEST_FOOD_SPOTS_REQUEST_NAME = "reportRequest"
const val TEST_FOOD_SPOTS_REQUEST_PHOTO = "reportPhotos"
const val TEST_FOOD_SPOTS_SIZE = 10
const val TEST_FOOD_SPOTS_LAST_ID = 1L
const val TEST_FOOD_SPOTS_PHOTO_URL = "test_url"
const val TEST_FOOD_SPOTS_HAS_NEXT = false
const val TEST_INVALID_FOOD_CATEGORY_ID = -1L
const val TEST_FOOD_SPOTS_CATEGORY_ID = 1L
const val TEST_OPERATION_HOURS_OPEN = "00:00"
const val TEST_OPERATION_HOURS_CLOSE = "23:59"
const val TEST_INVALID_TIME_FORMAT = "25:60"
const val TEST_CATEGORY_NAME = "붕어빵"
const val TEST_NEW_CATEGORY_NAME = "백반"
const val TEST_UPDATE_FOOD_SPOT_NAME = "updateFoodSpot"
const val TEST_UPDATE_FOOD_SPOT_LATITUDE = 11.1111111
const val TEST_UPDATE_FOOD_SPOT_LONGITUDE = 11.2222222
const val TEST_UPDATE_OPERATION_HOURS_OPEN = "10:00"
const val TEST_UPDATE_OPERATION_HOURS_CLOSE = "13:59"
const val TEST_FOOD_SPOTS_HISTORY_ID = 1L
const val TEST_INVALID_FOOD_SPOTS_HISTORY_ID = -1L
const val TEST_REST_DAILY_REPORT_CREATION_COUNT = 5

fun createMockTestFoodSpot(
    id: Long = 0L,
    name: String = TEST_FOOD_SPOT_NAME,
    foodTruck: Boolean = TEST_FOOD_SPOT_FOOD_TRUCK,
    open: Boolean = TEST_FOOD_SPOT_OPEN,
    storeClosure: Boolean = TEST_FOOD_SPOT_STORE_CLOSURE,
    point: Point = TEST_FOOD_SPOT_POINT,
    operationHours: MutableList<FoodSpotsOperationHours> = mutableListOf(createTestFoodOperationHours()),
    foodCategories: MutableList<FoodSpotsFoodCategory> = createTestFoodSpotsFoodCategories(foodSpotsSize = 1),
) = MockTestFoodSpot(id, name, foodTruck, open, storeClosure, point, operationHours, foodCategories)

fun createMockTestFoodHistory(
    user: User = createTestUser(0L),
    foodSpots: FoodSpots = createMockTestFoodSpot(),
) = MockTestFoodSpotsHistory(user = user, foodSpots = foodSpots)

fun createMockSliceFoodHistory(): Slice<FoodSpotsHistory> =
    SliceImpl(
        listOf(createMockTestFoodHistory()),
        Pageable.ofSize(TEST_FOOD_SPOTS_SIZE),
        TEST_FOOD_SPOTS_HAS_NEXT,
    )

fun createTestFoodSpots(
    id: Long = 0L,
    name: String = TEST_FOOD_SPOT_NAME,
    foodTruck: Boolean = TEST_FOOD_SPOT_FOOD_TRUCK,
    open: Boolean = TEST_FOOD_SPOT_OPEN,
    storeClosure: Boolean = TEST_FOOD_SPOT_STORE_CLOSURE,
    point: Point = TEST_FOOD_SPOT_POINT,
    operationHours: MutableList<FoodSpotsOperationHours> = mutableListOf(),
    foodCategories: MutableList<FoodSpotsFoodCategory> = mutableListOf(),
) = FoodSpots(id, name, foodTruck, open, storeClosure, point, operationHours, foodCategories)

fun createTestFoodCategory(
    id: Long = 0L,
    name: String = "Category 1",
) = FoodCategory(id, name)

fun createTestFoodHistory(
    id: Long = 0L,
    foodSpots: FoodSpots = createTestFoodSpots(0L),
    user: User = createTestUser(0L),
    name: String = TEST_FOOD_SPOT_NAME,
    foodTruck: Boolean = TEST_FOOD_SPOT_FOOD_TRUCK,
    open: Boolean = TEST_FOOD_SPOT_OPEN,
    storeClosure: Boolean = TEST_FOOD_SPOT_STORE_CLOSURE,
    point: Point = TEST_FOOD_SPOT_POINT,
    reportType: ReportType = ReportType.STORE_CREATE,
    operationHours: List<ReportOperationHours> = listOf(),
    foodCategories: List<ReportFoodCategory> = listOf(),
) = FoodSpotsHistory(id, foodSpots, user, name, foodTruck, open, storeClosure, point, reportType, operationHours, foodCategories)

fun createTestFoodSpotsPhoto(foodSpotsHistory: FoodSpotsHistory = createTestFoodHistory()) =
    FoodSpotsPhoto(0L, foodSpotsHistory, TEST_PHOTO_NAME)

fun createTestReportRequest(
    name: String = TEST_FOOD_SPOT_NAME,
    longitude: Double = TEST_FOOD_SPOT_LONGITUDE,
    latitude: Double = TEST_FOOD_SPOT_LATITUDE,
    foodTruck: Boolean = TEST_FOOD_SPOT_FOOD_TRUCK,
    open: Boolean = TEST_FOOD_SPOT_OPEN,
    storeClosure: Boolean = TEST_FOOD_SPOT_STORE_CLOSURE,
    foodCategories: Set<Long> = setOf(TEST_FOOD_SPOTS_CATEGORY_ID),
    operationHours: List<OperationHoursRequest> = listOf(createOperationHoursRequest()),
) = ReportRequest(
    name,
    longitude,
    latitude,
    foodTruck,
    open,
    storeClosure,
    foodCategories,
    operationHours,
)

fun createMockPhotoList(
    format: ImageFormat,
    name: String = TEST_PHOTO_NAME,
    size: Int = 2,
): List<MultipartFile> =
    List(size) {
        createTestImageFile(format, name)
    }

fun createTestReportPhotoResponse(
    id: Long = 0L,
    url: String = TEST_FOOD_SPOTS_PHOTO_URL,
) = ReportPhotoResponse(id, url)

fun createTestReportCategoryResponse(
    id: Long = 0L,
    name: String = "testCategory",
) = ReportCategoryResponse(id, name)

fun createTestFoodCategories(): List<FoodCategory> =
    listOf(
        createTestFoodCategory(1L, "포장마차"),
        createTestFoodCategory(2L, "붕어빵"),
        createTestFoodCategory(3L, "고기"),
        createTestFoodCategory(4L, "술"),
    )

fun createTestFoodSpotsFoodCategories(foodSpotsSize: Int = 3): MutableList<FoodSpotsFoodCategory> =
    (1L..foodSpotsSize)
        .flatMap { foodSpotsIndex ->
            createTestFoodCategories().map { foodCategory ->
                createTestFoodSpotsFoodCategory(createTestFoodSpots(foodSpotsIndex), foodCategory)
            }
        }.toMutableList()

fun createFoodSpotsForDistance(): List<FoodSpots> =
    listOf(
        createTestFoodSpots(
            name = "Food Spot 1 - 100m",
            point = CoordinateUtils.createCoordinate(TEST_FOOD_SPOT_LONGITUDE + 0.001, TEST_FOOD_SPOT_LATITUDE),
        ),
        createTestFoodSpots(
            name = "Food Spot 2 - 300m",
            point = CoordinateUtils.createCoordinate(TEST_FOOD_SPOT_LONGITUDE, TEST_FOOD_SPOT_LATITUDE + 0.003),
        ),
        createTestFoodSpots(
            name = "Food Spot 3 - 500m",
            point = CoordinateUtils.createCoordinate(TEST_FOOD_SPOT_LONGITUDE + 0.005, TEST_FOOD_SPOT_LATITUDE),
        ),
        createTestFoodSpots(
            name = "Food Spot 4 - 800m",
            point = CoordinateUtils.createCoordinate(TEST_FOOD_SPOT_LONGITUDE - 0.008, TEST_FOOD_SPOT_LATITUDE),
        ),
        createTestFoodSpots(
            name = "Food Spot 5 - 1km",
            point = CoordinateUtils.createCoordinate(TEST_FOOD_SPOT_LONGITUDE, TEST_FOOD_SPOT_LATITUDE + 0.01),
        ),
        createTestFoodSpots(
            name = "Food Spot 6 - 1km",
            open = false,
            point =
                CoordinateUtils.createCoordinate(
                    TEST_FOOD_SPOT_LONGITUDE,
                    TEST_FOOD_SPOT_LATITUDE + 0.01,
                ),
        ),
        createTestFoodSpots(
            name = "Food Spot 7 - 1km",
            open = false,
            point =
                CoordinateUtils.createCoordinate(
                    TEST_FOOD_SPOT_LONGITUDE,
                    TEST_FOOD_SPOT_LATITUDE + 0.01,
                ),
        ),
    )

fun createTestFoodSpotsForDistance(): List<FoodSpots> =
    listOf(
        MockTestFoodSpot(
            name = "Food Spot 1 - 100m",
            point = CoordinateUtils.createCoordinate(TEST_FOOD_SPOT_LONGITUDE + 0.001, TEST_FOOD_SPOT_LATITUDE),
        ),
        MockTestFoodSpot(
            name = "Food Spot 2 - 300m",
            point = CoordinateUtils.createCoordinate(TEST_FOOD_SPOT_LONGITUDE, TEST_FOOD_SPOT_LATITUDE + 0.003),
        ),
        MockTestFoodSpot(
            name = "Food Spot 3 - 500m",
            point = CoordinateUtils.createCoordinate(TEST_FOOD_SPOT_LONGITUDE + 0.005, TEST_FOOD_SPOT_LATITUDE),
        ),
        MockTestFoodSpot(
            name = "Food Spot 4 - 800m",
            point = CoordinateUtils.createCoordinate(TEST_FOOD_SPOT_LONGITUDE - 0.008, TEST_FOOD_SPOT_LATITUDE),
        ),
        MockTestFoodSpot(
            name = "Food Spot 5 - 1km",
            point = CoordinateUtils.createCoordinate(TEST_FOOD_SPOT_LONGITUDE, TEST_FOOD_SPOT_LATITUDE + 0.01),
        ),
    )

fun createMockTestFoodSpotList(): List<FoodSpots> =
    listOf(
        createMockTestFoodSpot(1L),
        createMockTestFoodSpot(2L),
        createMockTestFoodSpot(3L),
    )

fun createOperationHoursRequest(
    dayOfWeek: DayOfWeek = DayOfWeek.MON,
    openingHours: String = TEST_OPERATION_HOURS_OPEN,
    closingHours: String = TEST_OPERATION_HOURS_CLOSE,
) = OperationHoursRequest(dayOfWeek, openingHours, closingHours)

fun createTestFoodCategory(name: String = TEST_CATEGORY_NAME) = FoodCategory(name = name)

fun createTestFoodSpotsFoodCategory(
    foodSpots: FoodSpots = createTestFoodSpots(),
    foodCategory: FoodCategory = createTestFoodCategory(),
) = FoodSpotsFoodCategory(0L, foodSpots, foodCategory)

fun createTestReportFoodCategory(
    foodSpotsHistory: FoodSpotsHistory = createTestFoodHistory(),
    foodCategory: FoodCategory = createTestFoodCategory(),
) = ReportFoodCategory(0L, foodSpotsHistory, foodCategory)

fun createTestFoodOperationHours(
    foodSpots: FoodSpots = createTestFoodSpots(),
    dayOfWeek: DayOfWeek = DayOfWeek.MON,
    openingHours: String = TEST_OPERATION_HOURS_OPEN,
    closingHours: String = TEST_OPERATION_HOURS_CLOSE,
) = FoodSpotsOperationHours(foodSpots, dayOfWeek, openingHours, closingHours)

fun createTestReportOperationHours(
    foodSpotsHistory: FoodSpotsHistory = createTestFoodHistory(),
    dayOfWeek: DayOfWeek = DayOfWeek.MON,
    openingHours: String = TEST_OPERATION_HOURS_OPEN,
    closingHours: String = TEST_OPERATION_HOURS_CLOSE,
) = ReportOperationHours(foodSpotsHistory, dayOfWeek, openingHours, closingHours)

class MockTestFoodSpot(
    id: Long = 0L,
    name: String = TEST_FOOD_SPOT_NAME,
    foodTruck: Boolean = TEST_FOOD_SPOT_FOOD_TRUCK,
    open: Boolean = TEST_FOOD_SPOT_OPEN,
    closed: Boolean = TEST_FOOD_SPOT_STORE_CLOSURE,
    point: Point = TEST_FOOD_SPOT_POINT,
    operationHours: MutableList<FoodSpotsOperationHours> = mutableListOf(),
    foodCategories: MutableList<FoodSpotsFoodCategory> = mutableListOf(),
) : FoodSpots(id, name, foodTruck, open, closed, point, operationHours, foodCategories) {
    override var createdDateTime: LocalDateTime = LocalDateTime.now()
    override var updatedDateTime: LocalDateTime = LocalDateTime.now()
}

class MockTestFoodSpotsHistory(
    id: Long = 0L,
    foodSpots: FoodSpots = MockTestFoodSpot(),
    user: User = createTestUser(),
    name: String = TEST_FOOD_SPOT_NAME,
    foodTruck: Boolean = TEST_FOOD_SPOT_FOOD_TRUCK,
    open: Boolean = TEST_FOOD_SPOT_OPEN,
    closed: Boolean = TEST_FOOD_SPOT_STORE_CLOSURE,
    point: Point = TEST_FOOD_SPOT_POINT,
    reportType: ReportType = ReportType.STORE_CREATE,
    operationHours: List<ReportOperationHours> = listOf(),
    foodCategories: List<ReportFoodCategory> = listOf(),
) : FoodSpotsHistory(id, foodSpots, user, name, foodTruck, open, closed, point, reportType, operationHours, foodCategories) {
    override var createdDateTime: LocalDateTime = LocalDateTime.now()
}

fun createFoodSpotsSearchResponses(): FoodSpotsSearchResponses =
    FoodSpotsSearchResponses(
        listOf(
            FoodSpotsSearchResponse(
                id = 1L,
                name = "name",
                longitude = 1.0,
                latitude = 1.0,
                open = OperationStatus.OPEN,
                foodCategories = listOf("category1", "category2"),
                createdDateTime = LocalDateTime.of(2024, 1, 1, 1, 1),
            ),
            FoodSpotsSearchResponse(
                id = 2L,
                name = "name",
                longitude = 1.0,
                latitude = 1.0,
                open = OperationStatus.OPEN,
                foodCategories = listOf("category1", "category2"),
                createdDateTime = LocalDateTime.of(2024, 1, 1, 1, 1),
            ),
        ),
    )

fun createFoodCategoryResponse(foodCategory: FoodCategory = createTestFoodCategory()): FoodCategoryResponse =
    FoodCategoryResponse.of(foodCategory)

fun createTestFoodSpotsUpdateRequest(
    name: String = TEST_UPDATE_FOOD_SPOT_NAME,
    longitude: Double = TEST_UPDATE_FOOD_SPOT_LONGITUDE,
    latitude: Double = TEST_UPDATE_FOOD_SPOT_LATITUDE,
    open: Boolean = TEST_FOOD_SPOT_OPEN,
    closed: Boolean = TEST_FOOD_SPOT_STORE_CLOSURE,
    categories: Set<Long> = createTestFoodCategories().map { it.id }.toSet(),
    operationHours: List<OperationHoursRequest> = listOf(createOperationHoursRequest()),
): FoodSpotsUpdateRequest =
    FoodSpotsUpdateRequest(
        name,
        longitude,
        latitude,
        open,
        closed,
        categories,
        operationHours,
    )

// Entity 로부터 역으로 Request 를 만들어냅니다.
// 원본 FoodSpots Entity 로부터 변경내용이 없는 FoodSpotsUpdateRequest 를 생성하여 유효하지 않은 FoodSpots Update 요청 테스트에 사용됩니다.
fun createTestFoodSpotsUpdateRequestFromEntity(foodSpots: FoodSpots = createTestFoodSpots()): FoodSpotsUpdateRequest =
    FoodSpotsUpdateRequest(
        foodSpots.name,
        foodSpots.point.x,
        foodSpots.point.y,
        foodSpots.open,
        foodSpots.storeClosure,
        foodSpots.foodCategoryList.map { it.foodCategory.id }.toSet(),
        createTestFoodSpotsOperationHoursRequestsFromEntities(foodSpots.operationHoursList),
    )

fun createTestFoodSpotsOperationHoursRequestsFromEntities(operationHours: List<FoodSpotsOperationHours>): List<OperationHoursRequest> =
    operationHours.map {
        createOperationHoursRequest(it.dayOfWeek, it.openingHours, it.closingHours)
    }

fun createMockSearchCoinCaches(userId: Long): List<SearchCoinCache> =
    listOf(
        SearchCoinCache.of(
            userId = userId,
            latitude = TEST_FOOD_SPOT_LATITUDE,
            longitude = TEST_FOOD_SPOT_LONGITUDE + 0.001,
            radius = 1000,
        ),
        SearchCoinCache.of(
            userId = userId,
            latitude = TEST_FOOD_SPOT_LATITUDE + 0.001,
            longitude = TEST_FOOD_SPOT_LONGITUDE,
            radius = 1500,
        ),
    )

fun createReportOperationHoursResponse(): ReportOperationHoursResponse =
    ReportOperationHoursResponse(
        createTestReportOperationHours(),
    )

fun createReportHistoryDetailResponse(): ReportHistoryDetailResponse =
    ReportHistoryDetailResponse(
        createMockTestFoodHistory(),
        listOf(createTestReportPhotoResponse()),
        listOf(createTestReportCategoryResponse()),
        listOf(createReportOperationHoursResponse()),
    )

fun createTestSliceFoodSpotsReviewResponse(): SliceResponse<FoodSpotsReviewResponse> =
    SliceResponse(
        createMockSliceReview().map {
            FoodSpotsReviewResponse.of(
                it,
                createTestReviewerInfoResponse(),
                listOf(createTestReviewPhotoResponse()),
            )
        },
    )

fun createTestFoodSpotsDetailResponse(): FoodSpotsDetailResponse =
    FoodSpotsDetailResponse(
        createMockTestFoodSpot(),
        OperationStatus.OPEN,
        listOf(createTestReportPhotoResponse()),
    )
