package com.example.myapplication.ui.all_movies

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.data.models.Movie
import com.example.myapplication.databinding.ItemMovieBinding

class MoviesAdapter(private val listener : MovieItemListener) :
    RecyclerView.Adapter<MoviesAdapter.MovieViewHolder>() {

    private val movies = ArrayList<Movie>()

    class MovieViewHolder(private val itemBinding: ItemMovieBinding,
                              private val listener: MovieItemListener
    )
        : RecyclerView.ViewHolder(itemBinding.root),
        View.OnClickListener {

        private lateinit var movie: Movie

        init {
            itemBinding.root.setOnClickListener(this)
        }

        fun bind(item: Movie) {

            this.movie = item
            itemBinding.movieTitle.text = item.title
            itemBinding.MovieYear.text = item.year
            Glide.with(itemBinding.root).load(item.image).into(itemBinding.itemImage)
        }
        override fun onClick(v: View?) {

            listener.onMovieClick(movie.id)
        }
    }

    fun setMovies(movies : Collection<Movie>) {
        this.movies.clear()
        this.movies.addAll(movies)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MovieViewHolder(binding,listener)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) =
        holder.bind(movies[position])


    override fun getItemCount() = movies.size

    interface MovieItemListener {
        fun onMovieClick(movieId : String)
    }
}