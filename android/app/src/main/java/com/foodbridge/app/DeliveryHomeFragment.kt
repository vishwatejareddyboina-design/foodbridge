package com.foodbridge.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import org.json.JSONObject

class DeliveryHomeFragment : Fragment() {
    
    private val orderList = mutableListOf<OrderItem>()
    private lateinit var adapter: OrderAdapter
    private var deliveryPartnerId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_delivery_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val session = SessionManager(requireContext())
        val name = session.getUserName()
        deliveryPartnerId = session.getUserDetails()["id"]?.toIntOrNull() ?: 0
        
        view.findViewById<TextView>(R.id.tvUserName)?.text = name

        val rvDeliveryOrders = view.findViewById<RecyclerView>(R.id.rvDeliveryOrders)
        rvDeliveryOrders.layoutManager = LinearLayoutManager(requireContext())
        
        adapter = OrderAdapter(orderList, "Accept Delivery", "Mark Delivered", { order ->
            if (order.status == "Accepted") {
                updateOrderStatus(order.id, "Out for Delivery", deliveryPartnerId)
            }
        }, { order ->
            if (order.status == "Out for Delivery") {
                updateOrderStatus(order.id, "Completed", deliveryPartnerId)
            }
        })
        rvDeliveryOrders.adapter = adapter
        
        fetchDeliveryOrders()
    }

    private fun fetchDeliveryOrders() {
        lifecycleScope.launch {
            try {
                // First, fetch orders that are ready to be picked up
                val jsonPending = JSONObject().apply { put("status", "Accepted") }
                val responsePending = ApiClient.post("delivery_orders.php", jsonPending)
                
                orderList.clear()
                
                if (responsePending.has("status") && responsePending.getString("status") == "success") {
                    val data = responsePending.getJSONArray("data")
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
                }
                
                // Next, fetch orders assigned to this delivery partner
                val jsonAssigned = JSONObject().apply { 
                    put("status", "Out for Delivery")
                    put("delivery_partner_id", deliveryPartnerId)
                }
                val responseAssigned = ApiClient.post("delivery_orders.php", jsonAssigned)
                if (responseAssigned.has("status") && responseAssigned.getString("status") == "success") {
                    val data = responseAssigned.getJSONArray("data")
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
                }
                
                adapter.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun updateOrderStatus(orderId: Int, status: String, partnerId: Int) {
        lifecycleScope.launch {
            try {
                val json = JSONObject().apply { 
                    put("order_id", orderId)
                    put("status", status)
                    put("delivery_partner_id", partnerId)
                }
                val response = ApiClient.post("update_order_status.php", json)
                if (response.has("status") && response.getString("status") == "success") {
                    Toast.makeText(requireContext(), "Order Updated", Toast.LENGTH_SHORT).show()
                    fetchDeliveryOrders()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
