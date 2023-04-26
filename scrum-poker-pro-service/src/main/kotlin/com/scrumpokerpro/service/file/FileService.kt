package com.scrumpokerpro.service.file

import org.springframework.http.codec.multipart.FilePart
import software.amazon.awssdk.core.ResponseBytes
import software.amazon.awssdk.services.s3.model.GetObjectResponse

interface FileService {

    suspend fun upload(file: FilePart, bucket: String, key: String)

    suspend fun download(bucket: String, key: String): ResponseBytes<GetObjectResponse>
}
