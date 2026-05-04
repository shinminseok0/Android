package com.example.shintech

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class CartActivity : AppCompatActivity() {

    private val api = ApiClient.authApiService
    private lateinit var rvCartItems: RecyclerView
    private lateinit var tvTotalPrice: TextView
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        rvCartItems = findViewById(R.id.rvCartItems)
        tvTotalPrice = findViewById(R.id.tvTotalPrice)
        
        val btnClearCart = findViewById<Button>(R.id.btnClearCart)
        val btnGoToPayment = findViewById<Button>(R.id.btnGoToPayment)

        setupRecyclerView()

        // 장바구니 비우기 버튼
        btnClearCart.setOnClickListener {
            api.clearCart().enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@CartActivity, "장바구니를 모두 비웠습니다.", Toast.LENGTH_SHORT).show()
                        loadCartItemsFromServer()
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {}
            })
        }

        // 구매하러 가기 버튼 -> 결제 화면으로 이동
        btnGoToPayment.setOnClickListener {
            val intent = Intent(this, PaymentActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadCartItemsFromServer()
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            emptyList(),
            onIncrease = { cartItem ->
                api.addToCart(CartItemAddRequest(cartItem.phoneId)).enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) loadCartItemsFromServer()
                    }
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {}
                })
            },
            onDecrease = { cartItem ->
                api.decreaseCartItem(CartItemDecreaseRequest(cartItem.phoneId)).enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) loadCartItemsFromServer()
                    }
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {}
                })
            },
            onDelete = { cartItem ->
                api.deleteCartItem(cartItem.phoneId).enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@CartActivity, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                            loadCartItemsFromServer()
                        }
                    }
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {}
                })
            }
        )
        rvCartItems.adapter = cartAdapter
        rvCartItems.layoutManager = LinearLayoutManager(this)
    }

    private fun loadCartItemsFromServer() {
        api.getCartItems().enqueue(object : Callback<CartResponse> {
            override fun onResponse(call: Call<CartResponse>, response: Response<CartResponse>) {
                if (response.isSuccessful) {
                    val items = response.body()?.items ?: emptyList()
                    val totalPrice = response.body()?.totalPrice ?: 0L
                    cartAdapter.updateItems(items)
                    tvTotalPrice.text = "${NumberFormat.getNumberInstance(Locale.KOREA).format(totalPrice)}원"
                } else {
                    Log.e("CartActivity", "Failed to load cart items: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                Log.e("CartActivity", "Error loading cart items", t)
                Toast.makeText(this@CartActivity, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
