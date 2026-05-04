package com.example.shintech

import com.google.gson.annotations.SerializedName

data class OrderHistoryResponse(
    @SerializedName("orderId") val orderId: Long,
    @SerializedName("status") val status: String,
    @SerializedName("finalPrice") val finalPrice: Int,
    @SerializedName("createdAt") val createdAt: String, // LocalDateTime -> String
    @SerializedName("paidAt") val paidAt: String,      // LocalDateTime -> String
    @SerializedName("items") val items: List<OrderHistoryItemResponse>
)

data class OrderHistoryItemResponse(
    @SerializedName("phoneId") val phoneId: Long,
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Int,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("imageUrl") val imageUrl: String?
)
