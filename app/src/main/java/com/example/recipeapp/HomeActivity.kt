package com.example.recipeapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.recipeapp.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle View Recipes Button Click
        binding.btnViewRecipes.setOnClickListener {
            startActivity(Intent(this, RecipeListActivity::class.java)) // Navigate to ViewActivity
        }

        // Handle Add Recipe Button Click
        binding.btnAddRecipe.setOnClickListener {
            startActivity(Intent(this, AddActivity::class.java)) // Navigate to AddActivity
        }

        // Handle Search Button Click
        binding.btnSearch.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java)) // Navigate to SearchActivity
        }

        // Handle Settings Button Click
        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java)) // Navigate to SettingsActivity
        }
    }
}
