package com.example.shintech

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import coil.load
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class ProductDetailActivity : AppCompatActivity() {

    private val api = ApiClient.authApiService
    private var isFavorite = false
    private lateinit var btnFavorite: ImageButton
    private lateinit var rbDetailRating: RatingBar
    private lateinit var tvDetailRatingValue: TextView
    private lateinit var tvDetailReviewCount: TextView
    
    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var tvEmptyReviews: TextView
    private lateinit var tvEmptyComments: TextView

    private var currentQuantity = 1
    private var currentPhone: Phone? = null

    private val editPhoneLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            currentPhone?.id?.let { reloadPhoneDetail(it) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        // UI 요소 연결
        btnFavorite = findViewById(R.id.btnFavorite)
        rbDetailRating = findViewById(R.id.rbDetailRating)
        tvDetailRatingValue = findViewById(R.id.tvDetailRatingValue)
        tvDetailReviewCount = findViewById(R.id.tvDetailReviewCount)
        
        val btnAddToCart = findViewById<Button>(R.id.btnAddToCart)
        val etCommentContent = findViewById<EditText>(R.id.etCommentContent)
        val btnSubmitComment = findViewById<Button>(R.id.btnSubmitComment)
        val rbInputRating = findViewById<RatingBar>(R.id.rbInputRating)
        val etReviewContent = findViewById<EditText>(R.id.etReviewContent)
        val btnSubmitReview = findViewById<Button>(R.id.btnSubmitReview)
        
        val rvReviewList = findViewById<RecyclerView>(R.id.rvReviewList)
        val rvCommentList = findViewById<RecyclerView>(R.id.rvCommentList)
        tvEmptyReviews = findViewById(R.id.tvEmptyReviews)
        tvEmptyComments = findViewById(R.id.tvEmptyComments)

        // 관리자용 UI
        val layoutAdminActions = findViewById<LinearLayout>(R.id.layoutAdminActions)
        val btnEditPhone = findViewById<Button>(R.id.btnEditPhone)
        val btnDeletePhone = findViewById<Button>(R.id.btnDeletePhone)

        if (SessionManager.isUserAdmin(this)) {
            layoutAdminActions.visibility = View.VISIBLE
        }

        // 수량 조절 UI 연결
        val btnDecrease = findViewById<ImageButton>(R.id.btnDetailDecrease)
        val btnIncrease = findViewById<ImageButton>(R.id.btnDetailIncrease)
        val tvQuantity = findViewById<TextView>(R.id.tvDetailQuantity)

        btnDecrease.setOnClickListener {
            if (currentQuantity > 1) {
                currentQuantity--
                tvQuantity.text = currentQuantity.toString()
            }
        }

        btnIncrease.setOnClickListener {
            currentQuantity++
            tvQuantity.text = currentQuantity.toString()
        }

        // 어댑터 설정
        reviewAdapter = ReviewAdapter(emptyList()) { reviewId -> deleteReviewFromServer(reviewId) }
        rvReviewList.adapter = reviewAdapter

        commentAdapter = CommentAdapter(
            commentList = emptyList(),
            onUpdateClick = { comment -> showEditCommentDialog(comment) },
            onDeleteClick = { commentId -> deleteCommentFromServer(commentId) }
        )
        rvCommentList.adapter = commentAdapter

        currentPhone = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("phone", Phone::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("phone") as? Phone
        }

        currentPhone?.let { phone ->
            updateUI(phone)
            loadInitialData(phone.id)

            btnAddToCart.setOnClickListener {
                if (SessionManager.isGuest(this)) {
                    showLoginRequiredDialog()
                } else {
                    addToCartMultipleTimes(phone.id, currentQuantity)
                }
            }

            btnSubmitComment.setOnClickListener {
                if (SessionManager.isGuest(this)) {
                    showLoginRequiredDialog()
                    return@setOnClickListener
                }
                val content = etCommentContent.text.toString().trim()
                if (content.isEmpty()) return@setOnClickListener
                val request = CommentsCreateRequest(phoneId = phone.id, content = content)
                api.createComment(request).enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@ProductDetailActivity, "댓글 등록 완료", Toast.LENGTH_SHORT).show()
                            etCommentContent.text.clear()
                            loadCommentsFromServer(phone.id)
                        }
                    }
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {}
                })
            }

            btnSubmitReview.setOnClickListener {
                if (SessionManager.isGuest(this)) {
                    showLoginRequiredDialog()
                    return@setOnClickListener
                }
                val rating = rbInputRating.rating.toDouble()
                val content = etReviewContent.text.toString().trim()
                if (content.isEmpty()) return@setOnClickListener
                val request = ReviewCreateRequest(phoneId = phone.id, rating = rating, content = content)
                api.createReview(request).enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@ProductDetailActivity, "리뷰 등록 완료", Toast.LENGTH_SHORT).show()
                            etReviewContent.text.clear()
                            loadAverageRatingFromServer(phone.id)
                            loadReviewsFromServer(phone.id)
                        }
                    }
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {}
                })
            }

            btnFavorite.setOnClickListener {
                if (SessionManager.isGuest(this)) {
                    showLoginRequiredDialog()
                } else {
                    if (!isFavorite) addFavorite(phone.id) else removeFavorite(phone.id)
                }
            }

            btnEditPhone.setOnClickListener {
                val intent = Intent(this, PhoneCreateActivity::class.java)
                intent.putExtra("phone_to_edit", phone)
                editPhoneLauncher.launch(intent)
            }

            btnDeletePhone.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("상품 삭제")
                    .setMessage("정말로 이 상품을 삭제하시겠습니까?")
                    .setPositiveButton("삭제") { _, _ -> deletePhoneFromServer(phone.id) }
                    .setNegativeButton("취소", null)
                    .show()
            }
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

    private fun reloadPhoneDetail(phoneId: Long) {
        api.getPhoneDetail(phoneId).enqueue(object : Callback<Phone> {
            override fun onResponse(call: Call<Phone>, response: Response<Phone>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        currentPhone = it
                        updateUI(it)
                    }
                }
            }
            override fun onFailure(call: Call<Phone>, t: Throwable) {}
        })
    }

    private fun deletePhoneFromServer(phoneId: Long) {
        api.deletePhone(phoneId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProductDetailActivity, "상품이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@ProductDetailActivity, "삭제 실패 (${response.code()})", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@ProductDetailActivity, "네트워크 오류", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addToCartMultipleTimes(phoneId: Long, count: Int) {
        if (count <= 0) {
            Toast.makeText(this, "장바구니에 담겼습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val request = CartItemAddRequest(phoneId = phoneId)
        api.addToCart(request).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    addToCartMultipleTimes(phoneId, count - 1)
                } else {
                    Toast.makeText(this@ProductDetailActivity, "장바구니 담기 실패", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@ProductDetailActivity, "네트워크 오류", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showEditCommentDialog(comment: CommentResponse) {
        val editText = EditText(this).apply { setText(comment.content) }
        AlertDialog.Builder(this)
            .setTitle("댓글 수정")
            .setView(editText)
            .setPositiveButton("수정") { _, _ ->
                val newContent = editText.text.toString().trim()
                if (newContent.isNotEmpty()) {
                    updateCommentOnServer(comment.commentId, newContent)
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun updateCommentOnServer(commentId: Long, content: String) {
        api.updateComment(commentId, CommentUpdateRequest(content)).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProductDetailActivity, "댓글이 수정되었습니다.", Toast.LENGTH_SHORT).show()
                    currentPhone?.id?.let { loadCommentsFromServer(it) }
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {}
        })
    }

    private fun deleteCommentFromServer(commentId: Long) {
        api.deleteComment(commentId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProductDetailActivity, "댓글이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    currentPhone?.id?.let { loadCommentsFromServer(it) }
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {}
        })
    }

    private fun loadInitialData(phoneId: Long) {
        loadAverageRatingFromServer(phoneId)
        loadReviewsFromServer(phoneId)
        loadCommentsFromServer(phoneId)
        if (!SessionManager.isGuest(this)) {
            checkIfFavorite(phoneId)
        }
    }

    private fun loadCommentsFromServer(phoneId: Long) {
        api.getPhoneComments(phoneId).enqueue(object : Callback<List<CommentResponse>> {
            override fun onResponse(call: Call<List<CommentResponse>>, response: Response<List<CommentResponse>>) {
                if (response.isSuccessful) {
                    val comments = response.body() ?: emptyList()
                    commentAdapter.updateComments(comments)
                    tvEmptyComments.visibility = if (comments.isEmpty()) View.VISIBLE else View.GONE
                }
            }
            override fun onFailure(call: Call<List<CommentResponse>>, t: Throwable) {}
        })
    }

    private fun loadReviewsFromServer(phoneId: Long) {
        api.getPhoneReviews(phoneId).enqueue(object : Callback<List<PhoneReviewResponse>> {
            override fun onResponse(call: Call<List<PhoneReviewResponse>>, response: Response<List<PhoneReviewResponse>>) {
                if (response.isSuccessful) {
                    val reviews = response.body() ?: emptyList()
                    reviewAdapter.updateReviews(reviews)
                    tvDetailReviewCount.text = "(${reviews.size}개의 리뷰)"
                    tvEmptyReviews.visibility = if (reviews.isEmpty()) View.VISIBLE else View.GONE
                }
            }
            override fun onFailure(call: Call<List<PhoneReviewResponse>>, t: Throwable) {}
        })
    }

    private fun loadAverageRatingFromServer(phoneId: Long) {
        api.getAverageRating(phoneId).enqueue(object : Callback<PhoneRatingResponse> {
            override fun onResponse(call: Call<PhoneRatingResponse>, response: Response<PhoneRatingResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        rbDetailRating.rating = it.averageRating.toFloat()
                        tvDetailRatingValue.text = String.format("%.1f", it.averageRating)
                    }
                }
            }
            override fun onFailure(call: Call<PhoneRatingResponse>, t: Throwable) {}
        })
    }

    private fun deleteReviewFromServer(reviewId: Long) {
        api.deleteReview(reviewId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProductDetailActivity, "리뷰 삭제 완료", Toast.LENGTH_SHORT).show()
                    currentPhone?.let { loadInitialData(it.id) }
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {}
        })
    }

    private fun checkIfFavorite(phoneId: Long) {
        api.getFavorites().enqueue(object : Callback<List<FavoritePhoneResponse>> {
            override fun onResponse(call: Call<List<FavoritePhoneResponse>>, response: Response<List<FavoritePhoneResponse>>) {
                if (response.isSuccessful) {
                    isFavorite = (response.body() ?: emptyList()).any { it.phoneId == phoneId }
                    btnFavorite.setImageResource(if (isFavorite) R.drawable.ic_interest else R.drawable.ic_favorite_border)
                }
            }
            override fun onFailure(call: Call<List<FavoritePhoneResponse>>, t: Throwable) {}
        })
    }

    private fun addFavorite(phoneId: Long) {
        api.addFavorite(phoneId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) { isFavorite = true; btnFavorite.setImageResource(R.drawable.ic_interest) }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {}
        })
    }

    private fun removeFavorite(phoneId: Long) {
        api.deleteFavorite(phoneId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) { isFavorite = false; btnFavorite.setImageResource(R.drawable.ic_favorite_border) }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {}
        })
    }

    private fun updateUI(phone: Phone) {
        findViewById<ImageView>(R.id.ivDetailPhoneImage).load(phone.imageResId ?: phone.imageUrl) {
            placeholder(R.drawable.ic_launcher_background)
            error(R.drawable.ic_launcher_background)
        }
        findViewById<TextView>(R.id.tvDetailPhoneBrand).text = phone.brand
        findViewById<TextView>(R.id.tvDetailPhoneName).text = phone.name
        findViewById<TextView>(R.id.tvDetailPhonePrice).text = "${NumberFormat.getNumberInstance(Locale.KOREA).format(phone.price)}원"
        findViewById<TextView>(R.id.tvDetailPhoneDescription).text = phone.description ?: "상품 설명이 없습니다."
    }
}
