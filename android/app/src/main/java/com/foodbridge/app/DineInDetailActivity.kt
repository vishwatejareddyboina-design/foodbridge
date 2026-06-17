package com.foodbridge.app

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DineInDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dine_in_detail)

        val hotelName = intent.getStringExtra("hotel_name") ?: "Unknown Hotel"
        val hotelAddress = intent.getStringExtra("hotel_address") ?: ""
        val remaining = intent.getIntExtra("remaining", 0)
        val totalPlaces = intent.getIntExtra("total_places", 0)
        val inUse = intent.getIntExtra("in_use", 0)
        val maxClearTime = intent.getIntExtra("max_clear_time_mins", 0)

        findViewById<TextView>(R.id.tvDetailHotelName).text = hotelName
        findViewById<TextView>(R.id.tvDetailAddress).text = hotelAddress
        
        val tvSeatsAvailable = findViewById<TextView>(R.id.tvSeatsAvailable)
        tvSeatsAvailable.text = remaining.toString()
        if (remaining == 0) {
            tvSeatsAvailable.setTextColor(Color.parseColor("#ef4444"))
        } else if (remaining < 5) {
            tvSeatsAvailable.setTextColor(Color.parseColor("#f59e0b"))
        } else {
            tvSeatsAvailable.setTextColor(Color.parseColor("#10b981"))
        }

        findViewById<TextView>(R.id.tvInUse).text = inUse.toString()
        findViewById<TextView>(R.id.tvTotalCapacity).text = totalPlaces.toString()
        findViewById<TextView>(R.id.tvDetailWaitTime).text = "~$maxClearTime mins per table"
    }
}
