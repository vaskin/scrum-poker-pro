package com.scrumpokerpro

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@SpringBootTest
@ContextConfiguration(initializers = [PostgreSqlContainerInitializer::class])
@ActiveProfiles("test")
class ScrumPokerProApplicationTest {

    @Test
    @Suppress("EmptyFunctionBlock")
    fun contextLoads() {
    }
}
