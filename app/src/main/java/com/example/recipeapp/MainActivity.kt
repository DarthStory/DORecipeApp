package com.example.recipeapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        val launchTime = System.currentTimeMillis()

        // Added a delay to "promote" the app with the Icon.
        splashScreen.setKeepOnScreenCondition {
            System.currentTimeMillis() < launchTime + 3000
        }
        super.onCreate(savedInstanceState)

        // Navigate to LoginActivity after splash screen
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

}