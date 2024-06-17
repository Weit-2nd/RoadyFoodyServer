package kr.weit.roadyfoody.tourism.application.service

import kr.weit.roadyfoody.tourism.config.TourismProperties
import kr.weit.roadyfoody.tourism.dto.ResponseWrapper
import kr.weit.roadyfoody.tourism.dto.SearchResponse
import kr.weit.roadyfoody.tourism.dto.SearchResponses
import kr.weit.roadyfoody.tourism.dto.TourismType
import kr.weit.roadyfoody.tourism.presentation.client.TourismClientInterface
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.yaml.snakeyaml.util.UriEncoder
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Service
class TourismService(
    private val tourismProperties: TourismProperties,
    private val tourismClientInterface: TourismClientInterface,
) {
    private val log: Logger = LoggerFactory.getLogger(TourismService::class.java)

    private val executor: ExecutorService = Executors.newVirtualThreadPerTaskExecutor()

    fun searchTourism(
        numOfRows: Int,
        keyword: String,
    ): SearchResponses {
        val encodedKeyword: String = UriEncoder.encode(keyword)
        val encodedServiceKey: String = UriEncoder.encode(tourismProperties.apiKey)
        var start = System.currentTimeMillis()

        var tourResponse =
            CompletableFuture.supplyAsync(
                {
                    tourismClientInterface.searchTourismKeyword(
                        encodedServiceKey,
                        tourismProperties.mobileApp,
                        tourismProperties.mobileOs,
                        numOfRows,
                        encodedKeyword,
                        12,
                    )
                },
                executor,
            )

        var festivalResponse =
            CompletableFuture.supplyAsync(
                {
                    tourismClientInterface.searchTourismKeyword(
                        encodedServiceKey,
                        tourismProperties.mobileApp,
                        tourismProperties.mobileOs,
                        numOfRows,
                        encodedKeyword,
                        15,
                    )
                },
                executor,
            )
        var end = System.currentTimeMillis()
        log.info("Time : {} ms", end - start)

        return mergeResponses(tourResponse.get(), festivalResponse.get(), numOfRows)
    }

    private fun mergeResponses(
        tourResponse: ResponseWrapper,
        festivalResponse: ResponseWrapper,
        count: Int = 10,
    ): SearchResponses {
        val filteredTourItems =
            tourResponse.response.body.items.item.filter {
                it.mapX != null && it.mapX != null
            }.map {
                SearchResponse(
                    title = it.title,
                    addr1 = it.addr1,
                    addr2 = it.addr2,
                    mapX = it.mapX!!,
                    mapY = it.mapY!!,
                    tel = it.tel,
                    firstImage2 = it.firstImage2,
                    type = TourismType.TOUR,
                )
            }

        val filteredFestivalItems =
            festivalResponse.response.body.items.item.filter {
                it.mapX != null && it.mapY != null
            }.map {
                SearchResponse(
                    title = it.title,
                    addr1 = it.addr1,
                    addr2 = it.addr2,
                    mapX = it.mapX!!,
                    mapY = it.mapY!!,
                    tel = it.tel,
                    firstImage2 = it.firstImage2,
                    type = TourismType.FESTIVAL,
                )
            }

        val mergedItems = mutableListOf<SearchResponse>()
        val maxItems = minOf(count, filteredTourItems.size + filteredFestivalItems.size)

        var tourIndex = 0
        var festivalIndex = 0
        var toggle = true

        while (mergedItems.size < maxItems) {
            if (toggle && tourIndex < filteredTourItems.size) {
                mergedItems.add(filteredTourItems[tourIndex])
                tourIndex++
            } else if (!toggle && festivalIndex < filteredFestivalItems.size) {
                mergedItems.add(filteredFestivalItems[festivalIndex])
                festivalIndex++
            }
            toggle = !toggle
        }

        return SearchResponses(items = mergedItems)
    }
}
