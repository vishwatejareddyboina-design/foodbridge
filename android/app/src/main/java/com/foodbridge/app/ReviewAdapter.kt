package com.foodbridge.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class ReviewItem(
    val userName: String,
    val rating: Double,
    val comment: String,
    val isHygienic: Boolean?
)

class ReviewAdapter(private val reviews: List<ReviewItem>) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvReviewerName: TextView = view.findViewById(R.id.tvReviewerName)
        val tvRating: TextView = view.findViewById(R.id.tvRating)
        val tvComment: TextView = view.findViewById(R.id.tvComment)
        val tvHygiene: TextView = view.findViewById(R.id.tvHygiene)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.tvReviewerName.text = review.userName
        holder.tvRating.text = review.rating.toString()
        holder.tvComment.text = review.comment
        
        holder.tvComment.visibility = if (review.comment.isEmpty()) View.GONE else View.VISIBLE
        
        if (review.isHygienic == true) {
            holder.tvHygiene.visibility = View.VISIBLE
            holder.tvHygiene.text = "✓ Hygienic Preparation"
        } else if (review.isHygienic == false) {
            holder.tvHygiene.visibility = View.VISIBLE
            holder.tvHygiene.text = "✗ Not Hygienic"
            holder.tvHygiene.setTextColor(android.graphics.Color.parseColor("#ef4444")) // red
        } else {
            holder.tvHygiene.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = reviews.size
}
