package com.example.recipeapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.recipeapp.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View Binding
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Placeholder for functionality
        // Later you can handle toggles, save preferences, etc.
    }
}