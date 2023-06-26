package com.example.myapplication.ui.single_movie

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.data.models.Movie
import com.example.myapplication.databinding.MovieDetailFragmentBinding
import com.example.myapplication.utils.Error
import com.example.myapplication.utils.Loading
import com.example.myapplication.utils.Success
import com.example.myapplication.utils.autoCleared
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

@AndroidEntryPoint
class SingleMovieFragment : Fragment() {

    private var binding: MovieDetailFragmentBinding by autoCleared()

    private val viewModel: SingleMovieViewModel by viewModels()
    var movieImageF: String? = null

    var currentMovieIsFavorite: Boolean? = null

    val dbRef = FirebaseDatabase.getInstance().getReference("Manager_Movies")

    var idTo: String? = null


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

        // Fetch the selected movie details and show it on screen.
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
                    //Toast.makeText(requireContext(),it.status.message,Toast.LENGTH_SHORT).show()
                }
            }
        }

        arguments?.getString("id")?.let {
            viewModel.setId(it)
            idTo= it
        }

        // Add the movie to the list of selected movies (that all the users can see).
        binding.favoriteButton.setOnClickListener {
                saveMovieData()
                binding.favoriteButton.setImageDrawable(resources.getDrawable(R.drawable.baseline_favorite_24))
        }
    }

    // Add the movie to the list of selected movies (that all the users can see).
    private fun saveMovieData() {

        val movieToDb = Movie(idTo!!,binding.movieTitle.text.toString(),binding.movieYear.text.toString(),movieImageF!!.toString())

        dbRef.child(idTo!!).setValue(movieToDb)
            .addOnCompleteListener {
                Toast.makeText(requireContext(),getString(R.string.DataInsert), Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(requireContext(),it.message, Toast.LENGTH_SHORT).show()
            }
    }

    // Update the UI with the updated data.
    private fun updateMovie(movie: Movie) {

        binding.movieTitle.text = movie.title
        binding.movieYear.text = movie.year
        Glide.with(requireContext()).load(movie.image).into(binding.itemImage)
        movieImageF = movie.image
    }
}