package com.example.shintech

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private val api = ApiClient.authApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etName = findViewById<EditText>(R.id.etName)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val name = etName.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
                Toast.makeText(this, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "올바른 이메일 형식을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val signupRequest = SignupRequest(email, password, name)
            api.signup(signupRequest).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@RegisterActivity, "회원가입 성공! 로그인해주세요.", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        // 서버가 보낸 에러 메시지를 확인하여 보여줌
                        val errorBody = response.errorBody()?.string() ?: "알 수 없는 오류가 발생했습니다."
                        Toast.makeText(this@RegisterActivity, "회원가입 실패: ${errorBody}", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("RegisterAPI_Error", "회원가입 API 호출 실패", t)
                    Toast.makeText(this@RegisterActivity, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
