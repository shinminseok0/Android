package com.example.shintech

import com.google.gson.annotations.SerializedName

data class SignupRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("name") val name: String
)
