package com.example.myapplication.ui.single_movie

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.data.models.Movie
import com.example.myapplication.databinding.FragmentUserSingleMovieBinding
import com.example.myapplication.databinding.MovieDetailFragmentBinding
import com.example.myapplication.utils.Error
import com.example.myapplication.utils.Loading
import com.example.myapplication.utils.Success
import com.example.myapplication.utils.autoCleared
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

class UserSingleMovieFragment : Fragment() {

    private var binding: FragmentUserSingleMovieBinding by autoCleared()

    var movieImageF: String? = null

    private lateinit var dbRefUser: DatabaseReference

    private var movieId:String? = null

    var userEmail:String? = null
    var movieTitle:String? = null
    var movieImage:String? = null
    var movieYear:String? = null

    var movieSelect: Movie? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Fetch the data from the previous fragment (AllUserMovies), cast it to JSON object.
        // Update the UI with the updated data.
        arguments?.getString("idAndEmailAndMovie")?.let {
            try {
                val jsonObj = JSONObject(it)
                movieId = jsonObj.getString("movieId")
                userEmail = jsonObj.getString("email")
                movieTitle = jsonObj.getString("movieTitle")
                movieImage = jsonObj.getString("movieImage")
                movieYear = jsonObj.getString("movieYear")

                dbRefUser = FirebaseDatabase.getInstance().getReference(userEmail!!.replace(".", ",")
                    .replace("#", ",").replace("\$", ",")
                    .replace("'[", ",").replace("]", ","))

                movieSelect = Movie(movieId!!,movieTitle!!,movieYear!!,movieImage!!)
                updateMovie(movieSelect!!)

            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
        }

        // Save the selected movie (favorite) to the user's Firebase.
        binding.userFavoriteButton.setOnClickListener {

            binding.userFavoriteButton.setImageDrawable(resources.getDrawable(R.drawable.baseline_favorite_24))

            dbRefUser.child(movieId!!).setValue(movieSelect)
                .addOnCompleteListener {
                    Toast.makeText(requireContext(),getString(R.string.DataInsert), Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(requireContext(),it.message, Toast.LENGTH_SHORT).show()
                }
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserSingleMovieBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Update the UI with the updated data.
    private fun updateMovie(movie: Movie) {

        binding.userMovieTitle.text = movie.title
        binding.userMovieYear.text = movie.year
        Glide.with(requireContext()).load(movie.image).into(binding.userItemImage)
        movieImageF = movie.image
    }

}