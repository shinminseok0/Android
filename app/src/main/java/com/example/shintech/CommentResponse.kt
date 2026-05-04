package com.example.shintech

import com.google.gson.annotations.SerializedName

data class CommentResponse(
    @SerializedName("commentId") val commentId: Long,
    @SerializedName("userId") val userId: Long,
    @SerializedName("userName") val userName: String,
    @SerializedName("content") val content: String
)
