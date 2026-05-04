package com.example.shintech

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommentPhoneSelectActivity : AppCompatActivity() {

    private val api = ApiClient.authApiService
    private lateinit var phoneAdapter: PhoneAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interest) // Reusing existing layout with RecyclerView

        findViewById<TextView>(R.id.tvInterestTitle)?.text = "댓글을 확인할 기종을 선택하세요"

        val rvPhoneList = findViewById<RecyclerView>(R.id.rvInterestList)
        
        phoneAdapter = PhoneAdapter(
            phoneList = emptyList(),
            isAdmin = false,
            onItemClick = { phone ->
                val intent = Intent(this, CommentListActivity::class.java)
                intent.putExtra("phoneId", phone.id)
                intent.putExtra("phoneName", phone.name)
                startActivity(intent)
            }
        )
        rvPhoneList.adapter = phoneAdapter

        loadPhonesFromServer()
    }

    private fun loadPhonesFromServer() {
        api.getPhones().enqueue(object : Callback<List<Phone>> {
            override fun onResponse(call: Call<List<Phone>>, response: Response<List<Phone>>) {
                if (response.isSuccessful) {
                    phoneAdapter.updatePhones(response.body() ?: emptyList())
                } else {
                    Toast.makeText(this@CommentPhoneSelectActivity, "목록 로드 실패", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<Phone>>, t: Throwable) {
                Toast.makeText(this@CommentPhoneSelectActivity, "네트워크 오류 발생", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
