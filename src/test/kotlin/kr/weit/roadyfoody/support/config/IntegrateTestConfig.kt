package kr.weit.roadyfoody.support.config

import kr.weit.roadyfoody.RoadyFoodyApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackageClasses = [RoadyFoodyApplication::class])
class IntegrateTestConfig
