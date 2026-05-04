package com.example.shintech

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddressActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)

        val btnAddAddress = findViewById<Button>(R.id.btnAddAddress)

        btnAddAddress.setOnClickListener {
            // 새 주소 추가 화면으로 이동하는 로직 (시뮬레이션)
            Toast.makeText(this, "새 주소 추가 화면으로 이동합니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
