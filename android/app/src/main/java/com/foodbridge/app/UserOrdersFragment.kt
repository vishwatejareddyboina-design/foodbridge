package com.foodbridge.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class UserOrdersFragment : Fragment() {
    private lateinit var rvUserOrders: androidx.recyclerview.widget.RecyclerView
    private val ordersList = mutableListOf<org.json.JSONObject>()
    private lateinit var adapter: UserOrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_orders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvUserOrders = view.findViewById(R.id.rvUserOrders)
        rvUserOrders.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        adapter = UserOrderAdapter(ordersList) { order ->
            val intent = android.content.Intent(requireContext(), OrderTrackingActivity::class.java)
            intent.putExtra("order_id", order.optInt("order_id"))
            startActivity(intent)
        }
        rvUserOrders.adapter = adapter

        loadOrders()
    }

    override fun onResume() {
        super.onResume()
        loadOrders() // Refresh when coming back
    }

    private fun loadOrders() {
        val session = SessionManager(requireContext())
        val userId = session.getUserDetails()["id"]

        val json = org.json.JSONObject()
        json.put("user_id", userId)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.post("get_user_orders.php", json)
                if (response.has("status") && response.getString("status") == "success") {
                    ordersList.clear()
                    val data = response.getJSONArray("data")
                    for (i in 0 until data.length()) {
                        ordersList.add(data.getJSONObject(i))
                    }
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
