package com.example.shintech

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private val api = ApiClient.authApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnGuest = findViewById<Button>(R.id.btnGuest)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)
        val tvFindPassword = findViewById<TextView>(R.id.tvFindPassword)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "이메일과 비밀번호를 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val loginRequest = LoginRequest(email, password)
            api.login(loginRequest).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val token = response.body()?.token
                        if (token != null) {
                            SessionManager.saveAuthToken(this@MainActivity, token)
                            // 관리자 계정 확인 및 역할 저장
                            if (email == "admin@admin.com") { // 관리자 계정 수정
                                SessionManager.saveUserRole(this@MainActivity, "ADMIN")
                            } else {
                                SessionManager.saveUserRole(this@MainActivity, "USER")
                            }

                            Toast.makeText(this@MainActivity, "로그인 성공!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@MainActivity, ProductListActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@MainActivity, "로그인에 실패했습니다: 토큰이 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@MainActivity, "이메일 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            })
        }

        btnGuest.setOnClickListener {
            SessionManager.clearAuthToken(this)
            SessionManager.saveUserRole(this, "GUEST")
            val intent = Intent(this, ProductListActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        tvFindPassword.setOnClickListener {
            val intent = Intent(this, FindPasswordActivity::class.java)
            startActivity(intent)
        }
    }
}
