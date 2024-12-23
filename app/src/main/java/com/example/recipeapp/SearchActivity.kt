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

        // Set up RecyclerView
        binding.recyclerViewSearchResults.layoutManager = LinearLayoutManager(this)

        binding.btnSearch.setOnClickListener {
            val query = binding.SearchQuery.text.toString().trim()
            if (query.isNotEmpty()) {
                performSearch(query)
            } else {
                Toast.makeText(this, "Please enter a search term", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performSearch(query: String) {
        val searchResults = dbHelper.searchRecipes(query)
        if (searchResults.isNotEmpty()) {
            recipeAdapter = RecipeAdapter(searchResults,
                onRecipeClick = { recipe ->
                    // Navigate to RecipeDetailActivity
                    val intent = Intent(this, RecipeDetailActivity::class.java)
                    intent.putExtra("recipeId", recipe.id)
                    startActivity(intent)
                },
                onDeleteClick = { recipeId ->
                    // Delete recipe functionality
                    dbHelper.deleteRecipe(recipeId)
                    Toast.makeText(this, "Recipe deleted successfully!", Toast.LENGTH_SHORT).show()
                    performSearch(query) // Refresh search results
                },
                onEditClick = { recipe ->
                    // Navigate to EditRecipeActivity
                    val intent = Intent(this, EditRecipeActivity::class.java)
                    intent.putExtra("recipeId", recipe.id)
                    startActivity(intent)
                }
            )
            binding.recyclerViewSearchResults.adapter = recipeAdapter
        } else {
            Toast.makeText(this, "No recipes found for \"$query\"", Toast.LENGTH_SHORT).show()
        }
    }
}
