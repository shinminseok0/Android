package com.example.shintech

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class PaymentActivity : AppCompatActivity() {

    private val api = ApiClient.authApiService
    private lateinit var tvFinalPrice: TextView

    private var originalTotalPrice: Long = 0L
    private var currentCartItems: List<CartItem> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        tvFinalPrice = findViewById(R.id.tvFinalPrice)
        val btnFinalPay = findViewById<Button>(R.id.btnFinalPay)

        loadCartData()

        btnFinalPay.setOnClickListener {
            startKakaoPay()
        }

        // 브라우저에서 리다이렉트되어 들어온 경우 처리
        handleDeepLink(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        val data: Uri? = intent?.data
        if (data != null && data.scheme == "shintech" && data.host == "payment") {
            // 결제 완료 메시지 표시
            Toast.makeText(this, "결제가 최종 완료되었습니다!", Toast.LENGTH_LONG).show()
            
            // 장바구니 비우기 및 상품 목록으로 이동
            api.clearCart().enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    val nextIntent = Intent(this@PaymentActivity, ProductListActivity::class.java)
                    nextIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(nextIntent)
                    finish()
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    finish()
                }
            })
        }
    }

    private fun loadCartData() {
        api.getCartItems().enqueue(object : Callback<CartResponse> {
            override fun onResponse(call: Call<CartResponse>, response: Response<CartResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    originalTotalPrice = body?.totalPrice ?: 0L
                    currentCartItems = body?.items ?: emptyList()
                    updatePriceUI()
                }
            }
            override fun onFailure(call: Call<CartResponse>, t: Throwable) {}
        })
    }

    private fun updatePriceUI() {
        tvFinalPrice.text = "${NumberFormat.getNumberInstance(Locale.KOREA).format(originalTotalPrice)}원"
    }

    private fun startKakaoPay() {
        if (currentCartItems.isEmpty()) {
            Toast.makeText(this, "장바구니가 비어있습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = getUserIdFromToken()
        val orderItems = currentCartItems.map { OrderItemRequest(it.phoneId, it.quantity) }
        
        val request = KakaoPayRequest(
            userId = userId,
            items = orderItems,
            userCouponId = null // 쿠폰 기능 제거로 null 전달
        )

        api.readyKakaoPay(request).enqueue(object : Callback<KakaoPayResponse> {
            override fun onResponse(call: Call<KakaoPayResponse>, response: Response<KakaoPayResponse>) {
                if (response.isSuccessful) {
                    // 에뮬레이터 환경을 고려하여 PC URL을 우선 사용
                    val redirectUrl = response.body()?.nextRedirectPcUrl ?: response.body()?.nextRedirectAppUrl
                    if (redirectUrl != null) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(redirectUrl))
                        startActivity(intent)
                    }
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Log.e("KakaoPay_Error", "응답 실패: $errorMsg")
                    Toast.makeText(this@PaymentActivity, "카카오페이 준비 실패", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<KakaoPayResponse>, t: Throwable) {
                Toast.makeText(this@PaymentActivity, "네트워크 오류", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getUserIdFromToken(): Long {
        val token = SessionManager.getAuthToken(this) ?: return 0L
        return try {
            val parts = token.split(".")
            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
            JSONObject(payload).getString("sub").toLong()
        } catch (e: Exception) { 0L }
    }
}
