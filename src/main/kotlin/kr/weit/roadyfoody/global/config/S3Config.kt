package kr.weit.roadyfoody.global.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class S3Config(
    private val s3Properties: S3Properties,
) {
    @Bean
    fun amazonS3Client(): AmazonS3 =
        AmazonS3ClientBuilder
            .standard()
            .withEndpointConfiguration(
                AwsClientBuilder.EndpointConfiguration(
                    s3Properties.endpoint,
                    s3Properties.region,
                ),
            ).withCredentials(
                AWSStaticCredentialsProvider(
                    BasicAWSCredentials(
                        s3Properties.accessKey,
                        s3Properties.secretKey,
                    ),
                ),
            ).withPathStyleAccessEnabled(true)
            .build()
}
