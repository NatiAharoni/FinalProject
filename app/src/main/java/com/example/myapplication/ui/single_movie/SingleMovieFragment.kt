package com.example.myapplication.ui.single_movie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.myapplication.data.models.Movie
import com.example.myapplication.databinding.MovieDetailFragmentBinding
import com.example.myapplication.utils.Loading
import com.example.myapplication.utils.Success
import com.example.myapplication.utils.Error
import com.example.myapplication.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SingleMovieFragment : Fragment() {


    private var binding: MovieDetailFragmentBinding by autoCleared()

    private val viewModel: SingleMovieViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MovieDetailFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


       viewModel.movie.observe(viewLifecycleOwner) {
           when(it.status) {
               is Loading -> binding.progressBar.isVisible = true
               is Success -> {
                   binding.progressBar.isVisible = false
                   lifecycleScope.launch {
                       updateMovie(it.status.data!!)
                   }
               }
               is Error -> {
                   binding.progressBar.isVisible = false
                   Toast.makeText(requireContext(),it.status.message,Toast.LENGTH_SHORT).show()
               }
           }
       }

        arguments?.getString("id")?.let {

            viewModel.setId(it)

        }
    }

    private suspend fun updateMovie(movie: Movie) {

        binding.movieTitle.text = movie.title
        binding.movieYear.text = movie.year
        binding.trailer.text = viewModel.gatTrailer(movie.id).toString()
        Glide.with(requireContext()).load(movie.image).circleCrop().into(binding.itemImage)

    }
}