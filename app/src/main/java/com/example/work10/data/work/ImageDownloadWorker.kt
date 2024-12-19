package com.example.work10.data.work

import android.content.Context
import androidx.work.WorkerParameters
import com.example.work10.data.repository.CatRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import androidx.work.CoroutineWorker


class ImageDownloadWorker @Inject constructor(
    @ApplicationContext context: Context,
    workerParams: WorkerParameters,
    private val repository: CatRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val imageUrl = inputData.getString("IMAGE_URL") ?: return Result.failure()

        return try {
            val success = repository.downloadAndSaveImage(imageUrl)
            if (success) {
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}
