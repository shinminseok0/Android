package com.example.shintech

import com.google.gson.annotations.SerializedName

data class PhoneReviewResponse(
    @SerializedName("reviewId") val reviewId: Long,
    @SerializedName("rating") val rating: Double,
    @SerializedName("content") val content: String,
    @SerializedName("userId") val userId: Long,
    @SerializedName("userName") val userName: String,
    @SerializedName("createdAt") val createdAt: String // 서버에서 LocalDateTime 문자열로 내려옴
)
