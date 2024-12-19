package com.example.work10.data.network

import com.example.work10.data.model.Cat
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface CatApi {
    @GET("v1/images/search?limit=1&category_ids=5")
    fun getCat(): Call<List<Cat>>

    @GET
    fun downloadImage(@Url url: String): Call<ResponseBody>
}