package com.example.shintech

import com.google.gson.annotations.SerializedName

data class FavoriteRequest(
    @SerializedName("phoneId") val phoneId: Long
)
