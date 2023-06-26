package com.example.myapplication.data.remote_db

import com.example.myapplication.data.models.AllMovies
import com.example.myapplication.data.models.Movie
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
// MovieService interface: defines API endpoints for interacting with a movie service.
// It allows you to retrieve movies from the top 250 movies list,and retrieve a specific movie by its ID.
// The functions are defined as suspend functions to be called from coroutines,
// and the responses are wrapped in Response objects to handle the API response.
interface MovieService {
    @GET("en/API/Top250Movies/k_kvgsohdk/")
    suspend fun getAllMovies() : Response<AllMovies>

    @GET("movie/{id}")
    suspend fun getMovie(@Path("id")id :String) : Response<Movie>
}
