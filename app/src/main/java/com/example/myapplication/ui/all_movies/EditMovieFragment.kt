package com.example.myapplication.ui.all_movies

import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.data.models.Movie
import com.example.myapplication.databinding.FragmentEditMovieBinding
import com.example.myapplication.databinding.MovieDetailFragmentBinding
import com.example.myapplication.ui.single_movie.SingleMovieViewModel
import com.example.myapplication.utils.autoCleared


class EditMovieFragment : Fragment() {

    private var binding: FragmentEditMovieBinding by autoCleared()

    private val viewModel: SingleMovieViewModel by viewModels()

    var idTo: String? = null


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

        arguments?.getString("id")?.let {
            viewModel.setId(it)
            idTo = it
            Log.d("idTo", idTo!!)
        }

        viewModel.movie.observe(viewLifecycleOwner){
            updateMovie2(it.status.data!!)
        }
    }

    private fun updateMovie2(movie: Movie) {

        binding.itemTitle.text = Editable.Factory.getInstance().newEditable(movie.title)
        binding.itemYearRelease.text = Editable.Factory.getInstance().newEditable(movie.year)
//        binding.i.text = viewModel.gatTrailer(movie.id).toString()
        Glide.with(requireContext()).load(movie.image).circleCrop().into(binding.resultImage)
        //movieImageF = movie.image
    }

}