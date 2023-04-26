val kotlinVersion: String by project
val jacksonKotlinVersion: String by project
val springBootVersion: String by project
val springfoxBootStarterVersion: String by project
val dockerRegistry: String? by project
val projectName: String? by project
val imageTag: String? by project
val registryUser: String? by project
val registryPassword: String? by project
val jdkBaseImage: String? by project
val feignReactorVersion: String? by project
val s3Version: String? by project
val sentryVersion: String? by project
val logbackVersion: String? by project
val logstashLogbackEncoderVersion: String by project

plugins {
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("com.google.cloud.tools.jib")
    id("com.gorylenko.gradle-git-properties")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-freemarker")
    implementation("org.springframework.cloud:spring-cloud-starter-sleuth")
    implementation("org.springframework.cloud:spring-cloud-sleuth-zipkin")
    implementation("io.r2dbc:r2dbc-postgresql")
    runtimeOnly("io.r2dbc:r2dbc-pool")
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonKotlinVersion")
    implementation("io.springfox:springfox-boot-starter:$springfoxBootStarterVersion")
    implementation("com.playtika.reactivefeign:feign-reactor:$feignReactorVersion")
    implementation("com.playtika.reactivefeign:feign-reactor-core:$feignReactorVersion")
    implementation("com.playtika.reactivefeign:feign-reactor-spring-configuration:$feignReactorVersion")
    implementation("com.playtika.reactivefeign:feign-reactor-webclient:$feignReactorVersion")
    implementation("software.amazon.awssdk:s3:$s3Version")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("io.sentry:sentry-spring-boot-starter:$sentryVersion")
    implementation("io.sentry:sentry-logback:$sentryVersion")
    implementation("ch.qos.logback.contrib:logback-jackson:$logbackVersion")
    implementation("ch.qos.logback.contrib:logback-json-classic:$logbackVersion")
    implementation("net.logstash.logback:logstash-logback-encoder:$logstashLogbackEncoderVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation ("com.github.tomakehurst:wiremock-jre8:2.34.0")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.cloud:spring-cloud-contract-wiremock")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:r2dbc")
}

jib {
    from {
        image = jdkBaseImage
    }
    to {
        image = "$dockerRegistry/$projectName"
        tags = setOf("latest", imageTag)
        auth {
            username = registryUser
            password = registryPassword
        }
    }
    container {
        args = listOf("\$JAVA_TOOL_OPTIONS")
        environment = mapOf("JAVA_TOOL_OPTIONS" to "-Xms128m -Xmx512m -XX:MaxMetaspaceSize=128m -XX:MaxDirectMemorySize=256m")
    }
}
