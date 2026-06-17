package com.foodbridge.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class ChooseRoleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_role)

        findViewById<ConstraintLayout>(R.id.cardRestaurant).setOnClickListener {
            val intent = Intent(this, HotelLoginActivity::class.java)
            startActivity(intent)
        }

        findViewById<ConstraintLayout>(R.id.cardDelivery).setOnClickListener {
            val intent = Intent(this, DeliveryLoginActivity::class.java)
            startActivity(intent)
        }

        findViewById<ConstraintLayout>(R.id.cardUser).setOnClickListener {
            val intent = Intent(this, UserLoginActivity::class.java)
            startActivity(intent)
        }
    }
}
