package com.example.recipeapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.recipeapp.databinding.ActivityRecipeDetailBinding

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeDetailBinding
    private lateinit var dbHelper: DatabaseConnect

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View Binding
        binding = ActivityRecipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("RecipeDetailActivity", "Activity Started")

        // Initialize Database Helper
        dbHelper = DatabaseConnect(this)

        // Retrieve recipe ID from intent
        val recipeId = intent.getIntExtra("recipeId", -1)
        Log.d("RecipeDetailActivity", "Received Recipe ID: $recipeId")

        if (recipeId != -1) {
            // Fetch the recipe and ingredients
            val recipeWithIngredients = dbHelper.getRecipeWithIngredients(recipeId)

            if (recipeWithIngredients != null) {
                val (recipe, ingredients) = recipeWithIngredients
                Log.d("RecipeDetailActivity", "Fetched Recipe: $recipe")
                Log.d("RecipeDetailActivity", "Fetched Ingredients: $ingredients")

                // Display recipe details
                binding.tvRecipeName.text = recipe.name
                binding.tvRecipeInstructions.text = recipe.instructions
                binding.tvIngredients.text = ingredients.joinToString("\n") { "${it.name}: ${it.amount}" }
            } else {
                Toast.makeText(this, "Recipe not found.", Toast.LENGTH_SHORT).show()
                Log.d("RecipeDetailActivity", "Recipe not found for ID: $recipeId")
                finish() // Close activity if recipe not found
            }
        } else {
            Toast.makeText(this, "Invalid recipe ID.", Toast.LENGTH_SHORT).show()
            Log.d("RecipeDetailActivity", "Invalid Recipe ID received")
            finish() // Close activity if no valid recipe ID is provided
        }
    }
}