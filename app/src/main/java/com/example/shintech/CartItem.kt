package com.example.shintech

import com.google.gson.annotations.SerializedName

data class CartItem(
    @SerializedName("phoneId") val phoneId: Long,
    @SerializedName("name") val name: String,
    @SerializedName("brand") val brand: String,
    @SerializedName("price") val price: Int,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("quantity") val quantity: Int
)
