package com.foodbridge.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DonateFoodActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donate_food)

        val session = SessionManager(this)
        val hotelId = session.getUserDetails()["id"]

        val etPackageDesc = findViewById<EditText>(R.id.etPackageDesc)
        val etQuantity = findViewById<EditText>(R.id.etQuantity)
        val etHoursToExpire = findViewById<EditText>(R.id.etHoursToExpire)
        val btnSubmitDonation = findViewById<Button>(R.id.btnSubmitDonation)

        btnSubmitDonation.setOnClickListener {
            val desc = etPackageDesc.text.toString()
            val qty = etQuantity.text.toString()
            val hours = etHoursToExpire.text.toString()

            if (desc.isEmpty() || qty.isEmpty() || hours.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val calendar = Calendar.getInstance()
            calendar.add(Calendar.HOUR, hours.toInt())
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val expirationTime = sdf.format(calendar.time)

            val json = JSONObject().apply {
                put("hotel_id", hotelId)
                put("package_description", desc)
                put("quantity", qty.toInt())
                put("expiration_time", expirationTime)
            }

            lifecycleScope.launch {
                try {
                    val response = ApiClient.post("add_donation.php", json)
                    if (response.has("status") && response.getString("status") == "success") {
                        Toast.makeText(this@DonateFoodActivity, "Donation added successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@DonateFoodActivity, "Failed to add donation", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@DonateFoodActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
