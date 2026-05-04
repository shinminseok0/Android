package com.example.shintech

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderHistoryActivity : AppCompatActivity() {

    private val api = ApiClient.authApiService
    private lateinit var rvOrderHistory: RecyclerView
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)

        val btnBack: ImageButton = findViewById(R.id.btnBack)
        rvOrderHistory = findViewById(R.id.rvOrderHistory)

        btnBack.setOnClickListener {
            finish()
        }

        setupRecyclerView()
        loadOrderHistory()
    }

    private fun setupRecyclerView() {
        orderHistoryAdapter = OrderHistoryAdapter(emptyList())
        rvOrderHistory.apply {
            adapter = orderHistoryAdapter
            layoutManager = LinearLayoutManager(this@OrderHistoryActivity)
        }
    }

    private fun loadOrderHistory() {
        api.getOrderHistory().enqueue(object : Callback<List<OrderHistoryResponse>> {
            override fun onResponse(
                call: Call<List<OrderHistoryResponse>>,
                response: Response<List<OrderHistoryResponse>>
            ) {
                if (response.isSuccessful) {
                    val orders = response.body() ?: emptyList()
                    orderHistoryAdapter.updateOrders(orders)
                } else {
                    Log.e("OrderHistoryActivity", "Failed to load order history: ${response.code()}")
                    Toast.makeText(this@OrderHistoryActivity, "주문 내역을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<OrderHistoryResponse>>, t: Throwable) {
                Log.e("OrderHistoryActivity", "Error loading order history", t)
                Toast.makeText(this@OrderHistoryActivity, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
