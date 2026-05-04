package com.example.shintech

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class ImagePickerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_picker)

        val rvImagePicker = findViewById<RecyclerView>(R.id.rv_image_picker)

        // drawable 폴더의 모든 이미지 리소스를 가져옵니다.
        val imageList = mutableListOf<Int>()
        val fields = R.drawable::class.java.fields
        for (field in fields) {
            if (field.name.startsWith("galaxy_") || field.name.startsWith("iphone_")) {
                imageList.add(field.getInt(null))
            }
        }

        val adapter = ImagePickerAdapter(imageList) { selectedImageResId ->
            val resultIntent = Intent()
            resultIntent.putExtra("selected_image_res_id", selectedImageResId)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
        rvImagePicker.adapter = adapter
    }
}
