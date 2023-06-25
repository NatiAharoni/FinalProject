package com.example.myapplication.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
// Movie class: the pattern for single item fetched from the IMDB API
@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey
    val id: String = "",
    val title:String = "",
    val year:String = "",
    val image:String = ""
) {}