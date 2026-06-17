package com.foodbridge.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        val etEmail = findViewById<android.widget.EditText>(R.id.etEmail)

        findViewById<Button>(R.id.btnSendReset).setOnClickListener {
            val email = etEmail.text.toString()
            if (email.isNotEmpty()) {
                val session = SessionManager(this)
                session.saveResetEmail(email)
                
                val intent = Intent(this, ChangePasswordActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
