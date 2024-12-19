package com.example.work10.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.work10.data.model.Cat

@Dao
interface CatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCat(cat: Cat)

    @Query("SELECT * FROM cats LIMIT 1")
    suspend fun getCat(): Cat?
}