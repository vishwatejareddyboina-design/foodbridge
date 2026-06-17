package com.foodbridge.app

data class FoodItem(
    val id: Int,
    val hotelId: Int,
    val hotelName: String?,
    val itemName: String,
    val originalPrice: Double,
    val discountedPrice: Double,
    val imageUrl: String? = null,
    val hygienePercentage: Double? = null
)
