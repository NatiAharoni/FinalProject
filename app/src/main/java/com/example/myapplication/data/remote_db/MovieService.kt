package com.example.myapplication.data.remote_db

import com.example.myapplication.data.models.AllMovies
import com.example.myapplication.data.models.Movie
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface MovieService {
    @GET("en/API/Top250Movies/k_x0nwd8tl/")
    suspend fun getAllMovies() : Response<AllMovies>

    @GET("movie/{id}")
    suspend fun getMovie(@Path("id")id :String) : Response<Movie>

    @GET("API/Trailer/k_x0nwd8tl/{id}")
    suspend fun getMovieTrailer(@Path("id")id :String) : Response<String>
}