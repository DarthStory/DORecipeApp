package com.example.recipeapp

import IngredientAdapter
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipeapp.databinding.ActivityEditRecipeBinding

class EditRecipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditRecipeBinding
    private lateinit var dbHelper: DatabaseConnect
    private var ingredientAdapter: IngredientAdapter? = null

    private var recipeId: Int = -1
    private val ingredientList = mutableListOf<Ingredient>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View Binding
        binding = ActivityEditRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Database Helper
        dbHelper = DatabaseConnect(this)

        // Get Recipe ID from Intent
        recipeId = intent.getIntExtra("recipeId", -1)

        if (recipeId == -1) {
            Log.e("EditRecipeActivity", "Invalid recipe ID received")
            Toast.makeText(this, "Invalid recipe ID.", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Log.d("EditRecipeActivity", "Loaded Recipe ID: $recipeId")
            loadRecipeData()
        }

        // Initialize RecyclerView for Ingredients
        initializeRecyclerView()

        // Add New Ingredient
        binding.btnAddIngredient.setOnClickListener {
            showAddIngredientDialog()
        }

        // Save Recipe
        binding.btnSaveRecipe.setOnClickListener {
            saveRecipe()
        }
    }

    private fun initializeRecyclerView() {
        ingredientAdapter = IngredientAdapter(ingredientList) { position ->
            val removedIngredient = ingredientList[position]
            ingredientList.removeAt(position)
            ingredientAdapter?.notifyItemRemoved(position)
            Log.d("EditRecipeActivity", "Removed ingredient: $removedIngredient")
        }
        binding.recyclerViewIngredients.apply {
            layoutManager = LinearLayoutManager(this@EditRecipeActivity)
            adapter = ingredientAdapter
        }
    }

    private fun loadRecipeData() {
        val recipeWithIngredients = dbHelper.getRecipeWithIngredients(recipeId)

        if (recipeWithIngredients != null) {
            val (recipe, ingredients) = recipeWithIngredients
            Log.d("EditRecipeActivity", "Recipe loaded: $recipe")
            Log.d("EditRecipeActivity", "Ingredients loaded: $ingredients")

            // Populate the fields
            binding.etRecipeName.setText(recipe.name)
            binding.etRecipeInstructions.setText(recipe.instructions)

            // Load ingredients
            ingredientList.clear()
            ingredientList.addAll(ingredients)
            ingredientAdapter?.notifyDataSetChanged()
        } else {
            Log.e("EditRecipeActivity", "Recipe not found for ID: $recipeId")
            Toast.makeText(this, "Recipe not found.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun showAddIngredientDialog() {
        val dialogView = layoutInflater.inflate(R.layout.add_ingredient, null)
        val ingredientName = dialogView.findViewById<EditText>(R.id.ingredientName)
        val ingredientAmount = dialogView.findViewById<EditText>(R.id.ingredientAmount)

        AlertDialog.Builder(this)
            .setTitle("Add Ingredient")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = ingredientName.text.toString().trim()
                val amount = ingredientAmount.text.toString().trim()

                if (name.isNotBlank() && amount.isNotBlank()) {
                    val ingredient = Ingredient(name, amount)
                    ingredientList.add(ingredient)
                    ingredientAdapter?.notifyItemInserted(ingredientList.size - 1)
                    Log.d("EditRecipeActivity", "Added ingredient: $ingredient")
                } else {
                    Toast.makeText(this, "Both fields are required.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun saveRecipe() {
        val name = binding.etRecipeName.text.toString().trim()
        val instructions = binding.etRecipeInstructions.text.toString().trim()

        if (name.isBlank() || instructions.isBlank()) {
            Toast.makeText(this, "Name and Instructions cannot be empty.", Toast.LENGTH_SHORT).show()
            return
        }

        // Update recipe
        val updatedRecipe = Recipe(id = recipeId, name = name, instructions = instructions)
        dbHelper.updateRecipe(updatedRecipe)
        Log.d("EditRecipeActivity", "Recipe updated: $updatedRecipe")

        // Update ingredients
        dbHelper.updateIngredients(recipeId, ingredientList)
        Log.d("EditRecipeActivity", "Ingredients updated: $ingredientList")

        Toast.makeText(this, "Recipe updated successfully!", Toast.LENGTH_SHORT).show()
        finish()
    }
}
