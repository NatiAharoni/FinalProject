package com.example.myapplication.ui.all_movies

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.data.models.Movie
import com.example.myapplication.databinding.ItemMovieBinding
// UserMoviesAdapter class: provides the necessary functionality to display a list of movies in a
// RecyclerView and handle click events on the movie items using the provided listener interface.
// This movie adapter fitted to "AllUserMovies" fragment, that's accessible for all users.
class UserMovieAdapter(private val listener : MovieItemListener) :
    RecyclerView.Adapter<UserMovieAdapter.MovieViewHolder>() {

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
            Log.d("imge",item.image)
            Glide.with(itemBinding.root).load(item.image).into(itemBinding.itemImage)
        }
        override fun onClick(v: View?) {

            listener.onMovieClick(movie)
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
        fun onMovieClick(movie : Movie)
    }
}
