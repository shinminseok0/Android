package com.example.shintech

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CommentAdapter(
    private var commentList: List<CommentResponse>,
    private val onUpdateClick: (CommentResponse) -> Unit,
    private val onDeleteClick: (Long) -> Unit
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(commentList[position], onUpdateClick, onDeleteClick)
    }

    override fun getItemCount(): Int = commentList.size

    fun updateComments(newComments: List<CommentResponse>) {
        commentList = newComments
        notifyDataSetChanged()
    }

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvUserName: TextView = itemView.findViewById(R.id.tvCommentUserName)
        private val tvContent: TextView = itemView.findViewById(R.id.tvCommentContent)
        private val tvEdit: TextView = itemView.findViewById(R.id.tvEditComment)
        private val tvDelete: TextView = itemView.findViewById(R.id.tvDeleteComment)

        fun bind(
            comment: CommentResponse,
            onUpdateClick: (CommentResponse) -> Unit,
            onDeleteClick: (Long) -> Unit
        ) {
            tvUserName.text = comment.userName
            tvContent.text = comment.content

            // 본인 확인 로직이 필요하지만, 여기서는 UI 연결에 집중
            tvEdit.setOnClickListener { onUpdateClick(comment) }
            tvDelete.setOnClickListener { onDeleteClick(comment.commentId) }
        }
    }
}
