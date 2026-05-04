package com.example.shintech

import com.google.gson.annotations.SerializedName

data class CartItemAddRequest(
    @SerializedName("phoneId") val phoneId: Long
)
