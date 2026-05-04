package com.example.shintech

import com.google.gson.annotations.SerializedName

data class PasswordResetRequest(
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String
)
