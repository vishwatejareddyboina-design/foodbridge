package com.foodbridge.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import org.json.JSONObject

class DineInListActivity : AppCompatActivity() {

    private lateinit var rvDineInHotels: RecyclerView
    private lateinit var adapter: DineInHotelAdapter
    private val hotelList = mutableListOf<JSONObject>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dine_in_list)

        rvDineInHotels = findViewById(R.id.rvDineInHotels)
        rvDineInHotels.layoutManager = LinearLayoutManager(this)
        
        adapter = DineInHotelAdapter(hotelList)
        rvDineInHotels.adapter = adapter

        fetchDineInHotels()
    }

    private fun fetchDineInHotels() {
        lifecycleScope.launch {
            try {
                val response = ApiClient.post("get_all_dine_in.php", JSONObject())
                if (response.has("status") && response.getString("status") == "success") {
                    hotelList.clear()
                    val data = response.getJSONArray("data")
                    for (i in 0 until data.length()) {
                        hotelList.add(data.getJSONObject(i))
                    }
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
