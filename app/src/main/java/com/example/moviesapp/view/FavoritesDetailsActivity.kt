package com.example.moviesapp.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.moviesapp.databinding.ActivityFavoritesDetailsBinding
import com.example.moviesapp.model.Movie
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FavoritesDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritesDetailsBinding
    private lateinit var movie: Movie
    private val db = FirebaseFirestore.getInstance()
    private val userId by lazy { FirebaseAuth.getInstance().currentUser?.uid }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        movie = intent.getSerializableExtra("movie", Movie::class.java) ?: return finish()

        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        displayMovieDetails()
        setupListeners()
    }

    private fun displayMovieDetails() {
        db.collection("users")
            .document(userId!!)
            .collection("favoritemovies")
            .document(movie.imdbID)
            .get()
            .addOnSuccessListener { document ->
                val latestMovie = document.toObject(Movie::class.java)

                binding.titleTextView.text = latestMovie?.title ?: movie.title
                binding.studioTextView.text = latestMovie?.studio ?: movie.studio
                binding.yearTextView.text = latestMovie?.year ?: movie.year
                binding.ratingTextView.text = latestMovie?.imdbRating ?: movie.imdbRating
                binding.descriptionEditText.setText(latestMovie?.description ?: movie.description)

                Glide.with(this)
                    .load(latestMovie?.poster ?: movie.poster)
                    .into(binding.posterImageView)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load details", Toast.LENGTH_SHORT).show()
                fallbackToOriginalMovie()
            }
    }

    private fun fallbackToOriginalMovie() {
        binding.titleTextView.text = movie.title
        binding.studioTextView.text = movie.studio
        binding.ratingTextView.text = movie.imdbRating
        binding.yearTextView.text = movie.year
        binding.descriptionEditText.setText(movie.description)

        Glide.with(this)
            .load(movie.poster)
            .into(binding.posterImageView)
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.updateButton.setOnClickListener {
            val newDescription = binding.descriptionEditText.text.toString().trim()
            if (newDescription.isEmpty()) {
                Toast.makeText(this, "Description cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            db.collection("users")
                .document(userId!!)
                .collection("favoritemovies")
                .document(movie.imdbID)
                .update("description", newDescription)
                .addOnSuccessListener {
                    Toast.makeText(this, "Description updated", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show()
                }
        }

        binding.deleteButton.setOnClickListener {
            db.collection("users")
                .document(userId!!)
                .collection("favoritemovies")
                .document(movie.imdbID)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Deleted from favorites", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
