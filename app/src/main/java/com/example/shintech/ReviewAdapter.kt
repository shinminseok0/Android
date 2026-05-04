package com.example.shintech

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReviewAdapter(
    private var reviewList: List<PhoneReviewResponse>,
    private val onDeleteClick: (Long) -> Unit // 삭제 버튼 클릭 콜백 추가
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(reviewList[position], onDeleteClick)
    }

    override fun getItemCount(): Int = reviewList.size

    fun updateReviews(newReviews: List<PhoneReviewResponse>) {
        reviewList = newReviews
        notifyDataSetChanged()
    }

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvUserName: TextView = itemView.findViewById(R.id.tvReviewUserName)
        private val tvDate: TextView = itemView.findViewById(R.id.tvReviewDate)
        private val rbRating: RatingBar = itemView.findViewById(R.id.rbReviewRating)
        private val tvContent: TextView = itemView.findViewById(R.id.tvReviewContent)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteReview)

        fun bind(review: PhoneReviewResponse, onDeleteClick: (Long) -> Unit) {
            tvUserName.text = review.userName
            tvDate.text = review.createdAt.split("T").firstOrNull() ?: review.createdAt
            rbRating.rating = review.rating.toFloat()
            tvContent.text = review.content

            // TODO: 실제로는 현재 로그인한 유저 ID와 비교해야 함
            // 지금은 테스트를 위해 모든 리뷰에 삭제 버튼을 표시하거나, 로직을 추가할 수 있습니다.
            btnDelete.visibility = View.VISIBLE 
            
            btnDelete.setOnClickListener {
                onDeleteClick(review.reviewId)
            }
        }
    }
}
