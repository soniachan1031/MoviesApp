package com.example.moviesapp.api

import android.util.Log
import com.example.moviesapp.model.Movie
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class OmdbApiService {
    private val apiKey = "856984be"
    private val client = OkHttpClient()

    fun searchMovies(query: String, callback: (List<Movie>?) -> Unit) {
        val url = "https://www.omdbapi.com/?s=$query&apiKey=$apiKey"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                val result = mutableListOf<Movie>()
                response.body?.string()?.let { jsonString ->
                    val json = JSONObject(jsonString)
                    if (json.getString("Response") == "True") {
                        val searchArray = json.getJSONArray("Search")
                        val scope = CoroutineScope(Dispatchers.IO)
                        scope.launch {
                            for (i in 0 until searchArray.length()) {
                                val obj = searchArray.getJSONObject(i)
                                val movieId = obj.getString("imdbID")
                                val details = fetchMovieDetails(movieId)
                                result.add(
                                    Movie(
                                        title = obj.getString("Title"),
                                        studio = details?.optString("Production", "N/A") ?: "N/A",
                                        imdbRating = details?.optString("imdbRating", "N/A") ?: "N/A",
                                        year = obj.getString("Year"),
                                        imdbID = obj.getString("imdbID"),
                                        type = obj.getString("Type"),
                                        poster = obj.getString("Poster"),
                                        description = details?.optString("Plot", "No description available") ?: "No description available"
                                    )
                                )
                            }
                            withContext(Dispatchers.Main) {
                                callback(result)
                            }
                        }
                    } else {
                        callback(null)
                    }
                }
            }
        })
    }

    private suspend fun fetchMovieDetails(movieId: String): JSONObject? {
        return withContext(Dispatchers.IO) {
            try {
                val url = "https://www.omdbapi.com/?i=$movieId&apikey=856984be"
                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@use null
                    response.body?.string()?.let { JSONObject(it) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}