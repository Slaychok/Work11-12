package com.example.work10.data.repository

import android.content.Context
import com.example.work10.data.database.CatDao
import com.example.work10.data.model.Cat
import com.example.work10.data.network.CatApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject

class CatRepository @Inject constructor(
    private val catDao: CatDao,
    private val catApi: CatApi,
    private val context: Context
) {
    suspend fun downloadAndSaveImage(url: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = catApi.downloadImage(url).execute()
                if (response.isSuccessful && response.body() != null) {
                    response.body()?.let { saveImageToInternalStorage(it) } ?: false
                } else {
                    false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    private fun saveImageToInternalStorage(body: ResponseBody): Boolean {
        return try {
            val file = File(context.filesDir, "cat_image.png")  // Путь для сохранения
            val inputStream: InputStream = body.byteStream()
            FileOutputStream(file).use { output ->
                inputStream.copyTo(output)  // Копируем поток данных в файл
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun fetchCatFromApi(callback: (Result<List<Cat>>) -> Unit) {
        catApi.getCat().enqueue(object : Callback<List<Cat>> {
            override fun onResponse(call: Call<List<Cat>>, response: Response<List<Cat>>) {
                if (response.isSuccessful) {
                    response.body()?.let { cats ->
                        callback(Result.success(cats))
                    } ?: run {
                        callback(Result.failure(Throwable("No cat data found")))
                    }
                } else {
                    callback(Result.failure(Throwable("Error code: ${response.code()}")))
                }
            }

            override fun onFailure(call: Call<List<Cat>>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    suspend fun saveCatToDb(cat: Cat) = withContext(Dispatchers.IO) {
        catDao.insertCat(cat)
    }

    suspend fun getCatFromDb(): Cat? = withContext(Dispatchers.IO) {
        catDao.getCat()
    }

}