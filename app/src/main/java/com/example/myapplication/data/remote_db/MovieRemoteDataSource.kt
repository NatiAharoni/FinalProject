package com.example.myapplication.data.remote_db

import javax.inject.Inject

class MovieRemoteDataSource @Inject constructor(
    private val movieService: MovieService
)  : BaseDataSource() {

    suspend fun getMovies() = getResult { movieService.getAllMovies() }
    suspend fun getMovie(id : String) = getResult { movieService.getMovie(id) }
}