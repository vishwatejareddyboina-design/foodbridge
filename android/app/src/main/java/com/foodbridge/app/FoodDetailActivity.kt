package com.foodbridge.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import org.json.JSONObject

class FoodDetailActivity : AppCompatActivity() {

    private var foodId: Int = 0
    private var hotelId: Int = 0
    private var price: Double = 0.0
    private var itemName: String = ""
    private var hotelName: String = ""

    private val reviewList = mutableListOf<ReviewItem>()
    private lateinit var adapter: ReviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_detail)

        foodId = intent.getIntExtra("food_id", 0)
        hotelId = intent.getIntExtra("hotel_id", 0)
        price = intent.getDoubleExtra("price", 0.0)
        itemName = intent.getStringExtra("item_name") ?: ""
        hotelName = intent.getStringExtra("hotel_name") ?: "Unknown Hotel"

        findViewById<TextView>(R.id.tvItemName).text = itemName
        findViewById<TextView>(R.id.tvHotelName).text = "Prepared by: $hotelName"
        findViewById<TextView>(R.id.tvPrice).text = "₹$price"

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnProceedCheckout).setOnClickListener {
            val intent = Intent(this, CheckoutActivity::class.java)
            intent.putExtra("food_id", foodId)
            intent.putExtra("hotel_id", hotelId)
            intent.putExtra("price", price)
            intent.putExtra("item_name", itemName)
            startActivity(intent)
        }

        val rvReviews = findViewById<RecyclerView>(R.id.rvReviews)
        rvReviews.layoutManager = LinearLayoutManager(this)
        adapter = ReviewAdapter(reviewList)
        rvReviews.adapter = adapter

        fetchHotelReviews()
    }

    private fun fetchHotelReviews() {
        lifecycleScope.launch {
            try {
                val json = JSONObject().apply { put("hotel_id", hotelId) }
                val response = ApiClient.post("get_hotel_reviews.php", json)
                
                if (response.has("status") && response.getString("status") == "success") {
                    val data = response.getJSONObject("data")
                    val reviewsArray = data.getJSONArray("reviews")
                    
                    reviewList.clear()
                    
                    if (reviewsArray.length() == 0) {
                        findViewById<TextView>(R.id.tvNoReviews).visibility = View.VISIBLE
                        findViewById<RecyclerView>(R.id.rvReviews).visibility = View.GONE
                    } else {
                        findViewById<TextView>(R.id.tvNoReviews).visibility = View.GONE
                        findViewById<RecyclerView>(R.id.rvReviews).visibility = View.VISIBLE
                        
                        val avgRating = data.getDouble("average_rating")
                        val llAverageRating = findViewById<LinearLayout>(R.id.llAverageRating)
                        val tvAverageRating = findViewById<TextView>(R.id.tvAverageRating)
                        
                        llAverageRating.visibility = View.VISIBLE
                        tvAverageRating.text = avgRating.toString()
                        
                        for (i in 0 until reviewsArray.length()) {
                            val obj = reviewsArray.getJSONObject(i)
                            val isHygienicVal = if (obj.isNull("is_hygienic")) null else obj.getInt("is_hygienic") == 1
                            
                            reviewList.add(ReviewItem(
                                userName = obj.getString("user_name"),
                                rating = obj.getDouble("rating"),
                                comment = obj.optString("comment", ""),
                                isHygienic = isHygienicVal
                            ))
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
