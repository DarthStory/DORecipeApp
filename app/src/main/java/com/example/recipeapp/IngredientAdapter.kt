package com.example.recipeapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.databinding.ItemIngredientBinding

class IngredientAdapter(
    private val ingredients: MutableList<Ingredient>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder>() {

    inner class IngredientViewHolder(private val binding: ItemIngredientBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(ingredient: Ingredient, position: Int) {
            binding.ingredientName.text = ingredient.name
            binding.ingredientAmount.text = ingredient.amount
            binding.btnDeleteIngredient.setOnClickListener { onDeleteClick(position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val binding = ItemIngredientBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IngredientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        holder.bind(ingredients[position], position)
    }

    override fun getItemCount(): Int = ingredients.size
}
