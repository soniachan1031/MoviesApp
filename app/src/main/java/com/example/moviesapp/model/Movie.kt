package com.example.moviesapp.model

import java.io.Serializable

data class Movie(
    val title: String = "",
    val studio: String = "",         // NEW: studio name
    val imdbRating: String = "",     // NEW: critics rating
    val year: String = "",
    val imdbID: String = "",
    val type: String = "",
    val poster: String = "",
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis() // Add timestamp field
) : Serializable {
    constructor() : this("", "", "", "", "", "", "", "")
}