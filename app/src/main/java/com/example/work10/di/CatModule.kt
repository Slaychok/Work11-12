package com.example.work10.di

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.work10.data.database.CatDao
import com.example.work10.data.database.CatDatabase
import com.example.work10.data.network.CatApi
import com.example.work10.data.repository.CatRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(SingletonComponent::class)
object CatModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.thecatapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideCatApi(retrofit: Retrofit): CatApi {
        return retrofit.create(CatApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CatDatabase {
        return CatDatabase.getDatabase(context)
    }

    @Provides
    fun provideCatDao(database: CatDatabase) = database.catDao()

    @Provides
    @Singleton
    fun provideCatRepository(catDao: CatDao, catApi: CatApi,  @ApplicationContext context: Context): CatRepository {
        return CatRepository(catDao, catApi, context)
    }
}
