package com.example.shintech

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReviewListActivity : AppCompatActivity() {

    private val api = ApiClient.authApiService
    private lateinit var reviewAdapter: ReviewAdapter
    private var currentPhoneId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_list)

        currentPhoneId = intent.getLongExtra("phoneId", -1)
        val phoneName = intent.getStringExtra("phoneName") ?: "핸드폰"

        findViewById<TextView>(R.id.tvReviewListTitle).text = "${phoneName} 전체 리뷰"

        val rvReviewList = findViewById<RecyclerView>(R.id.rvReviewList)
        
        // 1. 어댑터 생성 시 삭제 버튼 클릭 처리 콜백을 전달합니다 (오류 해결 핵심)
        reviewAdapter = ReviewAdapter(emptyList()) { reviewId ->
            deleteReviewFromServer(reviewId)
        }
        rvReviewList.adapter = reviewAdapter

        if (currentPhoneId != -1L) {
            loadReviewsFromServer(currentPhoneId)
        }
    }

    // 리뷰 삭제 기능 구현
    private fun deleteReviewFromServer(reviewId: Long) {
        api.deleteReview(reviewId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ReviewListActivity, "리뷰가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    // 삭제 성공 후 목록 새로고침
                    if (currentPhoneId != -1L) loadReviewsFromServer(currentPhoneId)
                } else {
                    Toast.makeText(this@ReviewListActivity, "본인이 작성한 리뷰만 삭제할 수 있습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@ReviewListActivity, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadReviewsFromServer(phoneId: Long) {
        api.getPhoneReviews(phoneId).enqueue(object : Callback<List<PhoneReviewResponse>> {
            override fun onResponse(call: Call<List<PhoneReviewResponse>>, response: Response<List<PhoneReviewResponse>>) {
                if (response.isSuccessful) {
                    val reviews = response.body() ?: emptyList()
                    reviewAdapter.updateReviews(reviews)
                    if (reviews.isEmpty()) {
                        Toast.makeText(this@ReviewListActivity, "등록된 리뷰가 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ReviewListActivity, "리뷰를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<PhoneReviewResponse>>, t: Throwable) {
                Log.e("ReviewList_Error", "API 호출 실패", t)
                Toast.makeText(this@ReviewListActivity, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
