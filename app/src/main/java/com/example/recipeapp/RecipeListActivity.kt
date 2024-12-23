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

        recipeAdapter = RecipeAdapter(recipeList,
            onRecipeClick = { recipe ->
                // Navigate to RecipeDetailActivity when a recipe is clicked
                Log.d("RecipeListActivity", "Clicked Recipe ID: ${recipe.id}, Name: ${recipe.name}")
                val intent = Intent(this, RecipeDetailActivity::class.java)
                intent.putExtra("recipeId", recipe.id)
                startActivity(intent)
            },
            onDeleteClick = { recipeId ->
                // Handle recipe deletion
                deleteRecipe(recipeId)
            },
            onEditClick = { recipe ->
                // Handle recipe editing
                editRecipe(recipe)
            }
        )

        binding.recyclerViewRecipes.adapter = recipeAdapter
    }

    private fun deleteRecipe(recipeId: Int) {
        dbHelper.deleteRecipe(recipeId)
        Toast.makeText(this, "Recipe deleted successfully!", Toast.LENGTH_SHORT).show()
        refreshRecipeList()
    }

    private fun editRecipe(recipe: Recipe) {
        Log.d("RecipeListActivity", "Editing Recipe ID: ${recipe.id}, Name: ${recipe.name}")
        val intent = Intent(this, EditRecipeActivity::class.java)
        intent.putExtra("recipeId", recipe.id)
        startActivity(intent)
    }

    private fun refreshRecipeList() {
        val updatedRecipeList = dbHelper.getAllRecipes()
        recipeAdapter.updateData(updatedRecipeList)
    }

    override fun onResume() {
        super.onResume()
        // Refresh the recipe list when returning to this activity
        refreshRecipeList()
    }
}
