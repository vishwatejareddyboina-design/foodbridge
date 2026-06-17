package com.foodbridge.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.json.JSONObject

class OrderTrackingActivity : AppCompatActivity() {

    private var orderId: Int = 0
    private var hotelId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_tracking)

        orderId = intent.getIntExtra("order_id", 0)

        findViewById<Button>(R.id.btnRefresh).setOnClickListener {
            checkOrderStatus()
        }

        checkOrderStatus()
    }

    private fun checkOrderStatus() {
        lifecycleScope.launch {
            try {
                val json = JSONObject().apply { put("order_id", orderId) }
                val response = ApiClient.post("get_order_status.php", json)
                
                if (response.has("status") && response.getString("status") == "success") {
                    val data = response.getJSONObject("data")
                    val orderStatus = data.getString("status")
                    hotelId = data.getInt("hotel_id")
                    
                    updateTimeline(orderStatus)
                    
                    if (orderStatus == "Completed") {
                        val intent = Intent(this@OrderTrackingActivity, SubmitReviewActivity::class.java)
                        intent.putExtra("order_id", orderId)
                        intent.putExtra("hotel_id", hotelId)
                        startActivity(intent)
                        finish()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun updateTimeline(status: String) {
        val ivStep1 = findViewById<android.widget.ImageView>(R.id.ivStep1)
        val ivStep2 = findViewById<android.widget.ImageView>(R.id.ivStep2)
        val ivStep3 = findViewById<android.widget.ImageView>(R.id.ivStep3)
        val ivStep4 = findViewById<android.widget.ImageView>(R.id.ivStep4)
        
        val line1 = findViewById<android.view.View>(R.id.line1)
        val line2 = findViewById<android.view.View>(R.id.line2)
        val line3 = findViewById<android.view.View>(R.id.line3)
        
        val tvError = findViewById<TextView>(R.id.tvTrackingError)
        val llTimeline = findViewById<android.view.View>(R.id.llTimeline)
        
        val green = android.graphics.Color.parseColor("#10b981")
        val grey = android.graphics.Color.parseColor("#cbd5e1")
        
        if (status == "Cancelled") {
            tvError.visibility = android.view.View.VISIBLE
            llTimeline.visibility = android.view.View.GONE
            return
        }
        
        tvError.visibility = android.view.View.GONE
        llTimeline.visibility = android.view.View.VISIBLE
        
        // Reset all
        ivStep2.setColorFilter(grey)
        ivStep3.setColorFilter(grey)
        ivStep4.setColorFilter(grey)
        line1.setBackgroundColor(grey)
        line2.setBackgroundColor(grey)
        line3.setBackgroundColor(grey)

        // Step 1 is always green if not cancelled
        ivStep1.setColorFilter(green)
        
        if (status == "Accepted" || status == "Out for Delivery" || status == "Completed") {
            ivStep2.setColorFilter(green)
            line1.setBackgroundColor(green)
        }
        if (status == "Out for Delivery" || status == "Completed") {
            ivStep3.setColorFilter(green)
            line2.setBackgroundColor(green)
        }
        if (status == "Completed") {
            ivStep4.setColorFilter(green)
            line3.setBackgroundColor(green)
        }
    }
}
