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
import android.view.inputmethod.EditorInfo
import com.example.moviesapp.R
import android.text.Editable
import android.text.TextWatcher


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
        setupRecentChips()
    }
    private fun setupRecentChips() {
        val recentKeywords = listOf("Batman", "Spiderman", "Avengers", "Marvel")
        val chipGroup = binding.recentChipGroup

        chipGroup.removeAllViews()

        recentKeywords.forEach { keyword ->
            val chip = com.google.android.material.chip.Chip(requireContext()).apply {
                text = keyword
                isClickable = true
                isCheckable = false
                chipBackgroundColor = resources.getColorStateList(R.color.pill_background, null)
                setTextColor(resources.getColor(R.color.text_primary, null))
                textSize = 14f
                setPadding(24, 8, 24, 8)
                chipCornerRadius = 32f
                elevation = 4f
                setOnClickListener {
                    binding.searchEditText.setText(keyword)
                    binding.searchEditText.setSelection(keyword.length)
                    performSearch(keyword)
                }
            }
            chipGroup.addView(chip)
        }
    }


    private fun performSearch(query: String) {
        if (query.isNotEmpty()) {
            val api = OmdbApiService()
            viewModel.searchMovies(api, query)
        }
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
        // Search action when user presses Enter
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.searchEditText.text.toString().trim()
                if (query.isNotEmpty()) {
                    val api = OmdbApiService()
                    viewModel.searchMovies(api, query)
                }
                true
            } else {
                false
            }
        }

        // Hide header and chips when typing
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isTyping = !s.isNullOrEmpty()

                binding.searchTitle.visibility = if (isTyping) View.GONE else View.VISIBLE
                binding.searchSubtitle.visibility = if (isTyping) View.GONE else View.VISIBLE
                binding.recentChipGroup.visibility = if (isTyping) View.GONE else View.VISIBLE
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
