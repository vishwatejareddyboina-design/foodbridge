package com.foodbridge.app

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject

class DineInHotelAdapter(
    private val hotels: List<JSONObject>
) : RecyclerView.Adapter<DineInHotelAdapter.HotelViewHolder>() {

    class HotelViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvHotelName: TextView = view.findViewById(R.id.tvHotelName)
        val tvAvailability: TextView = view.findViewById(R.id.tvAvailability)
        val tvWaitTime: TextView = view.findViewById(R.id.tvWaitTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dine_in_hotel, parent, false)
        return HotelViewHolder(view)
    }

    override fun onBindViewHolder(holder: HotelViewHolder, position: Int) {
        val hotel = hotels[position]
        holder.tvHotelName.text = hotel.optString("hotel_name")
        
        val remaining = hotel.optInt("remaining")
        val total = hotel.optInt("total_places")
        holder.tvAvailability.text = "$remaining / $total Seats Available"
        
        if (remaining == 0) {
            holder.tvAvailability.setTextColor(Color.parseColor("#ef4444")) // Red
        } else if (remaining < 5) {
            holder.tvAvailability.setTextColor(Color.parseColor("#f59e0b")) // Orange
        } else {
            holder.tvAvailability.setTextColor(Color.parseColor("#10b981")) // Green
        }
        
        holder.tvWaitTime.text = "Wait Time: ~${hotel.optInt("max_clear_time_mins")} mins"
        
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DineInDetailActivity::class.java).apply {
                putExtra("hotel_name", hotel.optString("hotel_name"))
                putExtra("hotel_address", hotel.optString("hotel_address"))
                putExtra("remaining", remaining)
                putExtra("total_places", total)
                putExtra("in_use", hotel.optInt("in_use"))
                putExtra("max_clear_time_mins", hotel.optInt("max_clear_time_mins"))
            }
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = hotels.size
}
