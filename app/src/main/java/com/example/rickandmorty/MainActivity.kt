package com.example.rickandmorty

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.paging.LoadState
import androidx.paging.filter
import com.example.rickandmorty.network.RetrofitClient
import com.example.rickandmorty.ui.adapter.CharacterAdapter
import com.example.rickandmorty.ui.viewmodel.CharacterViewModel
import com.example.rickandmorty.ui.CharacterViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchInput: EditText
    private lateinit var adapter: CharacterAdapter
    private lateinit var viewModel: CharacterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        searchInput = findViewById(R.id.searchInput)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = CharacterAdapter()
        recyclerView.adapter = adapter

        val apiService = RetrofitClient.instance
        val viewModelFactory = CharacterViewModelFactory(apiService)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CharacterViewModel::class.java)

        lifecycleScope.launch {
            viewModel.characterFlow.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterCharacters(s.toString())
            }
        })

        adapter.addLoadStateListener { loadStates ->
            when (loadStates.refresh) {
                is LoadState.Loading -> Toast.makeText(this, "Loading characters...", Toast.LENGTH_SHORT).show()
                is LoadState.Error -> Toast.makeText(this, "Failed to load characters!", Toast.LENGTH_SHORT).show()
                is LoadState.NotLoading -> Unit
            }
        }
    }

    private fun filterCharacters(query: String) {
        lifecycleScope.launch {
            viewModel.characterFlow.collectLatest { pagingData ->
                val filteredPagingData = pagingData.filter { it.name.contains(query, ignoreCase = true) }
                adapter.submitData(filteredPagingData)
            }
        }
    }
}
