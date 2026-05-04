package com.example.shintech

import com.google.gson.annotations.SerializedName

data class CommentsCreateRequest(
    @SerializedName("phoneId") val phoneId: Long,
    @SerializedName("content") val content: String
)
