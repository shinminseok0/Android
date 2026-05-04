package com.example.shintech

import com.google.gson.annotations.SerializedName

data class CartResponse(
    @SerializedName("items") val items: List<CartItem>,
    @SerializedName("totalPrice") val totalPrice: Long
)
