package com.example.work10.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.work10.data.repository.CatRepository
import com.example.work10.data.work.ImageDownloadWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatViewModel @Inject constructor(
    private val context: Context,
    private val repository: CatRepository
): ViewModel() {
    val catImageUrl = MutableLiveData<String>()
    val error = MutableLiveData<String>()
    val saveResult = MutableLiveData<Boolean>()

    fun fetchCat() {
        repository.fetchCatFromApi { result ->
            result.onSuccess { cats ->
                if (cats.isNotEmpty()) {
                    val cat = cats[0]
                    viewModelScope.launch {
                        repository.saveCatToDb(cat)
                    }
                    catImageUrl.postValue(cat.url)
                } else {
                    error.postValue("No cat data found")
                }
            }.onFailure { throwable ->
                error.postValue(throwable.message)
            }
        }
    }

    fun loadCatFromDb() {
        viewModelScope.launch {
            val cat = repository.getCatFromDb()
            if (cat != null) {
                catImageUrl.postValue(cat.url)
            } else {
                error.postValue("No cat in database")
            }
        }
    }

    fun downloadAndSaveImage(url: String) {
        viewModelScope.launch {
            val success = repository.downloadAndSaveImage(url)
            saveResult.postValue(success)
        }
    }

    fun downloadImageUsingWorkManager(url: String) {
        val workManager = WorkManager.getInstance(context)

        val inputData = Data.Builder()
            .putString("IMAGE_URL", url)
            .build()

        val downloadWorkRequest = OneTimeWorkRequestBuilder<ImageDownloadWorker>()
            .setInputData(inputData)
            .build()

        workManager.enqueue(downloadWorkRequest)
    }
}