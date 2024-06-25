package kr.weit.roadyfoody.global.utils

import org.apache.tika.Tika
import org.apache.tika.mime.MimeTypes
import org.springframework.web.multipart.MultipartFile

class MimeUtils {
    companion object {
        @JvmStatic
        private val tika = Tika()

        @JvmStatic
        private val mimeTypes = MimeTypes.getDefaultMimeTypes()

        fun detectMimeType(file: MultipartFile): String = tika.detect(file.inputStream)

        fun getFileExtension(file: MultipartFile): String = mimeTypes.forName(detectMimeType(file)).extension
    }
}
