package com.example.shintech

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductListActivity : AppCompatActivity() {

    private val api = ApiClient.authApiService
    private lateinit var phoneAdapter: PhoneAdapter
    private var allPhones = mutableListOf<Phone>()
    private var currentSortOrderAscending = true

    private val createPhoneLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // 상품 등록 성공 시 서버에서 진짜 데이터를 다시 불러옵니다.
            loadPhonesFromServer()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        val rvPhoneList = findViewById<RecyclerView>(R.id.rvPhoneList)
        
        val isAdmin = SessionManager.isUserAdmin(this)
        
        phoneAdapter = PhoneAdapter(
            phoneList = emptyList(),
            isAdmin = isAdmin,
            onItemClick = { phone ->
                val intent = Intent(this, ProductDetailActivity::class.java)
                intent.putExtra("phone", phone)
                startActivity(intent)
            },
            onDeleteClick = { phone ->
                showDeleteConfirmDialog(phone)
            }
        )
        rvPhoneList.adapter = phoneAdapter

        setupUI()
    }

    private fun showDeleteConfirmDialog(phone: Phone) {
        AlertDialog.Builder(this)
            .setTitle("상품 삭제")
            .setMessage("${phone.name} 상품을 삭제하시겠습니까?")
            .setPositiveButton("삭제") { _, _ ->
                deletePhone(phone)
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun deletePhone(phone: Phone) {
        api.deletePhone(phone.id).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProductListActivity, "상품이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    loadPhonesFromServer()
                } else {
                    Toast.makeText(this@ProductListActivity, "삭제 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@ProductListActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        // 화면이 사용자에게 보일 때마다 최신 DB 정보를 서버에서 불러옵니다.
        loadPhonesFromServer()
    }

    private fun setupUI() {
        val searchView = findViewById<SearchView>(R.id.searchView)
        val chipGroup = findViewById<ChipGroup>(R.id.chipGroup_brands)
        val sortSpinner = findViewById<Spinner>(R.id.spinner_sort)
        val sortOrderButton = findViewById<ImageButton>(R.id.btn_sort_order)
        val fabAddPhone = findViewById<FloatingActionButton>(R.id.fab_add_phone)
        val btnMyPage = findViewById<ImageButton>(R.id.btnMyPage)
        val btnCart = findViewById<ImageButton>(R.id.btnCart)
        
        if (SessionManager.isUserAdmin(this)) {
            fabAddPhone.visibility = View.VISIBLE
        }
        fabAddPhone.setOnClickListener {
             createPhoneLauncher.launch(Intent(this, PhoneCreateActivity::class.java))
        }

        btnMyPage.setOnClickListener {
            if (SessionManager.isGuest(this)) {
                showLoginRequiredDialog()
            } else {
                startActivity(Intent(this, MemberMainActivity::class.java))
            }
        }
        
        btnCart.setOnClickListener {
            if (SessionManager.isGuest(this)) {
                showLoginRequiredDialog()
            } else {
                startActivity(Intent(this, CartActivity::class.java))
            }
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                applyFilters()
                return true
            }
        })

        chipGroup.setOnCheckedChangeListener { _, _ -> applyFilters() }

        ArrayAdapter.createFromResource(
            this, R.array.sort_options, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            sortSpinner.adapter = adapter
        }
        sortSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                applyFilters()
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }

        sortOrderButton.setOnClickListener {
            currentSortOrderAscending = !currentSortOrderAscending
            val iconRes = if (currentSortOrderAscending) R.drawable.ic_sort_ascending else R.drawable.ic_sort_descending
            sortOrderButton.setImageResource(iconRes)
            applyFilters()
        }
    }

    private fun showLoginRequiredDialog() {
        AlertDialog.Builder(this)
            .setTitle("로그인 필요")
            .setMessage("이 기능은 로그인이 필요합니다. 로그인 화면으로 이동하시겠습니까?")
            .setPositiveButton("확인") { _, _ ->
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun loadPhonesFromServer() {
        api.getPhones().enqueue(object : Callback<List<Phone>> {
            override fun onResponse(call: Call<List<Phone>>, response: Response<List<Phone>>) {
                if (response.isSuccessful) {
                    val phones = response.body() ?: emptyList()
                    allPhones.clear()
                    allPhones.addAll(phones)
                    applyFilters() // 데이터를 불러온 후 필터와 정렬을 즉시 적용
                } else {
                    // 비회원일 때 401 에러가 날 수 있으므로 예외 처리 필요할 수도 있음
                    if (response.code() == 401) {
                        // 만약 서버에서 비회원 접근을 막고 있다면 여기서 처리
                        Toast.makeText(this@ProductListActivity, "상품을 불러오려면 로그인이 필요할 수 있습니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@ProductListActivity, "상품 목록 로드 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<List<Phone>>, t: Throwable) {
                Toast.makeText(this@ProductListActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    
    private fun applyFilters() {
        val searchQuery = findViewById<SearchView>(R.id.searchView).query.toString()
        val checkedChipId = findViewById<ChipGroup>(R.id.chipGroup_brands).checkedChipId
        val selectedSort = findViewById<Spinner>(R.id.spinner_sort).selectedItem?.toString() ?: "기본"

        var filteredList = allPhones.toList()

        // 1. 검색 필터
        if (searchQuery.isNotBlank()) {
            filteredList = filteredList.filter {
                it.name.contains(searchQuery, ignoreCase = true) || it.brand.contains(searchQuery, ignoreCase = true)
            }
        }

        // 2. 브랜드 필터
        when (checkedChipId) {
            R.id.chip_samsung -> filteredList = filteredList.filter { it.brand.equals("Samsung", ignoreCase = true) }
            R.id.chip_apple -> filteredList = filteredList.filter { it.brand.equals("Apple", ignoreCase = true) }
        }

        // 3. 정렬
        val sortedList = when (selectedSort) {
            "가나다순" -> if (currentSortOrderAscending) filteredList.sortedBy { it.name } else filteredList.sortedByDescending { it.name }
            "별점순" -> if (currentSortOrderAscending) filteredList.sortedBy { it.rating ?: 0f } else filteredList.sortedByDescending { it.rating ?: 0f }
            else -> filteredList
        }

        phoneAdapter.updatePhones(sortedList)
    }
}
