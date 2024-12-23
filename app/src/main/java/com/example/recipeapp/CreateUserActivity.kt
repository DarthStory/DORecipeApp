package com.example.recipeapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.recipeapp.databinding.ActivityCreateUserBinding
import java.security.MessageDigest

class CreateUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateUserBinding
    private lateinit var dbConnect: DatabaseConnect

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Database
        dbConnect = DatabaseConnect(this)

        // Register Button Click Listener
        binding.btnRegister.setOnClickListener {
            val username = binding.Username.text.toString().trim()
            val password = binding.Password.text.toString().trim()

            // Input Validation
            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Hash the password before storing
            val hashedPassword = hashPassword(password)

            val result = dbConnect.addUser(User(username, hashedPassword))
            if (result != -1L) {
                Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show()
                finish() // Return to LoginActivity
            } else {
                Toast.makeText(this, "Username already exists!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to hash passwords securely using SHA-256
    private fun hashPassword(password: String): String {
        return MessageDigest.getInstance("SHA-256").digest(password.toByteArray()).joinToString("") {
            "%02x".format(it)
        }
    }
}
