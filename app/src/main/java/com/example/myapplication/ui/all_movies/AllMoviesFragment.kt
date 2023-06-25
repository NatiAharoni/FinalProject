package com.example.myapplication.ui.all_movies

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.data.models.Movie
import com.example.myapplication.databinding.MoviesFragmentBinding
import com.example.myapplication.utils.Error
import com.example.myapplication.utils.Loading
import com.example.myapplication.utils.Success
import com.example.myapplication.utils.autoCleared
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException
import org.json.JSONObject

@AndroidEntryPoint
class AllMoviesFragment : Fragment(), MoviesAdapter.MovieItemListener {

    private var binding : MoviesFragmentBinding by autoCleared()

    private val viewModel: AllMoviesViewModel by viewModels()

    private  lateinit var  adapter: MoviesAdapter

    var atMoviesIsFavorite: Boolean = false

    var editMode:Boolean = false

    private lateinit var dbRef: DatabaseReference

    val mainFirebaseMoviesList : ArrayList<Movie> = ArrayList()




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MoviesFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = MoviesAdapter(this)
        binding.moviesRv.layoutManager = LinearLayoutManager(requireContext())
        binding.moviesRv.adapter = adapter

        loadMovies()

        binding.favoriteButton.setOnClickListener{
            if(!atMoviesIsFavorite){
                atMoviesIsFavorite = true
                binding.favoriteButton.setImageDrawable(resources.getDrawable(R.drawable.baseline_wiht_favorite_24))

                val favoritemMvies:ArrayList<Movie> = ArrayList()
                val sharedPref = activity!!.getPreferences(Context.MODE_PRIVATE)
                // Get all key-value pairs from SharedPreferences
                val allEntries: Map<String, *> = sharedPref.all

                for ((key, value) in allEntries) {
                    try {
                        val jsonObj = JSONObject(value.toString())
                        val movieTitle = jsonObj.getString("movieTitle")
                        val movieImage = jsonObj.getString("movieImage")
                        val movieYear = jsonObj.getString("movieYear")
                        val movieId = jsonObj.getString("movieId")

                        val movieInstance = Movie(
                            movieId,
                            movieTitle,
                            movieYear,
                            movieImage
                        )
                        favoritemMvies.add(movieInstance)
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                }
                adapter.setMovies(favoritemMvies)
            }
            else{
                atMoviesIsFavorite =false
                binding.favoriteButton.setImageDrawable(resources.getDrawable(R.drawable.baseline_wiht_favorite_border_24))


                loadMovies()

            }

        }

        binding.testBtn.setOnClickListener{
            findNavController().navigate(R.id.action_allMoviesFragment_to_allUserMoviesFragment)
        }

        binding.editeFirebaseMoviesBtn.setOnClickListener{
            if(editMode){
                editMode = false
                loadMovies()
            }else{
                editMode = true
                getMainFirebaseMovie2()


            }

        }
    }

    override fun onMovieClick(movieId: String) {
        if (editMode){
            findNavController().navigate(R.id.action_allMoviesFragment_to_editMovieFragment,
                bundleOf("id" to movieId))
        }else{
            findNavController().navigate(
                R.id.action_allMoviesFragment_to_singleMovieFragment,
                bundleOf("id" to movieId))
        }
    }

    fun loadMovies(){
        viewModel.movies.observe(viewLifecycleOwner) {
            when(it.status) {
                is Loading -> binding.progressBar.isVisible = true
                is Success -> {
                    if(!it.status.data.isNullOrEmpty()) {
                        binding.progressBar.isVisible = false
                        adapter.setMovies(ArrayList(it.status.data))
                    }
                }
                is Error -> {
                    binding.progressBar.isVisible = false
                    val note = Snackbar.make(
                        requireView(),
                        it.status.message, Toast.LENGTH_LONG)
                    note.setTextMaxLines(4)
                    note.show()
                    Log.d("Error-API", it.status.message)
                    //Toast.makeText(requireContext(),it.status.message,Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    private fun getMainFirebaseMovie2(){
        binding.progressBar.visibility = View.VISIBLE

        dbRef = FirebaseDatabase.getInstance().getReference("Manager_Movies")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mainFirebaseMoviesList.clear()
                if (snapshot.exists()){
                    for (movieInstance in snapshot.children){
                        Log.d("movieInstance.value",movieInstance.toString())
                        Log.d("movieInstance.value",movieInstance.value.toString())
                        try {
                            val movieData : Movie? = movieInstance.getValue(Movie::class.java)
                            mainFirebaseMoviesList.add(movieData!!)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    adapter.setMovies(mainFirebaseMoviesList)
                    binding.progressBar.visibility = View.GONE
                    //binding.moviesUserRv.visibility = View.VISIBLE

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


    }
}