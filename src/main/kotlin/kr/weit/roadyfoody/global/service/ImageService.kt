package kr.weit.roadyfoody.global.service

import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Service
class ImageService(
    private val storageService: StorageService,
) {
    fun upload(
        name: String,
        file: MultipartFile,
    ) {
        storageService.upload(name, file.inputStream)
    }

    fun generateImageName(fileName: String?): String {
        val extension =
            if (fileName == null) {
                ""
            } else {
                ".${StringUtils.getFilenameExtension(fileName)}"
            }
        return "${UUID.randomUUID()}$extension"
    }
}
