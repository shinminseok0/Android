package com.example.shintech

import com.google.gson.annotations.SerializedName

data class KakaoPayRequest(
    @SerializedName("userId") val userId: Long,
    @SerializedName("items") val items: List<OrderItemRequest>,
    @SerializedName("userCouponId") val userCouponId: Long? = null
)

data class OrderItemRequest(
    @SerializedName("phoneId") val phoneId: Long,
    @SerializedName("quantity") val quantity: Int
)
