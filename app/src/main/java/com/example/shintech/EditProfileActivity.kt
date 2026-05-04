package com.example.shintech

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileActivity : AppCompatActivity() {

    private val api = ApiClient.authApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val etName = findViewById<EditText>(R.id.etName)
        val etNewPassword = findViewById<EditText>(R.id.etNewPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnSaveChanges = findViewById<Button>(R.id.btnSaveChanges)

        btnSaveChanges.setOnClickListener {
            val name = etName.text.toString().trim()
            val newPassword = etNewPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            // 필수 입력 검사
            if (name.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 비밀번호 확인 검사
            if (newPassword != confirmPassword) {
                Toast.makeText(this, "비밀번호 확인이 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // API 명세서(UpdateUserRequest)에 맞게 name과 password만 전송
            val request = UpdateUserRequest(
                name = name,
                password = newPassword
            )

            api.updateUser(request).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@EditProfileActivity, "회원 정보가 수정되었습니다.", Toast.LENGTH_SHORT).show()
                        finish() // 수정 완료 후 마이페이지로 돌아감
                    } else {
                        Toast.makeText(this@EditProfileActivity, "정보 수정에 실패했습니다. (에러 코드: ${response.code()})", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@EditProfileActivity, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
