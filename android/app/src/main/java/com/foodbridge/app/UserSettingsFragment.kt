package com.foodbridge.app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

class UserSettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_settings, container, false)
        
        val session = SessionManager(requireContext())
        view.findViewById<TextView>(R.id.tvUserName)?.text = session.getUserName()
        view.findViewById<TextView>(R.id.tvUserEmail)?.text = session.getUserEmail()
        
        view.findViewById<LinearLayout>(R.id.btnLogout).setOnClickListener {
            // Navigate back to ChooseRole or Login
            activity?.finish()
        }

        view.findViewById<LinearLayout>(R.id.btnChangePassword).setOnClickListener {
            val intent = Intent(requireContext(), ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
        
        view.findViewById<LinearLayout>(R.id.btnOrderHistory)?.setOnClickListener {
            // Let the main activity switch tabs
            val bottomNav = requireActivity().findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_navigation)
            bottomNav?.selectedItemId = R.id.navigation_orders
        }
        
        view.findViewById<LinearLayout>(R.id.btnSavedAddresses)?.setOnClickListener {
            val intent = Intent(requireContext(), SavedAddressActivity::class.java)
            startActivity(intent)
        }
        
        view.findViewById<LinearLayout>(R.id.btnHelpSupport)?.setOnClickListener {
            val intent = Intent(requireContext(), HelpSupportActivity::class.java)
            startActivity(intent)
        }
        
        view.findViewById<LinearLayout>(R.id.btnPrivacyPolicy)?.setOnClickListener {
            val intent = Intent(requireContext(), PrivacyPolicyActivity::class.java)
            startActivity(intent)
        }
        
        return view
    }
}
