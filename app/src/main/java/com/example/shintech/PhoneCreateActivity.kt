package com.example.shintech

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class PhoneCreateActivity : AppCompatActivity() {

    private val api = ApiClient.authApiService
    private lateinit var ivPreview: ImageView
    private var selectedImageResId: Int? = null
    private var editingPhoneId: Long? = null

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val resId = result.data?.getIntExtra("selected_image_res_id", 0)
            if (resId != null && resId != 0) {
                selectedImageResId = resId
                ivPreview.setImageResource(resId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_create)

        val tvTitle = findViewById<TextView>(R.id.tv_phone_create_title)
        val etName = findViewById<EditText>(R.id.et_phone_name)
        val etBrand = findViewById<EditText>(R.id.et_phone_brand)
        val etPrice = findViewById<EditText>(R.id.et_phone_price)
        val btnSelectImage = findViewById<Button>(R.id.btn_select_image)
        ivPreview = findViewById(R.id.iv_preview)
        val btnCreatePhone = findViewById<Button>(R.id.btn_create_phone)

        // 수정 모드 확인
        val phoneToEdit = intent.getSerializableExtra("phone_to_edit") as? Phone
        if (phoneToEdit != null) {
            editingPhoneId = phoneToEdit.id
            tvTitle?.text = "상품 수정"
            btnCreatePhone.text = "수정 완료"
            etName.setText(phoneToEdit.name)
            etBrand.setText(phoneToEdit.brand)
            etPrice.setText(phoneToEdit.price.toString())
            ivPreview.load(phoneToEdit.imageResId ?: phoneToEdit.imageUrl)
            selectedImageResId = phoneToEdit.imageResId
        }

        btnSelectImage.setOnClickListener {
            val intent = Intent(this, ImagePickerActivity::class.java)
            imagePickerLauncher.launch(intent)
        }

        btnCreatePhone.setOnClickListener {
            val name = etName.text.toString().trim()
            val brand = etBrand.text.toString().trim()
            val price = etPrice.text.toString().toIntOrNull()

            if (name.isEmpty() || brand.isEmpty() || price == null) {
                Toast.makeText(this, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val createRequest = PhoneCreateRequest(name, brand, price)
            val requestJson = Gson().toJson(createRequest)
            val requestBody = requestJson.toRequestBody("application/json".toMediaTypeOrNull())

            var filePart: MultipartBody.Part? = null
            if (selectedImageResId != null) {
                val uri = Uri.parse("android.resource://$packageName/$selectedImageResId")
                uriToFile(uri)?.let {
                    val fileBody = it.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    filePart = MultipartBody.Part.createFormData("image", it.name, fileBody)
                }
            }

            if (editingPhoneId != null) {
                // 수정 API 호출
                api.updatePhone(editingPhoneId!!, requestBody, filePart).enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@PhoneCreateActivity, "상품이 수정되었습니다.", Toast.LENGTH_SHORT).show()
                            setResult(Activity.RESULT_OK)
                            finish()
                        } else {
                            Toast.makeText(this@PhoneCreateActivity, "수정 실패 (${response.code()})", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(this@PhoneCreateActivity, "네트워크 오류", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                // 등록 API 호출
                if (filePart == null) {
                    val defaultUri = Uri.parse("android.resource://$packageName/${R.drawable.ic_launcher_background}")
                    uriToFile(defaultUri)?.let {
                        val fileBody = it.asRequestBody("image/jpeg".toMediaTypeOrNull())
                        filePart = MultipartBody.Part.createFormData("image", it.name, fileBody)
                    }
                }
                
                api.createPhone(requestBody, filePart).enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@PhoneCreateActivity, "상품이 등록되었습니다.", Toast.LENGTH_SHORT).show()
                            setResult(Activity.RESULT_OK)
                            finish()
                        } else {
                            Toast.makeText(this@PhoneCreateActivity, "등록 실패 (${response.code()})", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(this@PhoneCreateActivity, "네트워크 오류", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    private fun uriToFile(uri: Uri): File? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val tempFile = File.createTempFile("temp_upload", ".jpg", cacheDir)
            FileOutputStream(tempFile).use { it.write(inputStream.readBytes()) }
            tempFile
        } catch (e: Exception) { null }
    }
}
