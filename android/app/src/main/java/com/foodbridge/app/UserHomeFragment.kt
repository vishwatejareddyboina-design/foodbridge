package com.foodbridge.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class UserHomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_home, container, false)
    }

    private lateinit var rvFoodItems: RecyclerView
    private val foodList = mutableListOf<FoodItem>()
    private lateinit var adapter: FoodAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val session = SessionManager(requireContext())
        val name = session.getUserName()
        view.findViewById<android.widget.TextView>(R.id.tvUserName)?.text = name

        rvFoodItems = view.findViewById(R.id.rvFoodItems)
        rvFoodItems.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        view.findViewById<androidx.cardview.widget.CardView>(R.id.cardDineInPlaces)?.setOnClickListener {
            startActivity(android.content.Intent(requireContext(), DineInListActivity::class.java))
        }

        view.findViewById<androidx.cardview.widget.CardView>(R.id.cardCheapestFood)?.setOnClickListener {
            startActivity(android.content.Intent(requireContext(), CheapestSearchActivity::class.java))
        }

        adapter = FoodAdapter(foodList) { item ->
            val intent = android.content.Intent(requireContext(), FoodDetailActivity::class.java)
            intent.putExtra("food_id", item.id)
            intent.putExtra("hotel_id", item.hotelId)
            intent.putExtra("price", if (item.discountedPrice < item.originalPrice) item.discountedPrice else item.originalPrice)
            intent.putExtra("item_name", item.itemName)
            intent.putExtra("hotel_name", item.hotelName)
            startActivity(intent)
        }
        rvFoodItems.adapter = adapter

        val etSearch = view.findViewById<android.widget.EditText>(R.id.etSearch)
        etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                loadFoodItems(s.toString())
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
        
        loadFoodItems()
    }

    private fun loadFoodItems(query: String = "") {
        lifecycleScope.launch {
            try {
                val json = org.json.JSONObject()
                json.put("query", query)

                val response = ApiClient.post("search_food.php", json)
                if (response.has("status") && response.getString("status") == "success") {
                    foodList.clear()
                    val data = response.getJSONArray("data")
                    for (i in 0 until data.length()) {
                        val obj = data.getJSONObject(i)
                        val hygieneVal = obj.optDouble("hygiene_percentage", -1.0)
                        val finalHygiene = if (hygieneVal >= 0) hygieneVal else null
                        
                        foodList.add(FoodItem(
                            id = obj.getInt("id"),
                            hotelId = obj.getInt("hotel_id"),
                            hotelName = obj.optString("hotel_name", "Unknown Hotel"),
                            itemName = obj.getString("item_name"),
                            originalPrice = obj.getDouble("original_price"),
                            discountedPrice = obj.getDouble("discounted_price"),
                            imageUrl = obj.optString("image_url", null),
                            hygienePercentage = finalHygiene
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
