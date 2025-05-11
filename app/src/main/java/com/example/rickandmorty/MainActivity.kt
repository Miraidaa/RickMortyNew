package com.example.rickandmorty

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.example.rickandmorty.model.Character
import com.example.rickandmorty.model.CharacterResponse
import com.example.rickandmorty.network.RetrofitClient
import com.example.rickandmorty.ui.adapter.CharacterAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {


    private lateinit var recyclerView: RecyclerView
    private lateinit var searchInput: EditText
    private lateinit var adapter: CharacterAdapter
    private var characterList = mutableListOf<Character>()
    private var filteredList = mutableListOf<Character>()
    private var allCharacters = mutableListOf<Character>()



    private var currentPage = 1
    private var isLoading = false
    private lateinit var pageContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        recyclerView = findViewById(R.id.recyclerView)
        searchInput = findViewById(R.id.searchInput)
        pageContainer = findViewById(R.id.pageContainer)


        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CharacterAdapter(characterList)
        recyclerView.adapter = adapter
        recyclerView.visibility = View.VISIBLE


        fetchCharacters(currentPage)

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!isStrictCharacterName(s.toString())) {
                    searchInput.error = "Name must contain only letters!"
                }

                filterCharacters(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

    }

    fun isStrictCharacterName(name: String): Boolean {
        return name.matches(Regex("^[A-Za-z]+$"))
    }
    private fun updatePaginationControls(currentPage: Int) {
        pageContainer.removeAllViews()


        if (currentPage > 1) {
            val prevButton = Button(this)
            prevButton.text = "prev"
            prevButton.setOnClickListener {
                fetchCharacters(currentPage - 1)
            }
            pageContainer.addView(prevButton)
        }


        val currentPageText = TextView(this)
        currentPageText.text = "page $currentPage of $totalPages"
        currentPageText.textSize = 18f
        pageContainer.addView(currentPageText)


        if (currentPage < totalPages) {
            val nextButton = Button(this)
            nextButton.text = "next"
            nextButton.setOnClickListener {
                fetchCharacters(currentPage + 1)
            }
            pageContainer.addView(nextButton)
        }
    }




    private var totalPages = 1

    private fun fetchCharacters(page: Int) {
        isLoading = true

        RetrofitClient.instance.getCharacters(page).enqueue(object : Callback<CharacterResponse> {
            override fun onResponse(call: Call<CharacterResponse>, response: Response<CharacterResponse>) {
                response.body()?.let {
                    totalPages = it.info.pages

                    if (page == 1) {
                        allCharacters.clear()
                    }

                    allCharacters.addAll(it.results)

                    characterList.clear()
                    characterList.addAll(it.results)
                    adapter.notifyDataSetChanged()

                    updatePaginationControls(page)
                }
                isLoading = false
            }

            override fun onFailure(call: Call<CharacterResponse>, t: Throwable) {
                isLoading = false
                Toast.makeText(this@MainActivity, "Failed to load characters", Toast.LENGTH_SHORT).show()
            }
        })
    }






    private fun filterCharacters(query: String) {
        Log.d("MainActivity", "Search Query: $query")

        if (query.isEmpty()) {
            recyclerView.adapter = adapter
            return
        }

        filteredList.clear()
        filteredList.addAll(allCharacters.filter { it.name.contains(query, ignoreCase = true) })

        Log.d("MainActivity", "Filtered List Size: ${filteredList.size}")

        if (filteredList.isEmpty()) {
            findViewById<TextView>(R.id.noResultsText).visibility = View.VISIBLE
        } else {
            findViewById<TextView>(R.id.noResultsText).visibility = View.GONE
        }
        
        val filteredAdapter = CharacterAdapter(filteredList)
        recyclerView.adapter = filteredAdapter
    }

}
