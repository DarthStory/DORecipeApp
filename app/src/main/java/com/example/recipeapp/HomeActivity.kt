package com.example.recipeapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.recipeapp.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View Binding
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle View Recipes Button Click
        binding.btnViewRecipes.setOnClickListener {
            // Placeholder action for now
            startActivity(Intent(this, RecipeListActivity::class.java)) // Replace with actual implementation later
        }
    }
}
