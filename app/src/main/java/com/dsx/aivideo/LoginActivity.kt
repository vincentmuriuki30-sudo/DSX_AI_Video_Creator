package com.dsx.aivideo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if user is already logged in
        val prefs = getSharedPreferences("dsx_user_session", Context.MODE_PRIVATE)
        if (prefs.getBoolean("is_logged_in", false)) {
            navigateToMain()
            return
        }

        setContentView(R.layout.activity_login)

        val btnGoogle = findViewById<Button>(R.id.btnGoogleAuth)
        val btnFacebook = findViewById<Button>(R.id.btnFacebookAuth)
        val btnEmail = findViewById<Button>(R.id.btnLoginEmail)
        val etInput = findViewById<EditText>(R.id.etAuthInput)

        // Google Authentication trigger
        btnGoogle.setOnClickListener {
            saveSession("Google User", "google_auth")
            Toast.makeText(this, "Signed in via Google!", Toast.LENGTH_SHORT).show()
            navigateToMain()
        }

        // Facebook Authentication trigger
        btnFacebook.setOnClickListener {
            saveSession("Facebook User", "fb_auth")
            Toast.makeText(this, "Signed in via Facebook!", Toast.LENGTH_SHORT).show()
            navigateToMain()
        }

        // Email / Phone Authentication trigger
        btnEmail.setOnClickListener {
            val identifier = etInput.text.toString().trim()
            if (identifier.isNotEmpty()) {
                saveSession(identifier, "email_phone_auth")
                Toast.makeText(this, "Welcome, $identifier!", Toast.LENGTH_SHORT).show()
                navigateToMain()
            } else {
                Toast.makeText(this, "Please enter an email or phone number", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveSession(accountName: String, provider: String) {
        val prefs = getSharedPreferences("dsx_user_session", Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean("is_logged_in", true)
            .putString("account_name", accountName)
            .putString("auth_provider", provider)
            .apply()
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
