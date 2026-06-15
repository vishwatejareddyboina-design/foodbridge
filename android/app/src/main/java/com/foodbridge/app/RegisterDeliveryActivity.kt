package com.foodbridge.app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class RegisterDeliveryActivity : AppCompatActivity() {

    private lateinit var btnBike: TextView
    private lateinit var btnScooter: TextView
    private lateinit var btnBicycle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_delivery)

        btnBike = findViewById(R.id.btnBike)
        btnScooter = findViewById(R.id.btnScooter)
        btnBicycle = findViewById(R.id.btnBicycle)

        // Set default selection
        selectVehicle(btnBike)

        btnBike.setOnClickListener { selectVehicle(btnBike) }
        btnScooter.setOnClickListener { selectVehicle(btnScooter) }
        btnBicycle.setOnClickListener { selectVehicle(btnBicycle) }

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val etFullName = findViewById<android.widget.EditText>(R.id.etFullName)
        val etEmail = findViewById<android.widget.EditText>(R.id.etEmail)
        val etPhone = findViewById<android.widget.EditText>(R.id.etPhone)
        val etLicense = findViewById<android.widget.EditText>(R.id.etLicense)
        val etPassword = findViewById<android.widget.EditText>(R.id.etPassword)

        btnSubmit.setOnClickListener {
            val fullName = etFullName.text.toString()
            val email = etEmail.text.toString()
            val phone = etPhone.text.toString()
            val license = etLicense.text.toString()
            val password = etPassword.text.toString()

            if (fullName.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty() && password.isNotEmpty()) {
                btnSubmit.text = "Submitting..."
                btnSubmit.isEnabled = false

                val json = org.json.JSONObject()
                json.put("role", "delivery")
                json.put("full_name", fullName)
                json.put("email", email)
                json.put("phone", phone)
                json.put("license", license)
                json.put("password", password)

                lifecycleScope.launch {
                    val response = ApiClient.post("register.php", json)
                    
                    if (response.getString("status") == "success") {
                        Toast.makeText(this@RegisterDeliveryActivity, "Registered successfully! Please login.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@RegisterDeliveryActivity, DeliveryLoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@RegisterDeliveryActivity, response.getString("message"), Toast.LENGTH_SHORT).show()
                        btnSubmit.text = "Submit Application"
                        btnSubmit.isEnabled = true
                    }
                }
            } else {
                Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun selectVehicle(selected: TextView) {
        // Reset all
        resetVehicleButton(btnBike)
        resetVehicleButton(btnScooter)
        resetVehicleButton(btnBicycle)

        // Highlight selected
        selected.setTextColor(Color.parseColor("#ea580c"))
        // Background should ideally be changed to have an orange border, but we'll simulate it or keep it simple.
    }

    private fun resetVehicleButton(btn: TextView) {
        btn.setTextColor(Color.parseColor("#64748b"))
    }
}
