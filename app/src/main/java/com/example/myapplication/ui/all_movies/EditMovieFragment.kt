package com.example.myapplication.ui.all_movies

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.data.models.Movie
import com.example.myapplication.databinding.FragmentEditMovieBinding
import com.example.myapplication.ui.single_movie.SingleMovieViewModel
import com.example.myapplication.utils.autoCleared
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONException
import org.json.JSONObject

class EditMovieFragment : Fragment() {

    private var binding: FragmentEditMovieBinding by autoCleared()

    private val viewModel: SingleMovieViewModel by viewModels()

    var idTo: String? = null
    lateinit var movie: Movie

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditMovieBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getString("movie")?.let {

            try {
                val jsonObj = JSONObject(it)
                val movieTitle = jsonObj.getString("movieTitle")
                val movieImage = jsonObj.getString("movieImage")
                val movieYear = jsonObj.getString("movieYear")
                val movieId = jsonObj.getString("movieId")

                movie = Movie(
                    movieId,
                    movieTitle,
                    movieYear,
                    movieImage
                )
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            Log.d("idTo", movie.toString())
        }
        updateMovie2(movie)




        binding.updateBtn.setOnClickListener {
            val dbRef = FirebaseDatabase.getInstance().getReference("Manager_Movies").child(movie.id)
            val upTitel = binding.itemTitle.text.toString()
            val upYear = binding.itemYearRelease.text.toString()
            val upId = movie.id
            val upUrlImage = movie.image
            val movieUpdate = Movie(upId, upTitel, upYear, upUrlImage)
            dbRef.setValue(movieUpdate).addOnCompleteListener {
                Toast.makeText(requireContext(),getString(R.string.updateData), Toast.LENGTH_SHORT).show()

                findNavController().navigate(R.id.action_editMovieFragment_to_allMoviesFragment)

            }.addOnFailureListener {
                Toast.makeText(requireContext(),it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateMovie2(movie: Movie) {

        binding.itemTitle.text = Editable.Factory.getInstance().newEditable(movie.title)
        binding.itemYearRelease.text = Editable.Factory.getInstance().newEditable(movie.year)
//        binding.i.text = viewModel.gatTrailer(movie.id).toString()
        Glide.with(requireContext()).load(movie.image).into(binding.updateImage)
        //movieImageF = movie.image

    }

}