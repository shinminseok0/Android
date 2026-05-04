package com.example.shintech

import com.google.gson.annotations.SerializedName

data class CouponCreateRequest(
    @SerializedName("name") val name: String,
    @SerializedName("discountAmount") val discountAmount: Int,
    @SerializedName("minOrderAmount") val minOrderAmount: Int
)
