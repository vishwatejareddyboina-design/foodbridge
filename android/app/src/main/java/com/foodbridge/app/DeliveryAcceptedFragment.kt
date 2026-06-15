package com.foodbridge.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import org.json.JSONObject

data class DonationItem(
    val id: Int,
    val hotelName: String,
    val description: String,
    val quantity: Int,
    val status: String
)

class DonationAdapter(
    private val donations: List<DonationItem>,
    private val onAcceptClick: (DonationItem) -> Unit,
    private val onDropClick: (DonationItem) -> Unit
) : RecyclerView.Adapter<DonationAdapter.DonationViewHolder>() {

    class DonationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDonationHotel: TextView = view.findViewById(R.id.tvDonationHotel)
        val tvDonationDesc: TextView = view.findViewById(R.id.tvDonationDesc)
        val tvDonationStatus: TextView = view.findViewById(R.id.tvDonationStatus)
        val btnAcceptDonation: Button = view.findViewById(R.id.btnAcceptDonation)
        val btnDropNGO: Button = view.findViewById(R.id.btnDropNGO)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_donation, parent, false)
        return DonationViewHolder(view)
    }

    override fun onBindViewHolder(holder: DonationViewHolder, position: Int) {
        val donation = donations[position]
        holder.tvDonationHotel.text = donation.hotelName
        holder.tvDonationDesc.text = "${donation.quantity} units - ${donation.description}"
        holder.tvDonationStatus.text = "Status: ${donation.status}"

        holder.btnAcceptDonation.visibility = if (donation.status == "Pending") View.VISIBLE else View.GONE
        holder.btnDropNGO.visibility = if (donation.status == "Reserved") View.VISIBLE else View.GONE
        
        holder.btnAcceptDonation.setOnClickListener { onAcceptClick(donation) }
        holder.btnDropNGO.setOnClickListener { onDropClick(donation) }
    }

    override fun getItemCount(): Int = donations.size
}

class DeliveryAcceptedFragment : Fragment() {

    private val donationList = mutableListOf<DonationItem>()
    private lateinit var adapter: DonationAdapter
    private var deliveryPartnerId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_delivery_accepted, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val session = SessionManager(requireContext())
        deliveryPartnerId = session.getUserDetails()["id"]?.toIntOrNull() ?: 0

        val rvDonations = view.findViewById<RecyclerView>(R.id.rvDonations)
        rvDonations.layoutManager = LinearLayoutManager(requireContext())
        
        adapter = DonationAdapter(donationList, { donation ->
            updateDonationStatus(donation.id, "Reserved", deliveryPartnerId)
        }, { donation ->
            updateDonationStatus(donation.id, "Picked Up", deliveryPartnerId)
        })
        rvDonations.adapter = adapter
        
        fetchDonations()
    }

    private fun fetchDonations() {
        lifecycleScope.launch {
            try {
                // Fetch Pending
                val jsonPending = JSONObject().apply { put("status", "Pending") }
                val responsePending = ApiClient.post("get_donations.php", jsonPending)
                
                donationList.clear()
                
                if (responsePending.has("status") && responsePending.getString("status") == "success") {
                    val data = responsePending.getJSONArray("data")
                    for (i in 0 until data.length()) {
                        val obj = data.getJSONObject(i)
                        donationList.add(DonationItem(
                            id = obj.getInt("id"),
                            hotelName = obj.optString("hotel_name", "Unknown"),
                            description = obj.getString("package_description"),
                            quantity = obj.getInt("quantity"),
                            status = obj.getString("status")
                        ))
                    }
                }

                // Fetch Reserved
                val jsonReserved = JSONObject().apply { 
                    put("status", "Reserved")
                    put("delivery_partner_id", deliveryPartnerId)
                }
                val responseReserved = ApiClient.post("get_donations.php", jsonReserved)
                if (responseReserved.has("status") && responseReserved.getString("status") == "success") {
                    val data = responseReserved.getJSONArray("data")
                    for (i in 0 until data.length()) {
                        val obj = data.getJSONObject(i)
                        donationList.add(DonationItem(
                            id = obj.getInt("id"),
                            hotelName = obj.optString("hotel_name", "Unknown"),
                            description = obj.getString("package_description"),
                            quantity = obj.getInt("quantity"),
                            status = obj.getString("status")
                        ))
                    }
                }
                
                adapter.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun updateDonationStatus(donationId: Int, status: String, partnerId: Int) {
        lifecycleScope.launch {
            try {
                val json = JSONObject().apply { 
                    put("donation_id", donationId)
                    put("status", status)
                    put("delivery_partner_id", partnerId)
                }
                val response = ApiClient.post("update_donation_status.php", json)
                if (response.has("status") && response.getString("status") == "success") {
                    Toast.makeText(requireContext(), "Donation Updated", Toast.LENGTH_SHORT).show()
                    fetchDonations()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
