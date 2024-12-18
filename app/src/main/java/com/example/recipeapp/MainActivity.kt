package com.example.recipeapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        // Capture the current time when the splash screen starts
        val launchTime = System.currentTimeMillis()

        // Add an optional delay for the splash screen exit
        splashScreen.setKeepOnScreenCondition {
            // Use a condition to keep the splash screen visible for a certain duration
            System.currentTimeMillis() < launchTime + 3000
        }

        super.onCreate(savedInstanceState)

        // Navigate to LoginActivity after splash screen
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

}