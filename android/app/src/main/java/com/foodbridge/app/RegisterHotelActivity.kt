package com.foodbridge.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class RegisterHotelActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_hotel)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        val btnCreateAccount = findViewById<Button>(R.id.btnCreateAccount)
        val etHotelName = findViewById<android.widget.EditText>(R.id.etHotelName)
        val etOwnerName = findViewById<android.widget.EditText>(R.id.etOwnerName)
        val etEmail = findViewById<android.widget.EditText>(R.id.etEmail)
        val etPhone = findViewById<android.widget.EditText>(R.id.etPhone)
        val etAddress = findViewById<android.widget.EditText>(R.id.etAddress)
        val etPassword = findViewById<android.widget.EditText>(R.id.etPassword)

        btnCreateAccount.setOnClickListener {
            val hotelName = etHotelName.text.toString()
            val ownerName = etOwnerName.text.toString()
            val email = etEmail.text.toString()
            val phone = etPhone.text.toString()
            val address = etAddress.text.toString()
            val password = etPassword.text.toString()

            if (hotelName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                btnCreateAccount.text = "Creating Account..."
                btnCreateAccount.isEnabled = false

                val json = org.json.JSONObject()
                json.put("role", "hotel")
                json.put("hotel_name", hotelName)
                json.put("owner_name", ownerName)
                json.put("email", email)
                json.put("phone", phone)
                json.put("address", address)
                json.put("password", password)

                lifecycleScope.launch {
                    try {
                        val response = ApiClient.post("register.php", json)
                        
                        if (response.has("status") && response.getString("status") == "success") {
                            Toast.makeText(this@RegisterHotelActivity, "Registered successfully! Please login.", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@RegisterHotelActivity, HotelLoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            val msg = if (response.has("message")) response.getString("message") else "Unknown error occurred"
                            Toast.makeText(this@RegisterHotelActivity, msg, Toast.LENGTH_SHORT).show()
                            btnCreateAccount.text = "Create Account"
                            btnCreateAccount.isEnabled = true
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(this@RegisterHotelActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        btnCreateAccount.text = "Create Account"
                        btnCreateAccount.isEnabled = true
                    }
                }
            } else {
                Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
