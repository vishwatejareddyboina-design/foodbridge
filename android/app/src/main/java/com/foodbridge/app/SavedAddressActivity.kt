package com.foodbridge.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SavedAddressActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_address)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val etAddress = findViewById<EditText>(R.id.etAddress)
        val btnSaveAddress = findViewById<Button>(R.id.btnSaveAddress)

        val session = SessionManager(this)
        
        // Load existing address
        val currentAddress = session.getDeliveryAddress()
        if (currentAddress.isNotEmpty()) {
            etAddress.setText(currentAddress)
        }

        btnBack.setOnClickListener {
            finish()
        }

        btnSaveAddress.setOnClickListener {
            val newAddress = etAddress.text.toString().trim()
            if (newAddress.isEmpty()) {
                Toast.makeText(this, "Please enter your address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            session.saveDeliveryAddress(newAddress)
            Toast.makeText(this, "Address saved successfully!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
