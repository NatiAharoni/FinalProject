package com.example.myapplication.data.remote_db

import javax.inject.Inject
// MovieRemoteDataSource class: provides suspend functions for fetching movie data from a remote server.
// It uses the MovieService interface to make API calls and relies on the getResult function
// (defined in the BaseDataSource class) to handle the API calls and return the results.
class MovieRemoteDataSource @Inject constructor(
    private val movieService: MovieService
)  : BaseDataSource() {

    suspend fun getMovies() = getResult { movieService.getAllMovies() }
    suspend fun getMovie(id : String) = getResult { movieService.getMovie(id) }
}