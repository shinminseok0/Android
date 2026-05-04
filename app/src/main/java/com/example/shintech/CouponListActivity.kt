package com.example.shintech

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CouponListActivity : AppCompatActivity() {

    private lateinit var rvCoupons: RecyclerView
    private lateinit var adapter: CouponAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coupon_list)

        rvCoupons = findViewById(R.id.rvCoupons)
        rvCoupons.layoutManager = LinearLayoutManager(this)

        // 쿠폰 기능이 제거되었습니다. 빈 목록을 표시합니다.
        adapter = CouponAdapter(emptyList()) {
            // no-op
        }
        rvCoupons.adapter = adapter

        Toast.makeText(this, "쿠폰 기능은 더 이상 지원되지 않습니다.", Toast.LENGTH_SHORT).show()
        finish() // 화면을 닫습니다.
    }
}
