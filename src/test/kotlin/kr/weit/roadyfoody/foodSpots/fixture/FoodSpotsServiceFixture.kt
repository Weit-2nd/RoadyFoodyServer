package kr.weit.roadyfoody.foodSpots.fixture

import kr.weit.roadyfoody.auth.fixture.createTestImageFile
import kr.weit.roadyfoody.foodSpots.domain.FoodSpots
import kr.weit.roadyfoody.foodSpots.domain.FoodSpots.Companion.SRID_WGS84
import kr.weit.roadyfoody.foodSpots.domain.FoodSpotsHistory
import kr.weit.roadyfoody.foodSpots.dto.ReportRequest
import kr.weit.roadyfoody.foodSpots.utils.FOOD_SPOTS_NAME_MAX_LENGTH
import kr.weit.roadyfoody.support.utils.ImageFormat
import kr.weit.roadyfoody.user.fixture.createTestUser
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream

const val TEST_FOOD_SPOT_ID = 1L
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

fun createTestFoodSpot(id: Long = TEST_FOOD_SPOT_ID) =
    FoodSpots(id, TEST_FOOD_SPOT_NAME, TEST_FOOD_SPOT_FOOD_TRUCK, TEST_FOOD_SPOT_OPEN, TEST_FOOD_SPOT_STORE_CLOSURE, TEST_FOOD_SPOT_POINT)

fun createTestFoodHistory(id: Long = TEST_FOOD_SPOT_ID) =
    FoodSpotsHistory(
        id,
        createTestFoodSpot(),
        createTestUser(),
        TEST_FOOD_SPOT_NAME,
        TEST_FOOD_SPOT_FOOD_TRUCK,
        TEST_FOOD_SPOT_OPEN,
        TEST_FOOD_SPOT_STORE_CLOSURE,
        TEST_FOOD_SPOT_POINT,
    )

fun createTestReportRequest(
    name: String = TEST_FOOD_SPOT_NAME,
    longitude: Double = TEST_FOOD_SPOT_LONGITUDE,
    latitude: Double = TEST_FOOD_SPOT_LATITUDE,
    foodTruck: Boolean = TEST_FOOD_SPOT_FOOD_TRUCK,
    open: Boolean = TEST_FOOD_SPOT_OPEN,
    storeClosure: Boolean = TEST_FOOD_SPOT_STORE_CLOSURE,
) = ReportRequest(
    name,
    longitude,
    latitude,
    foodTruck,
    open,
    storeClosure,
)

fun createMockPhotoList(
    format: ImageFormat,
    name: String = TEST_PHOTO_NAME,
): List<MultipartFile> =
    listOf(
        createTestImageFile(format),
        createTestImageFile(format, name),
    )

fun createFoodSpotsRequestFile(
    contentStream: InputStream,
    name: String = TEST_FOOD_SPOTS_REQUEST_NAME,
    originalFileName: String? = TEST_FOOD_SPOTS_REQUEST_NAME,
    contentType: String? = MediaType.APPLICATION_JSON_VALUE,
): MockMultipartFile =
    MockMultipartFile(
        name,
        originalFileName,
        contentType,
        contentStream,
    )
