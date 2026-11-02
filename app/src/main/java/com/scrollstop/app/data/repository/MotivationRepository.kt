package com.scrollstop.app.data.repository

import android.content.Context
import android.net.Uri
import com.scrollstop.app.data.local.dao.MotivationalItemDao
import com.scrollstop.app.data.local.entity.MotivationalItemEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class MotivationRepository(
    private val context: Context,
    private val motivationalItemDao: MotivationalItemDao
) {
    private val imagesDir: File by lazy {
        val dir = File(context.filesDir, "motivation_images")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        dir
    }

    fun getAllItems(): Flow<List<MotivationalItemEntity>> = motivationalItemDao.getAllItems()

    suspend fun getActiveMotivation(useRandom: Boolean): MotivationalItemEntity? {
        return withContext(Dispatchers.IO) {
            if (useRandom) {
                motivationalItemDao.getRandomItem() ?: motivationalItemDao.getActiveItemSync()
            } else {
                motivationalItemDao.getActiveItemSync() ?: motivationalItemDao.getRandomItem()
            }
        }
    }

    suspend fun addCustomMotivation(
        imageUri: Uri?,
        quote: String,
        author: String = "My Goal"
    ): Long {
        return withContext(Dispatchers.IO) {
            var localPath: String? = null
            if (imageUri != null) {
                localPath = copyImageToInternalStorage(imageUri)
            }

            val count = motivationalItemDao.getCount()
            val newItem = MotivationalItemEntity(
                imagePath = localPath,
                quote = quote.ifBlank { "STOP SCROLLING. REMEMBER YOUR GOAL." },
                author = author.ifBlank { "My Commitment" },
                isUserAdded = true,
                isActive = (count == 0) // Make active if first item
            )
            motivationalItemDao.insertItem(newItem)
        }
    }

    suspend fun setActiveItem(id: Long) {
        withContext(Dispatchers.IO) {
            motivationalItemDao.setActiveItem(id)
        }
    }

    suspend fun deleteItem(item: MotivationalItemEntity) {
        withContext(Dispatchers.IO) {
            // Delete local file if exists
            item.imagePath?.let { path ->
                val file = File(path)
                if (file.exists()) {
                    file.delete()
                }
            }
            motivationalItemDao.deleteItem(item.id)
        }
    }

    private fun copyImageToInternalStorage(uri: Uri): String? {
        return try {
            val fileName = "motivation_${UUID.randomUUID()}.jpg"
            val destFile = File(imagesDir, fileName)
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(destFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            destFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
