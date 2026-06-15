package com.foodbridge.app

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import org.json.JSONObject

class CheapestSearchActivity : AppCompatActivity() {

    private lateinit var rvCheapestResults: RecyclerView
    private lateinit var adapter: FoodAdapter
    private val foodList = mutableListOf<FoodItem>()
    private lateinit var etSearch: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheapest_search)

        etSearch = findViewById(R.id.etCheapestSearch)
        rvCheapestResults = findViewById(R.id.rvCheapestResults)
        rvCheapestResults.layoutManager = LinearLayoutManager(this)

        adapter = FoodAdapter(foodList) { item ->
            val intent = Intent(this, FoodDetailActivity::class.java)
            intent.putExtra("food_id", item.id)
            intent.putExtra("hotel_id", item.hotelId)
            intent.putExtra("price", if (item.discountedPrice < item.originalPrice) item.discountedPrice else item.originalPrice)
            intent.putExtra("item_name", item.itemName)
            intent.putExtra("hotel_name", item.hotelName)
            startActivity(intent)
        }
        rvCheapestResults.adapter = adapter

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                if (query.length > 2) {
                    searchCheapest(query)
                } else if (query.isEmpty()) {
                    foodList.clear()
                    adapter.notifyDataSetChanged()
                }
            }
        })
    }

    private fun searchCheapest(query: String) {
        lifecycleScope.launch {
            try {
                val json = JSONObject().apply { put("query", query) }
                // The search_food.php automatically orders by discounted_price ASC
                val response = ApiClient.post("search_food.php", json)
                if (response.has("status") && response.getString("status") == "success") {
                    foodList.clear()
                    val data = response.getJSONArray("data")
                    for (i in 0 until data.length()) {
                        val obj = data.getJSONObject(i)
                        foodList.add(FoodItem(
                            id = obj.getInt("id"),
                            hotelId = obj.getInt("hotel_id"),
                            hotelName = obj.optString("hotel_name", "Unknown Hotel"),
                            itemName = obj.getString("item_name"),
                            originalPrice = obj.getDouble("original_price"),
                            discountedPrice = obj.getDouble("discounted_price"),
                            imageUrl = obj.optString("image_url", ""),
                            hygienePercentage = obj.optDouble("hygiene_percentage", -1.0)
                        ))
                    }
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
