package com.example.shintech

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MemberMainActivity : AppCompatActivity() {

    private val api = ApiClient.authApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_main)

        val cardEditProfile = findViewById<CardView>(R.id.cardEditProfile)
        val cardPayment = findViewById<CardView>(R.id.cardPayment)
        val cardInterest = findViewById<CardView>(R.id.cardInterest)
        val cardTotalSales = findViewById<CardView>(R.id.cardTotalSales)
        
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val btnDeleteAccount = findViewById<Button>(R.id.btnDeleteAccount)

        cardEditProfile.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        cardInterest.setOnClickListener {
            startActivity(Intent(this, InterestActivity::class.java))
        }
        
        cardPayment.setOnClickListener {
            startActivity(Intent(this, OrderHistoryActivity::class.java))
        }

        // --- 총 매출 버튼 연결 ---
        cardTotalSales.setOnClickListener {
            startActivity(Intent(this, TotalSalesActivity::class.java))
        }

        btnLogout.setOnClickListener {
            SessionManager.clearAuthToken(this)
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        btnDeleteAccount.setOnClickListener {
            val emailInput = EditText(this).apply { hint = "이메일" }
            val nameInput = EditText(this).apply { hint = "이름" }
            val passwordInput = EditText(this).apply {
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                hint = "비밀번호"
            }
            val layout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(60, 40, 60, 20)
                addView(emailInput)
                addView(nameInput)
                addView(passwordInput)
            }

            AlertDialog.Builder(this)
                .setTitle("회원 탈퇴")
                .setMessage("본인 확인 정보를 입력해주세요.")
                .setView(layout)
                .setPositiveButton("탈퇴") { _, _ ->
                    val request = WithdrawRequest(emailInput.text.toString(), nameInput.text.toString(), passwordInput.text.toString())
                    api.withdraw(request).enqueue(object: Callback<ResponseBody> {
                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@MemberMainActivity, "회원 탈퇴 완료", Toast.LENGTH_SHORT).show()
                                btnLogout.performClick()
                            }
                        }
                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {}
                    })
                }
                .setNegativeButton("취소", null)
                .show()
        }
    }
}
