package com.foodbridge.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class FoodAdapter(
    private val foodList: List<FoodItem>,
    private val onItemClick: ((FoodItem) -> Unit)? = null
) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    class FoodViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvItemName: TextView = view.findViewById(R.id.tvItemName)
        val tvHotelName: TextView = view.findViewById(R.id.tvHotelName)
        val tvHygiene: TextView = view.findViewById(R.id.tvHygiene)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
        val ivFoodImage: ImageView = view.findViewById(R.id.ivFoodImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val item = foodList[position]
        holder.tvItemName.text = item.itemName
        holder.tvHotelName.text = item.hotelName ?: ""
        
        if (item.hygienePercentage != null && item.hygienePercentage >= 0) {
            holder.tvHygiene.visibility = View.VISIBLE
            holder.tvHygiene.text = "✨ ${item.hygienePercentage.toInt()}% Hygienic"
        } else {
            holder.tvHygiene.visibility = View.GONE
        }
        
        if (item.discountedPrice < item.originalPrice) {
            holder.tvPrice.text = "₹${item.discountedPrice} (Was ₹${item.originalPrice})"
        } else {
            holder.tvPrice.text = "₹${item.originalPrice}"
        }

        if (!item.imageUrl.isNullOrEmpty() && item.imageUrl != "null") {
            val baseUrl = ApiClient.BASE_URL.replace("api/", "")
            val fullUrl = baseUrl + item.imageUrl.replace("'", "")
            Glide.with(holder.itemView.context)
                .load(fullUrl)
                .centerCrop()
                .into(holder.ivFoodImage)
        } else {
            holder.ivFoodImage.setImageDrawable(null)
            holder.ivFoodImage.setBackgroundColor(android.graphics.Color.parseColor("#e2e8f0"))
        }

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(item)
        }
    }

    override fun getItemCount() = foodList.size
}
