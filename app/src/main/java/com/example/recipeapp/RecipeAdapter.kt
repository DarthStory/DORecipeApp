package com.example.recipeapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.databinding.ItemRecipeBinding

class RecipeAdapter(
    private var recipes: List<Recipe>,
    private val onRecipeClick: (Recipe) -> Unit,
    private val onDeleteClick: (Int) -> Unit,
    private val onEditClick: (Recipe) -> Unit // Callback for the edit button
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    inner class RecipeViewHolder(private val binding: ItemRecipeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: Recipe) {
            // Set the recipe name
            binding.tvRecipeName.text = recipe.name

            // Handle click events
            binding.root.setOnClickListener { onRecipeClick(recipe) } // View recipe details
            binding.btnDeleteRecipe.setOnClickListener { onDeleteClick(recipe.id) } // Delete recipe
            binding.btnEditRecipe.setOnClickListener { onEditClick(recipe) } // Edit recipe
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(recipes[position])
    }

    override fun getItemCount(): Int = recipes.size

    fun updateData(newRecipes: List<Recipe>) {
        recipes = newRecipes
        notifyDataSetChanged()
    }
}
