package com.example.myapplication.data.local_db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.data.models.Movie
// MovieDao interface: provides methods for querying and inserting movies in the Room database.
// It allows you to retrieve all movies, retrieve a movie by its ID, and insert movies individually or in bulk.
// The use of LiveData enables observing the data and receiving updates when changes occur.
@Dao
interface MovieDao {

    @Query("SELECT * FROM movies")
    fun getAllMovies() : LiveData<List<Movie>>

    @Query("SELECT * FROM movies WHERE id = :id")
    fun getMovie(id:String) : LiveData<Movie>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: Movie)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<Movie>)

}