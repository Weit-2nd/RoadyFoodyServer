package kr.weit.roadyfoody.global.config

import kr.weit.roadyfoody.tourism.config.TourismProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@EnableConfigurationProperties(TourismProperties::class, S3Properties::class)
@Configuration
class EnablePropertiesConfig
