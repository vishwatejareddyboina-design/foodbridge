package com.foodbridge.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.json.JSONObject

class SubmitReviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit_review)

        val orderId = intent.getIntExtra("order_id", 0)
        val hotelId = intent.getIntExtra("hotel_id", 0)
        val session = SessionManager(this)
        val userId = session.getUserDetails()["id"]

        val ratingBar = findViewById<RatingBar>(R.id.ratingBar)
        val etComment = findViewById<EditText>(R.id.etComment)
        val switchHygiene = findViewById<android.widget.Switch>(R.id.switchHygiene)
        val btnSubmitReview = findViewById<Button>(R.id.btnSubmitReview)

        btnSubmitReview.setOnClickListener {
            val rating = ratingBar.rating
            val comment = etComment.text.toString().trim()
            val isHygienic = if (switchHygiene.isChecked) 1 else 0

            val json = JSONObject().apply {
                put("order_id", orderId)
                put("user_id", userId)
                put("hotel_id", hotelId)
                put("rating", rating)
                put("comment", comment)
                put("is_hygienic", isHygienic)
            }

            lifecycleScope.launch {
                try {
                    val response = ApiClient.post("submit_review.php", json)
                    if (response.has("status") && response.getString("status") == "success") {
                        Toast.makeText(this@SubmitReviewActivity, "Review submitted! Thank you.", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@SubmitReviewActivity, "Failed to submit review", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
