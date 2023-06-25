package com.example.myapplication.ui.all_movies

import android.os.Bundle
import android.util.Log
import android.view.*
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

// --- All movies fragment: The fragment that fetches all the movies that the ADMIN selected.
//     This fragment is accessible for every user.
//     This part handles the UI of this fragment. ---
@AndroidEntryPoint
class AllMoviesFragment : Fragment(), MoviesAdapter.MovieItemListener {

    private var binding : MoviesFragmentBinding by autoCleared()

    private val viewModel: AllMoviesViewModel by viewModels()

    private  lateinit var  adapter: MoviesAdapter

    var editMode : Boolean = false

    private lateinit var dbRef: DatabaseReference

    val mainFirebaseMoviesList : ArrayList<Movie> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        
        binding = MoviesFragmentBinding.inflate(inflater,container,false)

        //Check if the admin is on edit mode, and load the data according to the mode.
        // if on edit mode - show the movies from firebase (selected movies), else - show all the movies from API
        if(editMode){
            getMainFirebaseMovie2()
        }else{
            loadAPIMovies()
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = MoviesAdapter(this)
        binding.moviesRv.layoutManager = LinearLayoutManager(requireContext())
        binding.moviesRv.adapter = adapter

        binding.editeFirebaseMoviesBtn.setOnClickListener{
            if(editMode){
                editMode = false
                loadAPIMovies()
            }else{
                editMode = true
                getMainFirebaseMovie2()
            }
        }
    }

    override fun onMovieClick(movie: Movie) {
        // Check if the admin is on edit mode. If so, save the data of the movie in JSON string and pass it to editMovie fragment
        if (editMode){
            val jasonString = "{\"movieTitle\":" + "\"" + movie.title + "\"" +
                    ",\"movieImage\":" + "\"" + movie.image + "\"" +
                    ",\"movieYear\":" + "\"" + movie.year +"\""+
                    ",\"movieId\":" + "\"" + movie.id + "\"" + "}"
            findNavController().navigate(R.id.action_allMoviesFragment_to_editMovieFragment,
                bundleOf("movie" to jasonString))
        }else{
            findNavController().navigate(
                R.id.action_allMoviesFragment_to_singleMovieFragment,
                bundleOf("id" to movie.id))
        }
    }

    // Load all the movies from the API to the UI (using the viewModel and the adapter)
    fun loadAPIMovies(){
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
                }
            }
        }
    }


    // Load all the movies the admin selected from Firebase
    private fun getMainFirebaseMovie2(){
        binding.progressBar.visibility = View.VISIBLE

        dbRef = FirebaseDatabase.getInstance().getReference("Manager_Movies")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mainFirebaseMoviesList.clear()
                if (snapshot.exists()){
                    for (movieInstance in snapshot.children){
                        try {
                            val movieData : Movie? = movieInstance.getValue(Movie::class.java)
                            mainFirebaseMoviesList.add(movieData!!)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    adapter.setMovies(mainFirebaseMoviesList)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        binding.progressBar.visibility = View.GONE
    }

    // Logout function using the top menu bar
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_sign_out) {
            viewModel.signOut()
            findNavController().navigate(R.id.action_allMoviesFragment_to_loginFragment)
        }
        return super.onOptionsItemSelected(item)
    }
}