package com.scrumpokerpro.service.file

import com.scrumpokerpro.utils.logger
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import software.amazon.awssdk.core.ResponseBytes
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.core.async.AsyncResponseTransformer
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectResponse
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.awt.Color
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam

@Service
class FileServiceImpl(
    val s3AsyncClient: S3AsyncClient
) : FileService {

    val log by logger()

    override suspend fun download(bucket: String, key: String): ResponseBytes<GetObjectResponse> {
        return s3AsyncClient.getObject(
            GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build(),
            AsyncResponseTransformer.toBytes()
        ).await()
    }

    override suspend fun upload(file: FilePart, bucket: String, key: String) {
        s3AsyncClient.putObject(
            PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.headers().contentType.toString())
                .contentDisposition(file.filename())
                .build(),
            AsyncRequestBody.fromBytes(
                DataBufferUtils.join(file.content()).awaitFirst().asInputStream().resizeImage(SIZE_PX).compressImage(
                    ratio = 0.5F, format = file.filename().substringAfterLast(".")
                )
            )
        ).await()
        log.info("file uploaded [bucket = {}, key = {}]", bucket, key)
    }

    fun BufferedImage.compressImage(ratio: Float, format: String): ByteArray {
        ByteArrayOutputStream().use { baos ->
            ImageIO.createImageOutputStream(baos).use { ios ->
                val imageWriter = ImageIO.getImageWritersByFormatName(format).next()
                imageWriter.output = ios
                val param = imageWriter.defaultWriteParam
                param.compressionMode = ImageWriteParam.MODE_EXPLICIT
                param.compressionQuality = ratio
                imageWriter.write(null, IIOImage(this, null, null), param)
                return baos.toByteArray()
            }
        }
    }

    private fun InputStream.resizeImage(newSizePx: Int): BufferedImage {
        val bufferedImage = ImageIO.read(this)

        val aspectRatio = bufferedImage.width / bufferedImage.height.toFloat()
        val width = (newSizePx * aspectRatio).toInt()
        val height = newSizePx

        val scaledImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        scaledImage.createGraphics().apply {
            this.background = Color.WHITE
            this.paint = Color.WHITE
            this.fillRect(0, 0, width, height)
            this.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)
            this.drawImage(bufferedImage, 0, 0, width, height, null)
        }
        return scaledImage
    }

    companion object {
        const val SIZE_PX = 400
    }
}
