package com.foodbridge.app

import android.content.Intent
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

class DonationFragment : Fragment() {

    private lateinit var rvDonations: RecyclerView
    private lateinit var adapter: HotelDonationAdapter
    private val donationsList = mutableListOf<JSONObject>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_donation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvDonations = view.findViewById(R.id.rvDonations)
        rvDonations.layoutManager = LinearLayoutManager(requireContext())
        
        adapter = HotelDonationAdapter(donationsList) { donation ->
            deleteDonation(donation.optInt("id"))
        }
        rvDonations.adapter = adapter

        view.findViewById<View>(R.id.fabAddDonation).setOnClickListener {
            startActivity(Intent(requireContext(), DonateFoodActivity::class.java))
        }

        loadDonations()
    }

    override fun onResume() {
        super.onResume()
        loadDonations() // Refresh after returning from Add Donation
    }

    private fun loadDonations() {
        val session = SessionManager(requireContext())
        val hotelId = session.getUserDetails()["id"]

        val json = JSONObject().apply { put("hotel_id", hotelId) }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.post("get_hotel_donations.php", json)
                if (response.has("status") && response.getString("status") == "success") {
                    donationsList.clear()
                    val data = response.getJSONArray("data")
                    for (i in 0 until data.length()) {
                        donationsList.add(data.getJSONObject(i))
                    }
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun deleteDonation(donationId: Int) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Delete Donation")
            .setMessage("Are you sure you want to delete this donation?")
            .setPositiveButton("Yes") { _, _ ->
                val json = JSONObject().apply { put("donation_id", donationId) }
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        val response = ApiClient.post("delete_donation.php", json)
                        if (response.has("status") && response.getString("status") == "success") {
                            Toast.makeText(requireContext(), "Donation deleted", Toast.LENGTH_SHORT).show()
                            loadDonations()
                        } else {
                            Toast.makeText(requireContext(), "Failed to delete donation", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            .setNegativeButton("No", null)
            .show()
    }
}
