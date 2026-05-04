package com.example.shintech

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FindPasswordActivity : AppCompatActivity() {

    private val api = ApiClient.authApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_password)

        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val btnFindPassword = findViewById<Button>(R.id.btnFindPassword)

        btnFindPassword.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()

            if (name.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "이름과 이메일을 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = PasswordResetRequest(name = name, email = email)
            // 콜백의 타입을 PasswordResetResponse 로 정확하게 수정합니다.
            api.findPassword(request).enqueue(object : Callback<PasswordResetResponse> {
                override fun onResponse(call: Call<PasswordResetResponse>, response: Response<PasswordResetResponse>) {
                    if (response.isSuccessful) {
                        val tempPassword = response.body()?.temporaryPassword
                        if (tempPassword != null) {
                            // 성공 시, 임시 비밀번호를 알림창으로 보여줌
                            AlertDialog.Builder(this@FindPasswordActivity)
                                .setTitle("임시 비밀번호 발급 완료")
                                .setMessage("임시 비밀번호는 [ ${tempPassword} ] 입니다.\n로그인 후 반드시 비밀번호를 변경해주세요.")
                                .setPositiveButton("확인") { _, _ -> finish() }
                                .setCancelable(false)
                                .show()
                        } else {
                            Toast.makeText(this@FindPasswordActivity, "임시 비밀번호를 받지 못했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@FindPasswordActivity, "일치하는 사용자 정보가 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<PasswordResetResponse>, t: Throwable) {
                    Log.e("FindPassword_Error", "비밀번호 찾기 API 호출 실패", t)
                    Toast.makeText(this@FindPasswordActivity, "네트워크 오류가 발생했습니다. Logcat을 확인하세요.", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}
