package com.example.shintech

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import java.text.NumberFormat
import java.util.Locale

class OrderHistoryAdapter(private var orders: List<OrderHistoryResponse>) :
    RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder>() {

    fun updateOrders(newOrders: List<OrderHistoryResponse>) {
        orders = newOrders
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_history, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int = orders.size

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvOrderDate: TextView = itemView.findViewById(R.id.tvOrderDate)
        private val tvOrderStatus: TextView = itemView.findViewById(R.id.tvOrderStatus)
        private val tvOrderId: TextView = itemView.findViewById(R.id.tvOrderId)
        private val llProductContainer: LinearLayout = itemView.findViewById(R.id.llProductContainer)
        private val tvTotalFinalPrice: TextView = itemView.findViewById(R.id.tvTotalFinalPrice)

        fun bind(order: OrderHistoryResponse) {
            // 날짜 포맷팅 (ISO-8601 형식: 2023-10-27T10:00:00 -> 2023.10.27)
            val dateStr = order.createdAt.split("T").getOrNull(0)?.replace("-", ".") ?: order.createdAt
            tvOrderDate.text = dateStr
            tvOrderStatus.text = order.status
            tvOrderId.text = "주문번호 ${order.orderId}"
            tvTotalFinalPrice.text = "${NumberFormat.getNumberInstance(Locale.KOREA).format(order.finalPrice)}원"

            // 상품 목록 동적 생성
            llProductContainer.removeAllViews()
            order.items.forEach { item ->
                val productView = LayoutInflater.from(itemView.context)
                    .inflate(R.layout.item_order_product, llProductContainer, false)

                val ivProductImage = productView.findViewById<ImageView>(R.id.ivProductImage)
                val tvProductName = productView.findViewById<TextView>(R.id.tvProductName)
                val tvProductInfo = productView.findViewById<TextView>(R.id.tvProductInfo)

                tvProductName.text = item.name
                tvProductInfo.text = "${NumberFormat.getNumberInstance(Locale.KOREA).format(item.price)}원 | ${item.quantity}개"

                ivProductImage.load(item.imageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.bg_card)
                    error(R.drawable.bg_card)
                }

                llProductContainer.addView(productView)
            }
        }
    }
}
