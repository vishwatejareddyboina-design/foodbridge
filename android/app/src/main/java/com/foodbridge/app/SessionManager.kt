package com.foodbridge.app

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("FoodBridgePrefs", Context.MODE_PRIVATE)
    
    fun createLoginSession(id: Int, name: String, email: String, role: String) {
        val editor = prefs.edit()
        editor.putBoolean("IS_LOGGED_IN", true)
        editor.putInt("USER_ID", id)
        editor.putString("USER_NAME", name)
        editor.putString("USER_EMAIL", email)
        editor.putString("USER_ROLE", role)
        editor.apply()
    }
    
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("IS_LOGGED_IN", false)
    }
    
    fun getUserDetails(): HashMap<String, String> {
        val user = HashMap<String, String>()
        user["id"] = prefs.getInt("USER_ID", -1).toString()
        user["name"] = prefs.getString("USER_NAME", "")!!
        user["email"] = prefs.getString("USER_EMAIL", "")!!
        user["role"] = prefs.getString("USER_ROLE", "")!!
        return user
    }
    
    fun getUserName(): String {
        return prefs.getString("USER_NAME", "")!!
    }

    fun getUserEmail(): String {
        return prefs.getString("USER_EMAIL", "")!!
    }
    
    fun saveResetRole(role: String) {
        val editor = prefs.edit()
        editor.putString("RESET_ROLE", role)
        editor.apply()
    }
    
    fun getResetRole(): String {
        return prefs.getString("RESET_ROLE", "user")!!
    }
    
    fun saveResetEmail(email: String) {
        val editor = prefs.edit()
        editor.putString("RESET_EMAIL", email)
        editor.apply()
    }
    
    fun getResetEmail(): String {
        return prefs.getString("RESET_EMAIL", "")!!
    }
    
    fun logoutUser() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }

    fun saveDeliveryAddress(address: String) {
        val editor = prefs.edit()
        editor.putString("delivery_address", address)
        editor.apply()
    }

    fun getDeliveryAddress(): String {
        return prefs.getString("delivery_address", "") ?: ""
    }
}
