package com.foodbridge.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class ChangePasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        val etNewPassword = findViewById<android.widget.EditText>(R.id.etNewPassword)
        val btnSave = findViewById<Button>(R.id.btnSave)

        btnSave.setOnClickListener {
            val newPassword = etNewPassword.text.toString()
            if (newPassword.isNotEmpty()) {
                val session = SessionManager(this)
                val role = session.getResetRole()
                val email = session.getResetEmail()

                btnSave.text = "Saving..."
                btnSave.isEnabled = false

                val json = org.json.JSONObject()
                json.put("role", role)
                json.put("email", email)
                json.put("new_password", newPassword)

                lifecycleScope.launch {
                    val response = ApiClient.post("forgot_password.php", json)
                    if (response.getString("status") == "success") {
                        Toast.makeText(this@ChangePasswordActivity, "Password updated successfully!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@ChangePasswordActivity, ChooseRoleActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@ChangePasswordActivity, response.getString("message"), Toast.LENGTH_SHORT).show()
                        btnSave.text = "Save Password"
                        btnSave.isEnabled = true
                    }
                }
            } else {
                Toast.makeText(this, "Please enter new password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
