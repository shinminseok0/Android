package com.example.shintech

import com.google.gson.annotations.SerializedName

data class PhoneRatingResponse(
    @SerializedName("phoneId") val phoneId: Long,
    @SerializedName("averageRating") val averageRating: Double
)
