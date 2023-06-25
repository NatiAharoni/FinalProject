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


        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)

        val allEntries: Map<String, *> = sharedPref?.all as Map<String, *>



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
            idTo= it
            Log.d("idTo", idTo!!)

            currentMovieIsFavorite = sharedPref?.let { it1 -> isFavorite(idTo!!, it1) }
            if (currentMovieIsFavorite!!) {
                binding.favoriteButton.setImageDrawable(resources.getDrawable(R.drawable.baseline_favorite_24))
            }

        }

        binding.favoriteButton.setOnClickListener {
            val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return@setOnClickListener
            val editor = sharedPref.edit()
            val allEntriesSize = sharedPref.getAll().size

            if (currentMovieIsFavorite!!) {

                binding.favoriteButton.setImageDrawable(resources.getDrawable(R.drawable.baseline_favorite_border_24))
                currentMovieIsFavorite = false

                for ((key, value) in allEntries) {
                    try {
                        val jsonObj = JSONObject(value.toString())
                        if (jsonObj.getString("movieId") == idTo!!) {
                            editor.remove(key)
                            editor.apply()
                        }
                    } catch (e: JSONException) {
                        throw java.lang.RuntimeException(e)
                    }
                }
            }
            else{

                saveMovieData()




                val jasonString = "{\"movieTitle\":" + "\"" + binding.movieTitle.text + "\"" +
                        ",\"movieImage\":" + "\"" + movieImageF.toString() + "\"" +
                        ",\"movieYear\":" + "\"" + binding.movieYear.text +"\""+
                        ",\"movieId\":" + "\"" + idTo
                    .toString() + "\"" + "}"
                // Log.d("jasonString",jasonString)
                Log.d("Save Movie","Save Movie!!!!!!!!!!!!!!!!!!")

                editor.putString(Integer.toString(allEntriesSize + 1), jasonString)
                editor.apply()
                binding.favoriteButton.setImageDrawable(resources.getDrawable(R.drawable.baseline_favorite_24))
            }
        }
    }

    private fun saveMovieData() {

//        val movieId = dbRef.push().key!!
        val movieToDb = Movie(idTo!!,binding.movieTitle.text.toString(),binding.movieYear.text.toString(),movieImageF!!.toString())

        dbRef.child(idTo!!).setValue(movieToDb)
            .addOnCompleteListener {
                Toast.makeText(requireContext(),"Data insert successfully", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(requireContext(),it.message, Toast.LENGTH_SHORT).show()
            }
    }

    private suspend fun updateMovie(movie: Movie) {

        binding.movieTitle.text = movie.title
        binding.movieYear.text = movie.year
        binding.trailer.text = viewModel.gatTrailer(movie.id).toString()
        Glide.with(requireContext()).load(movie.image).circleCrop().into(binding.itemImage)
        movieImageF = movie.image
    }

    private fun isFavorite(movieId: String, sharedPref: SharedPreferences): Boolean {
        val allEntries: Map<String, *> = sharedPref.all

        for ((key, value) in allEntries) {
            try {
                val jsonObj = JSONObject(value.toString())
                if (jsonObj.getString("movieId") == movieId) {
                    return true
                }
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
        }
        return false
    }
}