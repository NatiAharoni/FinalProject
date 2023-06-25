package com.example.myapplication.ui.all_movies

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.data.models.Movie
import com.example.myapplication.databinding.FragmentAllUserMoviesBinding
import com.example.myapplication.utils.autoCleared
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import safeCall
// --- All USER movies fragment: The fragment that fetches all the movies from the IMDB API.
//     This fragment is accessible only for the admin. The admin can select from these movies
//     which ones the regular users will see
//     This part handles the UI of this fragment. ---
@AndroidEntryPoint
class AllUserMoviesFragment : Fragment(), UserMovieAdapter.MovieItemListener {

    private var binding : FragmentAllUserMoviesBinding by autoCleared()

    private val viewModel: AllMoviesViewModel by viewModels()

    private  lateinit var  adapter: UserMovieAdapter

    val mainFirebaseMoviesList : ArrayList<Movie> = ArrayList()

    private lateinit var dbRef: DatabaseReference

    var userEmail:String? = null

    var isFavorite:Boolean = false



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        binding = FragmentAllUserMoviesBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = UserMovieAdapter(this)
        binding.moviesUserRv.layoutManager = LinearLayoutManager(requireContext())
        binding.moviesUserRv.adapter = adapter

        userEmail =  arguments?.getString("email")

        getMainFirebaseMovie("Manager_Movies")

        binding.userFavoriteButton.setOnClickListener {
            // In case the user chose to go to favorite (that's means that now isFavorite is false) - load all the movies related to the user.
            if(!isFavorite){
                getMainFirebaseMovie(userEmail + "")
                binding.userFavoriteButton.setImageDrawable(resources.getDrawable(R.drawable.baseline_wiht_favorite_24))
                isFavorite = true

            }
            // In case the user chose to return to the admin's selected movies - load all movies from the admin's list (from Firebase).
            else{
                getMainFirebaseMovie("Manager_Movies")
                binding.userFavoriteButton.setImageDrawable(resources.getDrawable(R.drawable.baseline_wiht_favorite_border_24))
                isFavorite = false

            }


        }

    }

    // when a user click on a movie - save the data of the user and the movie for the SingleMovie fragment.
    // So we can save the data of the movie by the user's details.
    override fun onMovieClick(movie: Movie) {
        val idAndEmail  = "{\"movieId\":" + "\"" + movie.id + "\"" +
        ",\"email\":" + "\"" +  arguments?.getString("email") + "\"" +
                ",\"movieTitle\":" + "\"" + movie.title + "\"" +
                ",\"movieImage\":" + "\"" + movie.image + "\"" +
                ",\"movieYear\":" + "\"" + movie.year +"\""+
                ",\"movieId\":" + "\"" + movie.id + "\"" + "}"
        Log.d("idAndEmailAndMovie",idAndEmail)
        findNavController().navigate(
            R.id.action_allUserMoviesFragment_to_userSingleMovieFragment,
            bundleOf("idAndEmailAndMovie" to idAndEmail)
        )
    }

    // This function fetches all the movies from a specific user.
    // When we want to show all the movies the admin selected - we'll pass the admin's email.
    private fun getMainFirebaseMovie(path:String){

        binding.progressUserBar.visibility = View.VISIBLE

        dbRef = FirebaseDatabase.getInstance().getReference(path.replace(".", ",")
            .replace("#", ",").replace("\$", ",")
            .replace("'[", ",").replace("]", ","))

        dbRef.addValueEventListener(object : ValueEventListener{
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
        binding.progressUserBar.visibility = View.GONE
    }
    // Logout function using the top menu bar
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_sign_out) {
            viewModel.signOut()
            findNavController().navigate(R.id.action_allUserMoviesFragment_to_loginFragment)
        }
        return super.onOptionsItemSelected(item)
    }
}