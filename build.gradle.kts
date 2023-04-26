import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion: String by project
val springBootVersion: String by project
val testContainersVersion: String by project
val springCloudVersion: String by project

plugins {
    val springBootVersion = "2.5.4"
    val kotlinVersion = "1.6.0"
    val detektVersion = "1.19.0-RC1"
    val dependencyManagementVersion = "1.0.11.RELEASE"
    val sonarqubeVersion = "2.7"
    val jibVersion = "3.1.4"
    val gitPropertiesVersion = "2.4.0-rc2"

    kotlin("jvm") version kotlinVersion apply false
    kotlin("plugin.spring") version kotlinVersion apply false
    `maven-publish`
    id("org.springframework.boot") version springBootVersion apply false
    id("io.spring.dependency-management") version dependencyManagementVersion
    id("io.gitlab.arturbosch.detekt") version detektVersion apply false
    id("org.sonarqube") version sonarqubeVersion
    id("jacoco")
    id("com.google.cloud.tools.jib") version jibVersion apply false
    id("com.gorylenko.gradle-git-properties") version gitPropertiesVersion apply false
}

allprojects {
    group = "com.scrumpokerpro"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
        jcenter()
    }
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "io.gitlab.arturbosch.detekt")
    apply(plugin = "jacoco")
    apply(plugin = "io.spring.dependency-management")

    dependencyManagement {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:$springBootVersion")
            mavenBom("org.testcontainers:testcontainers-bom:$testContainersVersion")
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")

        }
    }

    val ktlint by configurations.creating
    dependencies.add("ktlint", "com.pinterest:ktlint:0.43.0") {
        attributes {
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
        }
    }
    val ktlintCheck by tasks.creating(JavaExec::class) {
        group = "verification"
        description = "Check Kotlin code style."
        classpath = ktlint
        mainClass.set("com.pinterest.ktlint.Main")
        args = mutableListOf("**/*.gradle.kts", "**/*.kt")
    }
    tasks["check"].dependsOn(ktlintCheck)
    tasks.register<JavaExec>("ktlintFormat") {
        group = "formatting"
        description = "Fix Kotlin code style deviations."
        classpath = ktlint
        mainClass.set("com.pinterest.ktlint.Main")
        args = mutableListOf("-F")
        jvmArgs = (jvmArgs ?: mutableListOf()).apply {
            addAll(listOf("--add-opens", "java.base/java.lang=ALL-UNNAMED"))
        }
    }

    tasks.withType<JacocoReport> {
        reports {
            xml.apply {
                isEnabled = true
            }
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events = setOf(
                TestLogEvent.FAILED,
                TestLogEvent.PASSED,
                TestLogEvent.SKIPPED
            )
            exceptionFormat = TestExceptionFormat.FULL
            showExceptions = true
            showCauses = true
            showStackTraces = true
        }
        finalizedBy("jacocoTestReport")
    }

    val detektConfigFilePath = "$rootDir/config/detekt/detekt.yml"

    tasks.withType<Detekt> {
        exclude("resources/")
        exclude("build/")
        config.setFrom(files(detektConfigFilePath))
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = JavaVersion.VERSION_17.toString()
        }
    }

    sonarqube {
        properties {
            property(
                "detekt.sonar.kotlin.filters",
                ".*/resources/.*,.*/build/.*,.*/target/.*"
            )
            property(
                "detekt.sonar.kotlin.config.path",
                detektConfigFilePath
            )
            property(
                "sonar.scm.provider",
                "git"
            )
            property(
                "sonar.coverage.jacoco.xmlReportPaths",
                "$buildDir/reports/jacoco/test/jacocoTestReport.xml"
            )
        }
    }

    jacoco {
        toolVersion = "0.8.7"
    }
}
