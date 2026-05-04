package com.example.shintech

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    // 로그캣 분석 결과 http -> https 리다이렉트 시 POST가 GET으로 바뀌는 문제가 확인되었습니다.
    // 이를 해결하기 위해 처음부터 https 주소를 사용하여 리다이렉트를 방지합니다.
    private const val BASE_URL = "https://simphone.kro.kr/api/"
    private lateinit var retrofit: Retrofit
    lateinit var authApiService: ApiService
        private set

    fun init(context: Context) {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val noAuthHeader = originalRequest.header("No-Authentication")

            if (noAuthHeader != null) {
                chain.proceed(originalRequest.newBuilder().removeHeader("No-Authentication").build())
            } else {
                val token = SessionManager.getAuthToken(context)
                val requestBuilder = originalRequest.newBuilder()
                if (token != null) {
                    requestBuilder.header("Authorization", "Bearer $token")
                }
                chain.proceed(requestBuilder.build())
            }
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            // 리다이렉트 설정 강제 유지
            .followRedirects(true)
            .followSslRedirects(true)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        authApiService = retrofit.create(ApiService::class.java)
    }
}
