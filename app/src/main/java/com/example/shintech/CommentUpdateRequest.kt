package com.example.shintech

import com.google.gson.annotations.SerializedName

data class CommentUpdateRequest(
    @SerializedName("content") val content: String
)
