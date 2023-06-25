package com.example.myapplication.ui.all_movies

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

@HiltViewModel
class AllMoviesViewModel @Inject constructor(
     movieRepository: MovieRepository
) :ViewModel() {

    val movies = movieRepository.getMovies()
}