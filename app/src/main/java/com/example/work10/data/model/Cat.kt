package com.example.work10.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cats")
data class Cat(
    @PrimaryKey
    val id: String,
    val url: String,
    val width: Int,
    val height: Int
)