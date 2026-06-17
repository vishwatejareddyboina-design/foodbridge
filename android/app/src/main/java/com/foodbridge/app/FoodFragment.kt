package com.foodbridge.app

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import org.json.JSONObject

class FoodFragment : Fragment() {

    private lateinit var rvFoodItems: RecyclerView
    private val foodList = mutableListOf<FoodItem>()
    private lateinit var adapter: FoodAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_food, container, false)
        rvFoodItems = view.findViewById(R.id.rvFoodItems)
        rvFoodItems.layoutManager = LinearLayoutManager(requireContext())
        
        adapter = FoodAdapter(foodList) { item ->
            showFoodActionDialog(item)
        }
        rvFoodItems.adapter = adapter
        
        loadFoodItems()
        return view
    }

    private fun loadFoodItems() {
        val session = SessionManager(requireContext())
        val hotelId = session.getUserDetails()["id"]

        val json = JSONObject()
        json.put("hotel_id", hotelId)

        lifecycleScope.launch {
            try {
                val response = ApiClient.post("get_food_items.php", json)
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
                            hotelName = obj.optString("hotel_name", null),
                            itemName = obj.getString("item_name"),
                            originalPrice = obj.getDouble("original_price"),
                            discountedPrice = obj.getDouble("discounted_price"),
                            imageUrl = obj.optString("image_url", null),
                            hygienePercentage = finalHygiene
                        ))
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    val msg = if (response.has("message")) response.getString("message") else "Failed to load foods"
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun showFoodActionDialog(item: FoodItem) {
        val bottomSheetDialog = com.google.android.material.bottomsheet.BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.layout_bottom_sheet_food_actions, null)
        bottomSheetDialog.setContentView(view)

        // Bind header data
        val tvName = view.findViewById<android.widget.TextView>(R.id.tvActionFoodName)
        val tvPrice = view.findViewById<android.widget.TextView>(R.id.tvActionFoodPrice)
        val ivPhoto = view.findViewById<android.widget.ImageView>(R.id.ivActionFoodPhoto)

        tvName.text = item.itemName
        tvPrice.text = "₹${item.discountedPrice}"

        if (!item.imageUrl.isNullOrEmpty() && item.imageUrl != "null") {
            val baseUrl = ApiClient.BASE_URL.replace("api/", "")
            val fullUrl = baseUrl + item.imageUrl.replace("'", "")
            com.bumptech.glide.Glide.with(this)
                .load(fullUrl)
                .centerCrop()
                .into(ivPhoto)
        }

        // Click listeners
        view.findViewById<android.view.View>(R.id.btnActionDiscount).setOnClickListener {
            bottomSheetDialog.dismiss()
            showDiscountDialog(item)
        }

        view.findViewById<android.view.View>(R.id.btnActionNotify).setOnClickListener {
            bottomSheetDialog.dismiss()
            notifyRush(item.hotelId)
        }

        view.findViewById<android.view.View>(R.id.btnActionDelete).setOnClickListener {
            bottomSheetDialog.dismiss()
            AlertDialog.Builder(requireContext())
                .setTitle("Delete Food")
                .setMessage("Are you sure you want to delete ${item.itemName}?")
                .setPositiveButton("Delete") { _, _ -> deleteFood(item) }
                .setNegativeButton("Cancel", null)
                .show()
        }

        bottomSheetDialog.show()
    }

    private fun deleteFood(item: FoodItem) {
        val json = JSONObject()
        json.put("food_id", item.id)
        json.put("hotel_id", item.hotelId)

        lifecycleScope.launch {
            try {
                val response = ApiClient.post("delete_food.php", json)
                if (response.has("status") && response.getString("status") == "success") {
                    Toast.makeText(requireContext(), "${item.itemName} deleted", Toast.LENGTH_SHORT).show()
                    loadFoodItems() // refresh list
                } else {
                    Toast.makeText(requireContext(), "Failed to delete item", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error deleting item", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDiscountDialog(item: FoodItem) {
        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        input.hint = "Enter discounted price (e.g. 150)"

        AlertDialog.Builder(requireContext())
            .setTitle("Apply Discount")
            .setMessage("Original Price: ₹${item.originalPrice}")
            .setView(input)
            .setPositiveButton("Save") { dialog, _ ->
                val newPrice = input.text.toString().trim()
                if (newPrice.isNotEmpty()) {
                    applyDiscount(item, newPrice)
                } else {
                    Toast.makeText(requireContext(), "Price cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun applyDiscount(item: FoodItem, newPrice: String) {
        val json = JSONObject()
        json.put("food_id", item.id)
        json.put("hotel_id", item.hotelId)
        json.put("discounted_price", newPrice)
        json.put("item_name", item.itemName)

        lifecycleScope.launch {
            try {
                val response = ApiClient.post("update_discount.php", json)
                if (response.has("status") && response.getString("status") == "success") {
                    Toast.makeText(requireContext(), "Discount applied & notified!", Toast.LENGTH_SHORT).show()
                    loadFoodItems() // Reload list
                } else {
                    Toast.makeText(requireContext(), "Failed to apply discount", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun notifyRush(hotelId: Int) {
        val session = SessionManager(requireContext())
        val hotelName = session.getUserName()
        
        val json = JSONObject()
        json.put("hotel_id", hotelId)
        json.put("hotel_name", hotelName)

        lifecycleScope.launch {
            try {
                val response = ApiClient.post("notify_rush.php", json)
                if (response.has("status") && response.getString("status") == "success") {
                    Toast.makeText(requireContext(), "Rush notification sent to users!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to send notification", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
