package com.example.shintech

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class TotalSalesActivity : AppCompatActivity() {

    private val api = ApiClient.authApiService
    private lateinit var tvTotalSales: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_total_sales)

        val btnBack: ImageButton = findViewById(R.id.btnBack)
        tvTotalSales = findViewById(R.id.tvTotalSales)

        btnBack.setOnClickListener {
            finish()
        }

        loadTotalSales()
    }

    private fun loadTotalSales() {
        api.getTotalSales().enqueue(object : Callback<Long> {
            override fun onResponse(call: Call<Long>, response: Response<Long>) {
                if (response.isSuccessful) {
                    val totalSales = response.body() ?: 0L
                    val formattedSales = NumberFormat.getCurrencyInstance(Locale.KOREA).format(totalSales)
                    tvTotalSales.text = formattedSales
                } else {
                    Log.e("TotalSalesActivity", "Failed to load total sales: ${response.code()}")
                    Toast.makeText(this@TotalSalesActivity, "권한이 없거나 불러오기에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    tvTotalSales.text = "조회 실패"
                }
            }

            override fun onFailure(call: Call<Long>, t: Throwable) {
                Log.e("TotalSalesActivity", "Error loading total sales", t)
                Toast.makeText(this@TotalSalesActivity, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                tvTotalSales.text = "오류 발생"
            }
        })
    }
}
