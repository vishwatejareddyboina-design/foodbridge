package com.foodbridge.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import org.json.JSONObject

data class OrderItem(
    val id: Int,
    val amount: Double,
    val status: String,
    val paymentMethod: String,
    val paymentStatus: String,
    val deliveryAddress: String? = null
)

class OrderAdapter(
    private val orders: List<OrderItem>,
    private val btn1Text: String = "Accept Order",
    private val btn2Text: String = "Assign to Delivery",
    private val onAcceptClick: (OrderItem) -> Unit,
    private val onAssignClick: (OrderItem) -> Unit,
    private val onDeleteClick: ((OrderItem) -> Unit)? = null
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvOrderTitle: TextView = view.findViewById(R.id.tvOrderTitle)
        val tvOrderAmount: TextView = view.findViewById(R.id.tvOrderAmount)
        val btnAcceptOrder: Button = view.findViewById(R.id.btnAcceptOrder)
        val btnAssignDelivery: Button = view.findViewById(R.id.btnAssignDelivery)
        val ivDeleteOrder: android.widget.ImageView? = view.findViewById(R.id.ivDeleteOrder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.tvOrderTitle.text = "Order #${order.id} - ${order.status}"
        holder.tvOrderAmount.text = "₹${order.amount} (${order.paymentMethod}: ${order.paymentStatus})"
        
        val tvDeliveryAddress: TextView? = holder.itemView.findViewById(R.id.tvDeliveryAddress)
        tvDeliveryAddress?.text = "Delivery Address: ${order.deliveryAddress ?: "Not provided"}"
        
        holder.btnAcceptOrder.text = btn1Text
        holder.btnAssignDelivery.text = btn2Text
        
        holder.btnAcceptOrder.visibility = if (order.status == "Pending" || (btn1Text == "Accept Delivery" && order.status == "Accepted")) View.VISIBLE else View.GONE
        holder.btnAssignDelivery.visibility = if (order.status == "Accepted" || (btn2Text == "Mark Delivered" && order.status == "Out for Delivery")) View.VISIBLE else View.GONE
        
        holder.ivDeleteOrder?.visibility = if (onDeleteClick != null) View.VISIBLE else View.GONE
        
        holder.btnAcceptOrder.setOnClickListener { onAcceptClick(order) }
        holder.btnAssignDelivery.setOnClickListener { onAssignClick(order) }
        holder.ivDeleteOrder?.setOnClickListener { onDeleteClick?.invoke(order) }
    }

    override fun getItemCount(): Int = orders.size
}

class HotelOrdersActivity : AppCompatActivity() {
    private val orderList = mutableListOf<OrderItem>()
    private lateinit var adapter: OrderAdapter
    private var hotelId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotel_orders)

        val session = SessionManager(this)
        hotelId = session.getUserDetails()["id"]?.toIntOrNull() ?: 0

        val rvHotelOrders = findViewById<RecyclerView>(R.id.rvHotelOrders)
        rvHotelOrders.layoutManager = LinearLayoutManager(this)
        
        adapter = OrderAdapter(orderList, "Accept Order", "Assign to Delivery", { order ->
            updateOrderStatus(order.id, "Accepted")
        }, { order ->
            // In a real app we'd open a dialog to select delivery partner or just set it to 'Out for Delivery' which delivery partners can claim
            updateOrderStatus(order.id, "Accepted") // Delivery app logic usually takes over
            Toast.makeText(this, "Order marked for Delivery Partner", Toast.LENGTH_SHORT).show()
        }, { order ->
            deleteOrder(order.id)
        })
        rvHotelOrders.adapter = adapter
        
        fetchOrders()
    }

    private fun fetchOrders() {
        lifecycleScope.launch {
            try {
                val json = JSONObject().apply { put("hotel_id", hotelId) }
                val response = ApiClient.post("hotel_orders.php", json)
                if (response.has("status") && response.getString("status") == "success") {
                    orderList.clear()
                    val data = response.getJSONArray("data")
                    for (i in 0 until data.length()) {
                        val obj = data.getJSONObject(i)
                        orderList.add(OrderItem(
                            id = obj.getInt("id"),
                            amount = obj.getDouble("total_amount"),
                            status = obj.getString("status"),
                            paymentMethod = obj.getString("payment_method"),
                            paymentStatus = obj.getString("payment_status"),
                            deliveryAddress = obj.optString("delivery_address", "Not provided")
                        ))
                    }
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun deleteOrder(orderId: Int) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Delete Order")
            .setMessage("Are you sure you want to delete this order?")
            .setPositiveButton("Yes") { _, _ ->
                val json = JSONObject().apply { put("order_id", orderId) }
                lifecycleScope.launch {
                    try {
                        val response = ApiClient.post("delete_order.php", json)
                        if (response.has("status") && response.getString("status") == "success") {
                            Toast.makeText(this@HotelOrdersActivity, "Order deleted", Toast.LENGTH_SHORT).show()
                            fetchOrders()
                        } else {
                            Toast.makeText(this@HotelOrdersActivity, "Failed to delete order", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun updateOrderStatus(orderId: Int, status: String) {
        lifecycleScope.launch {
            try {
                val json = JSONObject().apply { 
                    put("order_id", orderId)
                    put("status", status)
                }
                val response = ApiClient.post("update_order_status.php", json)
                if (response.has("status") && response.getString("status") == "success") {
                    Toast.makeText(this@HotelOrdersActivity, "Order Updated", Toast.LENGTH_SHORT).show()
                    fetchOrders() // refresh list
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
