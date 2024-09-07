package kr.weit.roadyfoody

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [ElasticsearchDataAutoConfiguration::class])
class RoadyFoodyApplication

fun main(args: Array<String>) {
    runApplication<RoadyFoodyApplication>(*args)
}
