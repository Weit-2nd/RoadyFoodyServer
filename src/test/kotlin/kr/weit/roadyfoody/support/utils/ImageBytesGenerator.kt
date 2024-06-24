package kr.weit.roadyfoody.support.utils

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.webp.WebpWriter
import kr.weit.roadyfoody.support.utils.ImageFormat.GIF
import kr.weit.roadyfoody.support.utils.ImageFormat.JPEG
import kr.weit.roadyfoody.support.utils.ImageFormat.PNG
import kr.weit.roadyfoody.support.utils.ImageFormat.WEBP
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

fun generateImageBytes(format: ImageFormat): ByteArray =
    when (format) {
        PNG, JPEG, GIF -> generateImageBytes(format.first())
        WEBP -> generateWebPImageBytes()
    }

private fun generateImageBytes(format: String): ByteArray {
    val bufferedImage = BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)

    ByteArrayOutputStream().use {
        ImageIO.write(bufferedImage, format, it)
        return it.toByteArray()
    }
}

private fun generateWebPImageBytes(): ByteArray {
    val jpegImageBytes = ImmutableImage.loader().fromBytes(generateImageBytes(JPEG.first()))
    return jpegImageBytes.bytes(WebpWriter.DEFAULT.withMultiThread())
}

enum class ImageFormat(val values: List<String>) {
    PNG(listOf("png")),
    JPEG(listOf("jpeg", "jpg")),
    WEBP(listOf("webp")),
    GIF(listOf("gif")),
    ;

    fun first(): String = values.first()

    fun getStrValues(): String = values.joinToString()

    fun getContentTypes(): List<String> = values.map { "image/$it" }
}
