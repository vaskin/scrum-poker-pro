package ru.newdv.bg.report.util

import org.springframework.http.ContentDisposition
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import java.nio.charset.StandardCharsets

fun ByteArray.toHttpEntity(filename: String, type: MediaType): HttpEntity<ByteArray> {
    val header = HttpHeaders()
    header.contentType = type
    val contentDisposition = ContentDisposition.builder("attachment")
        .filename(filename, StandardCharsets.UTF_8)
        .build()
    header.set(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
    header.contentLength = size.toLong()

    return HttpEntity(this, header)
}
