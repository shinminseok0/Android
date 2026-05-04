package com.example.shintech

import com.google.gson.annotations.SerializedName

data class PasswordResetResponse(
    @SerializedName("temporaryPassword") val temporaryPassword: String
)
