package com.foodbridge.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class RegisterUserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        val btnSignUp = findViewById<Button>(R.id.btnSignUp)
        val etFullName = findViewById<android.widget.EditText>(R.id.etFullName)
        val etEmail = findViewById<android.widget.EditText>(R.id.etEmail)
        val etPhone = findViewById<android.widget.EditText>(R.id.etPhone)
        val etPassword = findViewById<android.widget.EditText>(R.id.etPassword)

        btnSignUp.setOnClickListener {
            val fullName = etFullName.text.toString()
            val email = etEmail.text.toString()
            val phone = etPhone.text.toString()
            val password = etPassword.text.toString()

            if (fullName.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty() && password.isNotEmpty()) {
                btnSignUp.text = "Creating Account..."
                btnSignUp.isEnabled = false

                val json = org.json.JSONObject()
                json.put("role", "user")
                json.put("full_name", fullName)
                json.put("email", email)
                json.put("phone", phone)
                json.put("password", password)

                lifecycleScope.launch {
                    val response = ApiClient.post("register.php", json)
                    
                    if (response.getString("status") == "success") {
                        Toast.makeText(this@RegisterUserActivity, "Registered successfully! Please login.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@RegisterUserActivity, UserLoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@RegisterUserActivity, response.getString("message"), Toast.LENGTH_SHORT).show()
                        btnSignUp.text = "Sign Up"
                        btnSignUp.isEnabled = true
                    }
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
