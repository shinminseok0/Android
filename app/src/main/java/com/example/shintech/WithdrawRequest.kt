package com.example.shintech

import com.google.gson.annotations.SerializedName

data class WithdrawRequest(
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
    @SerializedName("password") val password: String
)
