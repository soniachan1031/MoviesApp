package com.example.moviesapp.view

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moviesapp.adapter.MovieAdapter
import com.example.moviesapp.databinding.FragmentFavoritesBinding
import com.example.moviesapp.model.Movie
import com.example.moviesapp.viewmodel.MainViewModel

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()
    private val favoriteMovies = mutableListOf<Movie>()
    private lateinit var movieAdapter: MovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadFavorites()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter(favoriteMovies) { movie ->
            val intent = Intent(requireContext(), FavoritesDetailsActivity::class.java)
            intent.putExtra("movie", movie)
            startActivity(intent)
        }

        binding.movieRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = movieAdapter
        }
    }

    private fun setupObservers() {
        viewModel.favoriteMovies.observe(viewLifecycleOwner) {
            favoriteMovies.clear()
            favoriteMovies.addAll(it)
            movieAdapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
