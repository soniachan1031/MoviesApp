package com.example.moviesapp.view

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.moviesapp.databinding.ActivitySearchDetailsBinding
import com.example.moviesapp.model.Movie
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SearchDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchDetailsBinding
    private lateinit var movie: Movie
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        movie = intent.getSerializableExtra("movie", Movie::class.java) ?: return finish()

        displayMovieDetails()
        setupListeners()
    }

    private fun displayMovieDetails() {
        binding.titleTextView.text = movie.title
        binding.descriptionTextView.text = movie.description

        Glide.with(this)
            .load(movie.poster)
            .into(binding.posterImageView)
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.addToFavButton.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser?.uid

            if (userId == null) {
                Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val movieMap = mapOf(
                "title" to movie.title,
                "year" to movie.year,
                "imdbID" to movie.imdbID,
                "type" to movie.type,
                "poster" to movie.poster,
                "studio" to movie.studio,
                "imdbRating" to movie.imdbRating,
                "description" to movie.description,
                "timestamp" to System.currentTimeMillis()
            )

            Log.d("SearchDetailsActivity", "Adding movie for user $userId: $movieMap")

            db.collection("users")
                .document(userId)
                .collection("favoritemovies")
                .document(movie.imdbID)
                .set(movieMap)
                .addOnSuccessListener {
                    Toast.makeText(this, "Added to favorites!", Toast.LENGTH_SHORT).show()
                    binding.addToFavButton.postDelayed({
                        finish()
                    }, 500)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to add movie.", Toast.LENGTH_SHORT).show()
                }
        }

    }
}
