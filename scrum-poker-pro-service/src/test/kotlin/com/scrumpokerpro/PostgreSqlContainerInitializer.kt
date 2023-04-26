package com.scrumpokerpro

import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.PostgreSQLContainer

class PostgreSqlContainerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    internal class SpecifiedPostgreSQLContainer(image: String) : PostgreSQLContainer<SpecifiedPostgreSQLContainer>(image)

    companion object {
        internal val postgres = SpecifiedPostgreSQLContainer("postgres:12-alpine")
            .withUsername("username")
            .withPassword("password")
            .withDatabaseName("db")
    }

    override fun initialize(context: ConfigurableApplicationContext) {
        postgres.start()

        TestPropertyValues.of(
            "spring.flyway.url=${postgres.jdbcUrl}",
            "spring.flyway.username=${postgres.username}",
            "spring.flyway.password=${postgres.password}",
            "spring.r2dbc.url=r2dbc:postgresql://${postgres.host}:${postgres.firstMappedPort}/${postgres.databaseName}",
            "spring.r2dbc.username=${postgres.username}",
            "spring.r2dbc.password=${postgres.password}"
        ).applyTo(context.environment)
    }
}
