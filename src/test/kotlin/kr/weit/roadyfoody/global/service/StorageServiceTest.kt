package kr.weit.roadyfoody.global.service

import io.awspring.cloud.s3.S3Template
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeEmpty
import kr.weit.roadyfoody.global.config.S3Properties
import kr.weit.roadyfoody.global.service.StorageService.Companion.OBJECT_STORAGE_CACHE_KEY
import kr.weit.roadyfoody.support.annotation.ServiceIntegrateTest
import org.springframework.data.redis.core.StringRedisTemplate
import kotlin.random.Random

const val TEST_OBJECT_NAME = "testObject"

@ServiceIntegrateTest
class StorageServiceTest(
    private val storageService: StorageService,
    private val s3Template: S3Template,
    private val s3Properties: S3Properties,
    private val redisTemplate: StringRedisTemplate,
) : BehaviorSpec({
        beforeSpec { s3Template.createBucket(s3Properties.bucket) }
        afterSpec { s3Template.deleteBucket(s3Properties.bucket) }

        afterTest {
            s3Template.deleteObject(s3Properties.bucket, TEST_OBJECT_NAME)
        }

        given("파일이 존재 하지 않는 경우") {
            s3Template.objectExists(s3Properties.bucket, TEST_OBJECT_NAME).shouldBeFalse()

            `when`("파일을 저장하면") {
                storageService.upload(TEST_OBJECT_NAME, Random.nextBytes(20).inputStream())
                then("파일이 저장된다.") {
                    s3Template.objectExists(s3Properties.bucket, TEST_OBJECT_NAME).shouldBeTrue()
                }
            }

            `when`("파일을 가져오면") {
                then("아무런 에러가 발생하지 않는다.") {
                    shouldNotThrowAny { storageService.downloadUrl(TEST_OBJECT_NAME) }
                }
            }

            `when`("파일을 삭제하면") {
                then("아무런 에러가 발생하지 않는다.") {
                    shouldNotThrowAny { storageService.delete(TEST_OBJECT_NAME) }
                }
            }
        }

        given("파일이 이미 존재하는 경우") {
            beforeEach {
                storageService.upload(TEST_OBJECT_NAME, Random.nextBytes(20).inputStream())
            }

            `when`("파일을 가져오면") {
                then("파일의 링크가 반환된다") {
                    storageService.downloadUrl(TEST_OBJECT_NAME).shouldNotBeEmpty()
                }
            }

            `when`("레디스에 캐시가 있는 상태로 다시 파일을 가져오면") {
                val cachedUrl = storageService.downloadUrl(TEST_OBJECT_NAME)
                then("캐시의 값이 반환된다.") {
                    storageService.downloadUrl(TEST_OBJECT_NAME) shouldBe cachedUrl
                }
            }

            `when`("파일을 삭제하면") {
                // S3는 파일 삭제가 비동기적으로 이루어지기 때문에 삭제 후 바로 확인할 수 없다.
                // 따라서 삭제 후에도 파일이 존재하는지 확인하는 테스트는 하지 않는다.
                redisTemplate.hasKey("$OBJECT_STORAGE_CACHE_KEY$TEST_OBJECT_NAME").shouldBeTrue()
                storageService.delete(TEST_OBJECT_NAME)
                then("레디스의 캐시가 삭제된다.") {
                    redisTemplate.hasKey("$OBJECT_STORAGE_CACHE_KEY$TEST_OBJECT_NAME").shouldBeFalse()
                }
            }
        }
    })
