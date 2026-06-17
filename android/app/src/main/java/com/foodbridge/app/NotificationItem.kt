package com.foodbridge.app

data class NotificationItem(
    val id: Int,
    val hotelId: Int,
    val hotelName: String,
    val message: String,
    val type: String,
    val createdAt: String
)
