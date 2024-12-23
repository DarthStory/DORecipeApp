package com.example.recipeapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.recipeapp.databinding.FragmentRecipeNameBinding

class RecipeNameFragment : Fragment() {

    private var _binding: FragmentRecipeNameBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNext.setOnClickListener {
            val recipeName = binding.RecipeName.text.toString().trim()

            if (recipeName.isNotBlank()) {
                val bundle = Bundle().apply {
                    putString("recipeName", recipeName)
                }

                // Navigate to the next fragment
                val fragment = RecipeIngredientsFragment().apply {
                    arguments = bundle
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            } else {
                binding.RecipeName.error = "Please enter a recipe name"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
