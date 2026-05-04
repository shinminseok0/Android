package com.example.shintech

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ReviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        val btnWriteReview = findViewById<Button>(R.id.btnWriteReview)

        btnWriteReview.setOnClickListener {
            Toast.makeText(this, "리뷰 작성 화면으로 이동합니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
