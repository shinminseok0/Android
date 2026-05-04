package com.example.shintech

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Phone(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("brand") val brand: String,
    @SerializedName("price") val price: Int,

    // 서버 응답과 로컬 더미 데이터 모두를 위해 존재
    @SerializedName("imageUrl") val imageUrl: String? = null,
    val imageResId: Int? = null, // 로컬 이미지를 위한 필드

    // 상세 정보에만 포함될 수 있는 필드
    @SerializedName("description") val description: String? = null,
    @SerializedName("rating") val rating: Float? = null,
    @SerializedName("reviewCount") val reviewCount: Int? = null
) : Serializable
