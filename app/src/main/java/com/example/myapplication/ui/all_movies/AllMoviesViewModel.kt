package com.example.myapplication.ui.all_movies

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
// --- All movies ViewModels: The ViewModels that fetches all the movies from the IMDB API.
//     This park handles the ViewModel of this fragment. ---
@HiltViewModel
class AllMoviesViewModel @Inject constructor(
     movieRepository: MovieRepository,
     private val authRepository: AuthRepository
) :ViewModel() {

    val movies = movieRepository.getMovies()

    // Sign-Out function: Update the repository
    fun signOut() {
        authRepository.logout()
    }
}