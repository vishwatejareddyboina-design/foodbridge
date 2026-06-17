package com.foodbridge.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.roundToInt

class CheckoutActivity : AppCompatActivity(), PaymentResultListener {

    private var foodId: Int = 0
    private var hotelId: Int = 0
    private var price: Double = 0.0
    private var itemName: String = ""
    private var currentOrderId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        
        Checkout.preload(applicationContext)

        foodId = intent.getIntExtra("food_id", 0)
        hotelId = intent.getIntExtra("hotel_id", 0)
        price = intent.getDoubleExtra("price", 0.0)
        itemName = intent.getStringExtra("item_name") ?: ""

        findViewById<TextView>(R.id.tvItemName).text = itemName
        findViewById<TextView>(R.id.tvPrice).text = "₹$price"

        val btnPlaceOrder = findViewById<Button>(R.id.btnPlaceOrder)
        val rgPaymentMethod = findViewById<RadioGroup>(R.id.rgPaymentMethod)
        val rbOnline = findViewById<RadioButton>(R.id.rbOnline)

        btnPlaceOrder.setOnClickListener {
            val isOnline = rbOnline.isChecked
            placeOrderInDatabase(isOnline)
        }
    }

    private fun placeOrderInDatabase(isOnline: Boolean) {
        val session = SessionManager(this)
        val userId = session.getUserDetails()["id"]

        val itemArray = JSONArray()
        val itemObj = JSONObject().apply {
            put("food_item_id", foodId)
            put("quantity", 1)
            put("price", price)
        }
        itemArray.put(itemObj)

        val json = JSONObject().apply {
            put("user_id", userId)
            put("hotel_id", hotelId)
            put("total_amount", price)
            put("items", itemArray)
            put("payment_method", if (isOnline) "Online" else "Offline")
            put("delivery_address", session.getDeliveryAddress())
        }

        lifecycleScope.launch {
            try {
                val response = ApiClient.post("place_order.php", json)
                if (response.has("status") && response.getString("status") == "success") {
                    val data = response.getJSONObject("data")
                    currentOrderId = data.getInt("order_id")
                    
                    if (isOnline) {
                        startRazorpayPayment(currentOrderId, price)
                    } else {
                        Toast.makeText(this@CheckoutActivity, "Order placed successfully!", Toast.LENGTH_SHORT).show()
                        goToOrderTracking(currentOrderId)
                    }
                } else {
                    Toast.makeText(this@CheckoutActivity, "Failed to place order", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@CheckoutActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startRazorpayPayment(orderId: Int, amount: Double) {
        val checkout = Checkout()
        checkout.setKeyID("rzp_test_SqOZwDnHPrqJm0")
        
        try {
            val options = JSONObject()
            options.put("name", "Food Bridge")
            options.put("description", "Order #$orderId")
            options.put("theme.color", "#00b468")
            options.put("currency", "INR")
            // Razorpay takes amount in paise (multiply by 100)
            options.put("amount", (amount * 100).roundToInt())
            
            val prefill = JSONObject()
            val session = SessionManager(this)
            prefill.put("email", session.getUserDetails()["email"])
            prefill.put("contact", session.getUserDetails()["phone"])
            options.put("prefill", prefill)

            checkout.open(this as Activity, options)
        } catch (e: Exception) {
            Toast.makeText(this, "Error in payment: " + e.message, Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(razorpayPaymentID: String?) {
        // Update payment status in db
        lifecycleScope.launch {
            try {
                val json = JSONObject().apply {
                    put("order_id", currentOrderId)
                    put("payment_id", razorpayPaymentID)
                }
                ApiClient.post("update_payment_status.php", json)
                Toast.makeText(this@CheckoutActivity, "Payment Successful!", Toast.LENGTH_SHORT).show()
                goToOrderTracking(currentOrderId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onPaymentError(code: Int, response: String?) {
        Toast.makeText(this, "Payment failed. Try again.", Toast.LENGTH_SHORT).show()
    }

    private fun goToOrderTracking(orderId: Int) {
        val intent = Intent(this, OrderTrackingActivity::class.java)
        intent.putExtra("order_id", orderId)
        startActivity(intent)
        finish()
    }
}
