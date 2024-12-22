package com.example.recipeapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.recipeapp.databinding.FragmentRecipeIngredientsBinding

class RecipeIngredientsFragment : Fragment() {

    private var _binding: FragmentRecipeIngredientsBinding? = null
    private val binding get() = _binding!!

    private val ingredientsList = mutableListOf<Pair<String, String>>() // Name and amount

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeIngredientsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddIngredient.setOnClickListener {
            val ingredientName = binding.IngredientName.text.toString().trim()
            val ingredientAmount = binding.IngredientAmount.text.toString().trim()

            if (ingredientName.isNotBlank() && ingredientAmount.isNotBlank()) {
                ingredientsList.add(Pair(ingredientName, ingredientAmount))

                // Clear the input fields
                binding.IngredientName.text.clear()
                binding.IngredientAmount.text.clear()

                // Update the IngredientList TextView
                binding.IngredientList.text = ingredientsList.joinToString("\n") {
                    "${it.first}: ${it.second}"
                }
            } else {
                if (ingredientName.isBlank()) {
                    binding.IngredientName.error = "Enter ingredient name"
                }
                if (ingredientAmount.isBlank()) {
                    binding.IngredientAmount.error = "Enter ingredient amount"
                }
            }
        }

        binding.btnNext.setOnClickListener {
            val bundle = Bundle().apply {
                putStringArrayList("ingredients", ArrayList(ingredientsList.map { "${it.first}:${it.second}" }))
                arguments?.getString("recipeName")?.let { putString("recipeName", it) }
            }

            val fragment = RecipeInstructionsFragment().apply {
                arguments = bundle
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
