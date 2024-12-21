package com.example.recipeapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.recipeapp.databinding.FragmentRecipeInstructionsBinding

class RecipeInstructionsFragment : Fragment() {

    private var _binding: FragmentRecipeInstructionsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeInstructionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve data passed from previous fragments
        val recipeName = arguments?.getString("recipeName")
        val ingredients = arguments?.getStringArrayList("ingredients") ?: arrayListOf()

        binding.btnSubmit.setOnClickListener {
            val instructions = binding.etInstructions.text.toString().trim()

            if (instructions.isNotBlank()) {
                // Save the recipe to the database
                val success = saveRecipeToDatabase(recipeName, ingredients, instructions)

                if (success) {
                    Toast.makeText(requireContext(), "Recipe submitted successfully!", Toast.LENGTH_SHORT).show()
                    activity?.finish() // Close AddActivity and return to HomeActivity
                } else {
                    Toast.makeText(requireContext(), "Failed to save recipe. Try again.", Toast.LENGTH_SHORT).show()
                }
            } else {
                binding.etInstructions.error = "Please enter recipe instructions"
            }
        }
    }

    private fun saveRecipeToDatabase(name: String?, ingredients: List<String>, instructions: String): Boolean {
        if (name.isNullOrBlank()) {
            Toast.makeText(requireContext(), "Invalid recipe name", Toast.LENGTH_SHORT).show()
            return false
        }

        val dbHelper = DatabaseConnect(requireContext())

        // Save recipe to database
        val recipeId = dbHelper.addRecipe(
            Recipe(
                id = 0, // ID is auto-generated
                name = name,
                instructions = instructions
            )
        )

        if (recipeId == -1) {
            // If the recipe ID is -1, insertion failed
            return false
        }

        // Save ingredients to database
        val ingredientsList = ingredients.map {
            val parts = it.split(":")
            Ingredient(parts[0], parts[1])
        }
        dbHelper.addIngredients(recipeId, ingredientsList)
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
