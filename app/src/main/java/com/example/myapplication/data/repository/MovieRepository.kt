package com.example.myapplication.data.repository

import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local_db.MovieDao
import com.example.myapplication.data.remote_db.MovieRemoteDataSource
import com.example.myapplication.utils.performFetchingAndSaving
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val remoteDataSource : MovieRemoteDataSource,
    private val localDataSource: MovieDao
) {

    fun getMovies()  = performFetchingAndSaving(
        {localDataSource.getAllMovies()},
        {remoteDataSource.getMovies()},
        {localDataSource.insertMovies(it.items)}
    )

    fun getMovie(id:String) = performFetchingAndSaving(
        {localDataSource.getMovie(id)},
        {remoteDataSource.getMovie(id)},
        {localDataSource.insertMovie(it)}
    )
}