package com.example.recipeapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.recipeapp.databinding.ActivityLoginBinding
import java.security.MessageDigest

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var dbHelper: DatabaseConnect

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View Binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Database Helper
        dbHelper = DatabaseConnect(this)

        // Login Button Click Listener
        binding.btnLogin.setOnClickListener {
            val username = binding.LoginUsername.text.toString().trim()
            val password = binding.LoginPassword.text.toString().trim()

            // Validate inputs
            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Validate credentials in database
                if (dbHelper.validateUser(username, hashPassword(password))) {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Invalid Username or Password", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Create Account Button Click Listener
        binding.btnCreateAccount.setOnClickListener {
            startActivity(Intent(this, CreateUserActivity::class.java))
        }
    }

    // Hash password for security
    private fun hashPassword(password: String): String {
        return MessageDigest.getInstance("SHA-256").digest(password.toByteArray()).joinToString("") {
            "%02x".format(it)
        }
    }
}
