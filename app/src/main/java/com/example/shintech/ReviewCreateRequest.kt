package com.example.shintech

import com.google.gson.annotations.SerializedName

data class ReviewCreateRequest(
    @SerializedName("phoneId") val phoneId: Long,
    @SerializedName("rating") val rating: Double, // 명세서에 따른 double 타입 추가
    @SerializedName("content") val content: String
)
