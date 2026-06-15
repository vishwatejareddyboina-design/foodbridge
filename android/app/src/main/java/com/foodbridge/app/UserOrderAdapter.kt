package com.foodbridge.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject

class UserOrderAdapter(
    private val ordersList: List<JSONObject>,
    private val onItemClick: (JSONObject) -> Unit
) : RecyclerView.Adapter<UserOrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvOrderHotelName: TextView = view.findViewById(R.id.tvOrderHotelName)
        val tvOrderAmount: TextView = view.findViewById(R.id.tvOrderAmount)
        val tvOrderId: TextView = view.findViewById(R.id.tvOrderId)
        val tvOrderStatus: TextView = view.findViewById(R.id.tvOrderStatus)
        val tvOrderDate: TextView = view.findViewById(R.id.tvOrderDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = ordersList[position]
        
        holder.tvOrderHotelName.text = order.optString("hotel_name", "Unknown Hotel")
        holder.tvOrderAmount.text = "₹${order.optString("total_amount", "0.00")}"
        holder.tvOrderId.text = "Order #${order.optInt("order_id")}"
        
        val status = order.optString("status", "Pending")
        holder.tvOrderStatus.text = status
        
        when (status) {
            "Completed" -> holder.tvOrderStatus.setTextColor(android.graphics.Color.parseColor("#10b981")) // Green
            "Cancelled" -> holder.tvOrderStatus.setTextColor(android.graphics.Color.parseColor("#ef4444")) // Red
            "Out for Delivery" -> holder.tvOrderStatus.setTextColor(android.graphics.Color.parseColor("#3b82f6")) // Blue
            else -> holder.tvOrderStatus.setTextColor(android.graphics.Color.parseColor("#f59e0b")) // Orange/Yellow
        }

        holder.tvOrderDate.text = order.optString("created_at", "")

        holder.itemView.setOnClickListener {
            onItemClick(order)
        }
    }

    override fun getItemCount(): Int = ordersList.size
}
