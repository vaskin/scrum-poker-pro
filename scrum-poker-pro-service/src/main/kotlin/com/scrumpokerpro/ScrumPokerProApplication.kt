package com.scrumpokerpro

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import reactivefeign.spring.config.EnableReactiveFeignClients

@SpringBootApplication
@EnableR2dbcRepositories
@EnableReactiveFeignClients
@EnableFeignClients
@ConfigurationPropertiesScan
class ScrumPokerProApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<ScrumPokerProApplication>(*args)
}
