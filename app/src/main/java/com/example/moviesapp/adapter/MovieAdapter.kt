package com.example.moviesapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moviesapp.R
import com.example.moviesapp.databinding.ItemMovieBinding
import com.example.moviesapp.model.Movie

class MovieAdapter(private val movies: List<Movie>, private val onClick: (Movie) -> Unit) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    inner class MovieViewHolder(val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun getItemCount(): Int = movies.size

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.binding.titleTextView.text = movie.title
        holder.binding.studioTextView.text = "Studio: ${movie.studio}"
        holder.binding.ratingTextView.text = "Rating: ${movie.imdbRating}"
        holder.binding.yearTextView.text = "Year: ${movie.year}"

        if (movie.poster != "N/A") {
            Glide.with(holder.itemView.context)
                .load(movie.poster)
                .into(holder.binding.posterImageView)
        } else {
            holder.binding.posterImageView.setImageResource(R.drawable.ic_movie_placeholder)
        }

        holder.itemView.setOnClickListener {
            onClick(movie)
        }
    }
}
