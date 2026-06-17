package com.foodbridge.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.json.JSONObject

class ManageDineInActivity : AppCompatActivity() {

    private lateinit var etTotalPlaces: EditText
    private lateinit var etInUse: EditText
    private lateinit var etMaxTime: EditText
    private var hotelId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_dine_in)

        val session = SessionManager(this)
        hotelId = session.getUserDetails()["id"]?.toIntOrNull() ?: -1

        etTotalPlaces = findViewById(R.id.etTotalPlaces)
        etInUse = findViewById(R.id.etInUse)
        etMaxTime = findViewById(R.id.etMaxTime)

        fetchDineInStatus()

        findViewById<Button>(R.id.btnSaveDineIn).setOnClickListener {
            saveDineInStatus()
        }

        findViewById<Button>(R.id.btnDeleteDineIn).setOnClickListener {
            deleteDineInStatus()
        }
    }

    private fun fetchDineInStatus() {
        lifecycleScope.launch {
            try {
                val json = JSONObject().apply { put("hotel_id", hotelId) }
                val response = ApiClient.post("get_hotel_dine_in.php", json)
                if (response.has("status") && response.getString("status") == "success") {
                    val data = response.getJSONObject("data")
                    etTotalPlaces.setText(data.getString("total_places"))
                    etInUse.setText(data.getString("in_use"))
                    etMaxTime.setText(data.getString("max_clear_time_mins"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveDineInStatus() {
        val total = etTotalPlaces.text.toString()
        val inUse = etInUse.text.toString()
        val maxTime = etMaxTime.text.toString()

        if (total.isEmpty() || inUse.isEmpty() || maxTime.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (inUse.toInt() > total.toInt()) {
            Toast.makeText(this, "In-use cannot be greater than total places", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val json = JSONObject().apply {
                    put("hotel_id", hotelId)
                    put("total_places", total.toInt())
                    put("in_use", inUse.toInt())
                    put("max_clear_time", maxTime.toInt())
                }
                val response = ApiClient.post("update_dine_in.php", json)
                if (response.has("status") && response.getString("status") == "success") {
                    Toast.makeText(this@ManageDineInActivity, "Dine-in status updated successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@ManageDineInActivity, "Failed to update", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@ManageDineInActivity, "Error occurred", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteDineInStatus() {
        lifecycleScope.launch {
            try {
                val json = JSONObject().apply { put("hotel_id", hotelId) }
                val response = ApiClient.post("delete_dine_in.php", json)
                if (response.has("status") && response.getString("status") == "success") {
                    Toast.makeText(this@ManageDineInActivity, "Dine-in tracking cleared", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@ManageDineInActivity, "Failed to clear", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
