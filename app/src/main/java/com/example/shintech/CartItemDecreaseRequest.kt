package com.example.shintech

import com.google.gson.annotations.SerializedName

data class CartItemDecreaseRequest(
    @SerializedName("phoneId") val phoneId: Long
)
