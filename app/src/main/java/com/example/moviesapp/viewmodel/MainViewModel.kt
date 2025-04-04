package com.example.moviesapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moviesapp.api.OmdbApiService
import com.example.moviesapp.model.Movie
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MainViewModel : ViewModel() {

    private val _searchResults = MutableLiveData<List<Movie>?>()
    val searchResults: LiveData<List<Movie>?> get() = _searchResults

    private val _favoriteMovies = MutableLiveData<List<Movie>>()
    val favoriteMovies: LiveData<List<Movie>> get() = _favoriteMovies

    private val db = FirebaseFirestore.getInstance()

    fun searchMovies(api: OmdbApiService, query: String) {
        api.searchMovies(query) { results ->
            _searchResults.postValue(results)
        }
    }

    fun loadFavorites() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return;

        db.collection("users")
            .document(userId)
            .collection("favoritemovies")
            .orderBy("timestamp", Query.Direction.DESCENDING) // Order by timestamp
            .get()
            .addOnSuccessListener { snapshot ->
                val movies = snapshot.documents.mapNotNull { doc ->
                    try {
                        Movie(
                            title = doc.getString("title") ?: "",
                            year = doc.getString("year") ?: "",
                            imdbID = doc.getString("imdbID") ?: "",
                            type = doc.getString("type") ?: "",
                            poster = doc.getString("poster") ?: "",
                            studio = doc.getString("studio") ?: "N/A",
                            imdbRating = doc.getString("imdbRating") ?: "N/A",
                            description = doc.getString("description") ?: "No description",
                            timestamp = doc.getLong("timestamp") ?: 0L
                        )

                    } catch (e: Exception) {
                        null
                    }
                }
                val limitedList = movies.take(20) // Limit to 20 items
                _favoriteMovies.postValue(limitedList)
            }
            .addOnFailureListener {
                _favoriteMovies.postValue(emptyList())
            }
    }
}
