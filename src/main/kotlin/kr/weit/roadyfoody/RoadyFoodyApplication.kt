package kr.weit.roadyfoody

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan
@SpringBootApplication
class RoadyFoodyApplication

fun main(args: Array<String>) {
    runApplication<RoadyFoodyApplication>(*args)
}
