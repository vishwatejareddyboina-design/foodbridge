package com.foodbridge.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class UserDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_dashboard)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        
        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(UserHomeFragment())
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            var fragment: Fragment? = null
            when (item.itemId) {
                R.id.navigation_home -> fragment = UserHomeFragment()
                R.id.navigation_orders -> fragment = UserOrdersFragment()
                R.id.navigation_alerts -> fragment = UserAlertsFragment()
                R.id.navigation_profile -> fragment = UserSettingsFragment()
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
