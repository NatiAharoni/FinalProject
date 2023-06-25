package com.example.myapplication.ui.single_movie

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.example.myapplication.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SingleMovieViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : ViewModel() {


    private val _id = MutableLiveData<String>()


    val movie = _id.switchMap {
        movieRepository.getMovie(it)
    }


    fun setId(id: String) {
        _id.value = id
    }
}