package com.scrumpokerpro.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import java.net.URI

@Configuration
class S3Config(val scrumPokerPro: ScrumPokerPro) {

    @Bean
    fun s3AsyncClient(): S3AsyncClient {
        return S3AsyncClient.builder()
            .region(Region.of(scrumPokerPro.s3.region))
            .credentialsProvider {
                AwsBasicCredentials.create(
                    scrumPokerPro.s3.accessKeyId,
                    scrumPokerPro.s3.secretAccessKey
                )
            }
            .endpointOverride(URI(scrumPokerPro.s3.url))
            .build()
    }
}
