package com.example.shintech

import com.google.gson.annotations.SerializedName

data class UpdateUserRequest(
    @SerializedName("name") val name: String,
    @SerializedName("password") val password: String
)
