package com.example.shintech

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

class CouponAdapter(
    private var coupons: List<Coupon>,
    private val onApplyClick: (Coupon) -> Unit
) : RecyclerView.Adapter<CouponAdapter.CouponViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_coupon, parent, false)
        return CouponViewHolder(view, onApplyClick)
    }

    override fun onBindViewHolder(holder: CouponViewHolder, position: Int) {
        holder.bind(coupons[position])
    }

    override fun getItemCount(): Int = coupons.size

    fun updateCoupons(newCoupons: List<Coupon>) {
        coupons = newCoupons
        notifyDataSetChanged()
    }

    class CouponViewHolder(
        itemView: View,
        private val onApplyClick: (Coupon) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val tvCouponName: TextView = itemView.findViewById(R.id.tvCouponName)
        private val tvCouponInfo: TextView = itemView.findViewById(R.id.tvCouponInfo)
        private val btnApply: Button = itemView.findViewById(R.id.btnApplyCoupon)

        fun bind(coupon: Coupon) {
            tvCouponName.text = coupon.name
            val discount = NumberFormat.getNumberInstance(Locale.KOREA).format(coupon.discountAmount)
            val minOrder = NumberFormat.getNumberInstance(Locale.KOREA).format(coupon.minOrderAmount)
            tvCouponInfo.text = "${discount}원 할인 | ${minOrder}원 이상 구매 시"

            btnApply.setOnClickListener { onApplyClick(coupon) }
        }
    }
}
