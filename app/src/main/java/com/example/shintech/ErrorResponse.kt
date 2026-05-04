package com.example.shintech

import com.google.gson.annotations.SerializedName

// 서버의 에러 응답을 파싱하기 위한 데이터 클래스
data class ErrorResponse(
    @SerializedName("timestamp") val timestamp: String?,
    @SerializedName("status") val status: Int?,
    @SerializedName("error") val error: String?,
    @SerializedName("message") val message: String?, // 가장 중요한 에러 메시지 필드
    @SerializedName("path") val path: String?
)
