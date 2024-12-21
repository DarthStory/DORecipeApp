package com.example.recipeapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipeapp.databinding.ActivityRecipeListBinding

class RecipeListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeListBinding
    private lateinit var dbHelper: DatabaseConnect
    private lateinit var recipeAdapter: RecipeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View Binding
        binding = ActivityRecipeListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Database Helper
        dbHelper = DatabaseConnect(this)

        // Load and display recipes
        loadRecipes()

        // Initialize RecyclerView
        binding.recyclerViewRecipes.layoutManager = LinearLayoutManager(this)
    }

    private fun loadRecipes() {
        val recipeList = dbHelper.getAllRecipes()

        if (recipeList.isEmpty()) {
            Toast.makeText(this, "No recipes available.", Toast.LENGTH_SHORT).show()
            return
        }

        recipeAdapter = RecipeAdapter(recipeList, { recipe ->
            // Navigate to RecipeDetailActivity when a recipe is clicked
            Log.d("RecipeListActivity", "Clicked Recipe ID: ${recipe.id}, Name: ${recipe.name}")
            val intent = Intent(this, RecipeDetailActivity::class.java)
            intent.putExtra("recipeId", recipe.id) // Ensure ID is an Int
            startActivity(intent)
        }, { recipeId ->
            // Handle recipe deletion
            deleteRecipe(recipeId)
        })

        binding.recyclerViewRecipes.adapter = recipeAdapter
    }

    private fun deleteRecipe(recipeId: Int) {
        dbHelper.deleteRecipe(recipeId)
        Toast.makeText(this, "Recipe deleted successfully!", Toast.LENGTH_SHORT).show()
        refreshRecipeList()
    }

    private fun refreshRecipeList() {
        val updatedRecipeList = dbHelper.getAllRecipes()
        recipeAdapter.updateData(updatedRecipeList)
    }
}
