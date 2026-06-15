package com.foodbridge.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class DeliveryDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery_dashboard)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        
        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(DeliveryHomeFragment())
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            var fragment: Fragment? = null
            when (item.itemId) {
                R.id.navigation_home -> fragment = DeliveryHomeFragment()
                R.id.navigation_deliveries -> fragment = DeliveryAcceptedFragment()
                R.id.navigation_settings -> fragment = DeliverySettingsFragment()
            }
            if (fragment != null) {
                loadFragment(fragment)
                true
            } else {
                false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
