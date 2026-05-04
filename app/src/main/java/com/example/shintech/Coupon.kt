package com.example.shintech

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Coupon(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("discountAmount") val discountAmount: Int,
    @SerializedName("minOrderAmount") val minOrderAmount: Int,
    @SerializedName("expiryDate") val expiryDate: String? = null
) : Serializable
