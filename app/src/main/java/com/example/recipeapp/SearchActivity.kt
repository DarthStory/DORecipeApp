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

        // Retrieve the search query from the intent
        val searchQuery = intent.getStringExtra("searchQuery") ?: ""
        if (searchQuery.isEmpty()) {
            Toast.makeText(this, "Search query is empty", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Fetch search results
        val searchResults = dbHelper.searchRecipes(searchQuery)

        if (searchResults.isEmpty()) {
            Toast.makeText(this, "No recipes found for \"$searchQuery\"", Toast.LENGTH_SHORT).show()
            return
        }

        // Initialize RecyclerView
        recipeAdapter = RecipeAdapter(searchResults, { recipe ->
            // Navigate to RecipeDetailActivity when a recipe is clicked
            val intent = Intent(this, RecipeDetailActivity::class.java)
            intent.putExtra("recipeId", recipe.id)
            startActivity(intent)
        }, { recipeId ->
            // Delete recipe functionality (optional in SearchActivity)
            dbHelper.deleteRecipe(recipeId)
            Toast.makeText(this, "Recipe deleted successfully!", Toast.LENGTH_SHORT).show()
            refreshSearchResults(searchQuery)
        })

        binding.recyclerViewSearchResults.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = recipeAdapter
        }
    }

    private fun refreshSearchResults(query: String) {
        val updatedSearchResults = dbHelper.searchRecipes(query)
        recipeAdapter.updateData(updatedSearchResults)
    }
}
