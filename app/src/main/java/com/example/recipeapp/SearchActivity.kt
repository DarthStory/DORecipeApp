package com.example.recipeapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipeapp.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var dbHelper: DatabaseConnect
    private lateinit var recipeAdapter: RecipeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View Binding
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Database Helper
        dbHelper = DatabaseConnect(this)

        // Handle Search Input
        binding.btnSearchSubmit.setOnClickListener {
            val query = binding.etSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                val searchResults = dbHelper.searchRecipes(query)
                if (searchResults.isEmpty()) {
                    Toast.makeText(this, "No results found for \"$query\"", Toast.LENGTH_SHORT).show()
                } else {
                    setupRecyclerView(searchResults)
                }
            } else {
                Toast.makeText(this, "Please enter a search term", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView(recipes: List<Recipe>) {
        recipeAdapter = RecipeAdapter(recipes) { recipe ->
            val intent = Intent(this, RecipeDetailActivity::class.java)
            intent.putExtra("recipeId", recipe.id)
            startActivity(intent)
        }
        binding.recyclerViewSearchResults.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = recipeAdapter
        }
    }
}
