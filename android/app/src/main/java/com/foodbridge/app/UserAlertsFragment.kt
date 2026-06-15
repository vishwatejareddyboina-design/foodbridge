package com.foodbridge.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import org.json.JSONObject

class UserAlertsFragment : Fragment() {

    private lateinit var rvNotifications: RecyclerView
    private val notificationList = mutableListOf<NotificationItem>()
    private lateinit var adapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_alerts, container, false)
        rvNotifications = view.findViewById(R.id.rvNotifications)
        rvNotifications.layoutManager = LinearLayoutManager(requireContext())
        adapter = NotificationAdapter(notificationList)
        rvNotifications.adapter = adapter
        view.findViewById<android.widget.TextView>(R.id.tvClearAll).setOnClickListener {
            clearNotifications()
        }
        
        loadNotifications()
        return view
    }

    private fun clearNotifications() {
        lifecycleScope.launch {
            try {
                val response = ApiClient.post("clear_notifications.php", JSONObject())
                if (response.has("status") && response.getString("status") == "success") {
                    notificationList.clear()
                    adapter.notifyDataSetChanged()
                    Toast.makeText(requireContext(), "All notifications cleared", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to clear notifications", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error clearing notifications", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadNotifications() {
        lifecycleScope.launch {
            try {
                val response = ApiClient.post("get_notifications.php", JSONObject())
                if (response.has("status") && response.getString("status") == "success") {
                    notificationList.clear()
                    val data = response.getJSONArray("data")
                    for (i in 0 until data.length()) {
                        val obj = data.getJSONObject(i)
                        notificationList.add(NotificationItem(
                            id = obj.getInt("id"),
                            hotelId = obj.getInt("hotel_id"),
                            hotelName = obj.optString("hotel_name", "Hotel"),
                            message = obj.getString("message"),
                            type = obj.getString("type"),
                            createdAt = obj.getString("created_at")
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
