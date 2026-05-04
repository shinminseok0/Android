package com.example.shintech

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommentListActivity : AppCompatActivity() {

    private val api = ApiClient.authApiService
    private lateinit var commentAdapter: CommentAdapter
    private var currentPhoneId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment_list)

        currentPhoneId = intent.getLongExtra("phoneId", -1)
        val phoneName = intent.getStringExtra("phoneName") ?: "핸드폰"

        findViewById<TextView>(R.id.tvCommentListTitle).text = "${phoneName} 전체 댓글"

        val rvCommentList = findViewById<RecyclerView>(R.id.rvCommentList)
        
        // 1. 어댑터 생성 시 수정 및 삭제 버튼 클릭 처리 콜백을 전달합니다 (오류 해결 핵심)
        commentAdapter = CommentAdapter(
            commentList = emptyList(),
            onUpdateClick = { comment -> showEditCommentDialog(comment) },
            onDeleteClick = { commentId -> deleteCommentFromServer(commentId) }
        )
        rvCommentList.adapter = commentAdapter

        if (currentPhoneId != -1L) {
            loadCommentsFromServer(currentPhoneId)
        }
    }

    // 댓글 수정 다이얼로그
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
                    Toast.makeText(this@CommentListActivity, "댓글이 수정되었습니다.", Toast.LENGTH_SHORT).show()
                    if (currentPhoneId != -1L) loadCommentsFromServer(currentPhoneId)
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {}
        })
    }

    private fun deleteCommentFromServer(commentId: Long) {
        api.deleteComment(commentId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CommentListActivity, "댓글이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    if (currentPhoneId != -1L) loadCommentsFromServer(currentPhoneId)
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {}
        })
    }

    private fun loadCommentsFromServer(phoneId: Long) {
        api.getPhoneComments(phoneId).enqueue(object : Callback<List<CommentResponse>> {
            override fun onResponse(call: Call<List<CommentResponse>>, response: Response<List<CommentResponse>>) {
                if (response.isSuccessful) {
                    val comments = response.body() ?: emptyList()
                    commentAdapter.updateComments(comments)
                }
            }
            override fun onFailure(call: Call<List<CommentResponse>>, t: Throwable) {
                Log.e("CommentList_Error", "API 호출 실패", t)
            }
        })
    }
}
