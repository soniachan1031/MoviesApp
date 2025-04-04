package com.example.moviesapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moviesapp.api.OmdbApiService
import com.example.moviesapp.databinding.FragmentSearchBinding
import com.example.moviesapp.model.Movie
import com.example.moviesapp.adapter.MovieAdapter
import com.example.moviesapp.viewmodel.MainViewModel
import android.content.Intent

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var movieAdapter: MovieAdapter
    private val searchResults = mutableListOf<Movie>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter(searchResults) { movie ->
            val intent = Intent(requireContext(), SearchDetailsActivity::class.java)
            intent.putExtra("movie", movie)
            startActivity(intent)
        }
        binding.movieRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = movieAdapter
        }
    }

    private fun setupObservers() {
        viewModel.searchResults.observe(viewLifecycleOwner) {
            searchResults.clear()
            if (it != null) {
                searchResults.addAll(it)
            } else {
                Toast.makeText(requireContext(), "Search failed", Toast.LENGTH_SHORT).show()
            }
            movieAdapter.notifyDataSetChanged()
        }
    }

    private fun setupListeners() {
        binding.searchButton.setOnClickListener {
            val query = binding.searchEditText.text.toString().trim()
            if (query.isNotEmpty()) {
                val api = OmdbApiService()
                viewModel.searchMovies(api, query)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
