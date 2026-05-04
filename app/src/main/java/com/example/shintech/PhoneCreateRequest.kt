package com.example.shintech

import com.google.gson.annotations.SerializedName

data class PhoneCreateRequest(
    @SerializedName("name") val name: String,
    @SerializedName("brand") val brand: String,
    @SerializedName("price") val price: Int
)
