package com.foodbridge.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class HotelLoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotel_login)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val etEmail = findViewById<android.widget.EditText>(R.id.editEmail)
        val etPassword = findViewById<android.widget.EditText>(R.id.editPassword)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                btnLogin.text = "Logging in..."
                btnLogin.isEnabled = false

                val json = org.json.JSONObject()
                json.put("role", "hotel")
                json.put("email", email)
                json.put("password", password)

                lifecycleScope.launch {
                    val response = ApiClient.post("login.php", json)
                    
                    if (response.getString("status") == "success") {
                        val data = response.getJSONObject("data")
                        val session = SessionManager(this@HotelLoginActivity)
                        session.createLoginSession(
                            data.getInt("id"),
                            data.getString("name"),
                            data.getString("email"),
                            data.getString("role")
                        )
                        Toast.makeText(this@HotelLoginActivity, "Login successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@HotelLoginActivity, HotelDashboardActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@HotelLoginActivity, response.getString("message"), Toast.LENGTH_SHORT).show()
                        btnLogin.text = "Login"
                        btnLogin.isEnabled = true
                    }
                }
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<TextView>(R.id.textSignUp).setOnClickListener {
            startActivity(Intent(this, RegisterHotelActivity::class.java))
        }

        findViewById<TextView>(R.id.textForgotPassword).setOnClickListener {
            val session = SessionManager(this)
            session.saveResetRole("hotel")
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }
}
