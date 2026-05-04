package com.example.shintech

import com.google.gson.annotations.SerializedName

data class KakaoPayResponse(
    @SerializedName("tid") val tid: String,
    @SerializedName("next_redirect_pc_url") val nextRedirectPcUrl: String,
    @SerializedName("next_redirect_app_url") val nextRedirectAppUrl: String
)
