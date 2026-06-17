package com.foodbridge.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val session = SessionManager(requireContext())
        val name = session.getUserName()
        view.findViewById<android.widget.TextView>(R.id.tvHotelName)?.text = name

        view.findViewById<androidx.cardview.widget.CardView>(R.id.cardAddFood)?.setOnClickListener {
            startActivity(android.content.Intent(requireContext(), AddFoodActivity::class.java))
        }
        view.findViewById<androidx.cardview.widget.CardView>(R.id.cardOrders)?.setOnClickListener {
            startActivity(android.content.Intent(requireContext(), HotelOrdersActivity::class.java))
        }
        view.findViewById<androidx.cardview.widget.CardView>(R.id.cardDonate)?.setOnClickListener {
            val bottomNav = requireActivity().findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_navigation)
            bottomNav?.selectedItemId = R.id.navigation_donations
        }
        view.findViewById<androidx.cardview.widget.CardView>(R.id.cardDineIn)?.setOnClickListener {
            startActivity(android.content.Intent(requireContext(), ManageDineInActivity::class.java))
        }
    }
}
