package com.example.myapplication.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey
    val id: String = "",
    val title:String = "",
    val year:String = "",
    val image:String = ""
) {}