package com.foodbridge.app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment

class DeliverySettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_delivery_settings, container, false)
        
        val session = SessionManager(requireContext())
        view.findViewById<android.widget.TextView>(R.id.tvUserName)?.text = session.getUserName()
        view.findViewById<android.widget.TextView>(R.id.tvUserEmail)?.text = session.getUserEmail()
        
        view.findViewById<LinearLayout>(R.id.itemProfile).setOnClickListener {
            startActivity(Intent(requireContext(), ProfileActivity::class.java))
        }

        view.findViewById<LinearLayout>(R.id.itemChangePassword).setOnClickListener {
            startActivity(Intent(requireContext(), ChangePasswordActivity::class.java))
        }

        view.findViewById<LinearLayout>(R.id.itemHelpSupport).setOnClickListener {
            startActivity(Intent(requireContext(), HelpSupportActivity::class.java))
        }

        view.findViewById<LinearLayout>(R.id.itemPrivacyPolicy).setOnClickListener {
            startActivity(Intent(requireContext(), PrivacyPolicyActivity::class.java))
        }

        view.findViewById<LinearLayout>(R.id.itemReviews).setOnClickListener {
            startActivity(Intent(requireContext(), ReviewsActivity::class.java))
        }

        view.findViewById<LinearLayout>(R.id.itemLogout).setOnClickListener {
            val intent = Intent(requireContext(), DeliveryLoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        return view
    }
}
