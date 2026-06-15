package com.foodbridge.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject

class HotelDonationAdapter(
    private val donations: List<JSONObject>,
    private val onDeleteClick: (JSONObject) -> Unit
) : RecyclerView.Adapter<HotelDonationAdapter.DonationViewHolder>() {

    class DonationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDonationDesc: TextView = view.findViewById(R.id.tvDonationDesc)
        val tvDonationQty: TextView = view.findViewById(R.id.tvDonationQty)
        val tvDonationStatus: TextView = view.findViewById(R.id.tvDonationStatus)
        val ivDeleteDonation: ImageView = view.findViewById(R.id.ivDeleteDonation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hotel_donation, parent, false)
        return DonationViewHolder(view)
    }

    override fun onBindViewHolder(holder: DonationViewHolder, position: Int) {
        val donation = donations[position]
        holder.tvDonationDesc.text = donation.optString("package_description")
        holder.tvDonationQty.text = "Qty: ${donation.optInt("quantity")}"
        
        val status = donation.optString("status", "Pending")
        holder.tvDonationStatus.text = status
        
        if (status == "Pending") {
            holder.tvDonationStatus.setTextColor(android.graphics.Color.parseColor("#f59e0b")) // Orange
        } else {
            holder.tvDonationStatus.setTextColor(android.graphics.Color.parseColor("#10b981")) // Green
        }
        
        holder.ivDeleteDonation.setOnClickListener {
            onDeleteClick(donation)
        }
    }

    override fun getItemCount(): Int = donations.size
}
