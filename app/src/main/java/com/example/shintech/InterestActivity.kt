package com.example.shintech

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InterestActivity : AppCompatActivity() {

    private val api = ApiClient.authApiService
    private lateinit var phoneAdapter: PhoneAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interest)

        val rvInterestList = findViewById<RecyclerView>(R.id.rvInterestList)

        // 어댑터 설정
        phoneAdapter = PhoneAdapter(
            phoneList = emptyList(),
            isAdmin = false,
            onItemClick = { phone ->
                val intent = Intent(this, ProductDetailActivity::class.java)
                intent.putExtra("phone", phone)
                startActivity(intent)
            }
        )
        rvInterestList.adapter = phoneAdapter

        // 서버에서 찜 목록 불러오기
        loadFavoritesFromServer()
    }

    private fun loadFavoritesFromServer() {
        // 1. 먼저 사용자의 찜 목록(ID 리스트)을 가져옵니다.
        api.getFavorites().enqueue(object : Callback<List<FavoritePhoneResponse>> {
            override fun onResponse(call: Call<List<FavoritePhoneResponse>>, response: Response<List<FavoritePhoneResponse>>) {
                if (response.isSuccessful) {
                    val favorites = response.body() ?: emptyList()
                    val favoriteIds = favorites.map { it.phoneId }.toSet()

                    // 2. 전체 상품 목록 API를 호출하여 필터링
                    api.getPhones().enqueue(object : Callback<List<Phone>> {
                        override fun onResponse(call: Call<List<Phone>>, response: Response<List<Phone>>) {
                            if (response.isSuccessful) {
                                val allPhones = response.body() ?: emptyList()
                                val favoritePhones = allPhones.filter { it.id in favoriteIds }
                                phoneAdapter.updatePhones(favoritePhones)
                            } else {
                                Toast.makeText(this@InterestActivity, "상품 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<List<Phone>>, t: Throwable) {
                            Toast.makeText(this@InterestActivity, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    Toast.makeText(this@InterestActivity, "찜 목록을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<FavoritePhoneResponse>>, t: Throwable) {
                Log.e("Interest_Error", "API 호출 실패", t)
                Toast.makeText(this@InterestActivity, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
