package com.scrumpokerpro.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties("scrum-poker-pro")
@ConstructorBinding
data class ScrumPokerPro(
    val s3: S3,
    val jira: Jira,
    val email: Email
)

@ConfigurationProperties("scrum-poker-pro.s3")
@ConstructorBinding
data class S3(
    val url: String,
    val region: String,
    val accessKeyId: String,
    val secretAccessKey: String
)

@ConfigurationProperties("scrum-poker-pro.jira")
@ConstructorBinding
data class Jira(
    val url: String
)

@ConfigurationProperties("scrum-poker-pro.email")
@ConstructorBinding
data class Email(
    val from: String,
    val displayName: String,
    val baseUrl: String
)
