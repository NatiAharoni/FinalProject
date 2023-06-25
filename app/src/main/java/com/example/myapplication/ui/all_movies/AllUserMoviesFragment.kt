package com.example.myapplication.ui.all_movies

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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


@AndroidEntryPoint
class AllUserMoviesFragment : Fragment(), UserMovieAdapter.MovieItemListener {

    private var binding : FragmentAllUserMoviesBinding by autoCleared()

    private val viewModel: AllMoviesViewModel by viewModels()

    private  lateinit var  adapter: UserMovieAdapter

    val mainFirebaseMoviesList : ArrayList<Movie> = ArrayList()

    private lateinit var dbRef: DatabaseReference



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAllUserMoviesBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = UserMovieAdapter(this)
        binding.moviesUserRv.layoutManager = LinearLayoutManager(requireContext())
        binding.moviesUserRv.adapter = adapter

        getMainFirebaseMovie()

    }

    override fun onMovieClick(movieId: String) {
        findNavController().navigate(
            R.id.action_allUserMoviesFragment_to_singleMovieFragment,
            bundleOf("id" to movieId)
        )    }

    private fun getMainFirebaseMovie(){
        binding.progressUserBar.visibility = View.VISIBLE

        dbRef = FirebaseDatabase.getInstance().getReference("Manager_Movies")

        dbRef.addValueEventListener(object : ValueEventListener{
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
                    binding.progressUserBar.visibility = View.GONE
                    //binding.moviesUserRv.visibility = View.VISIBLE

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


    }
}