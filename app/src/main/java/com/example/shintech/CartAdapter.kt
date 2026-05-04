package com.example.shintech

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import java.text.NumberFormat
import java.util.Locale

class CartAdapter(
    private var cartItems: List<CartItem>,
    private val onIncrease: (CartItem) -> Unit,
    private val onDecrease: (CartItem) -> Unit,
    private val onDelete: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view, onIncrease, onDecrease, onDelete)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(cartItems[position])
    }

    override fun getItemCount(): Int = cartItems.size

    fun updateItems(newItems: List<CartItem>) {
        cartItems = newItems
        notifyDataSetChanged()
    }

    class CartViewHolder(
        itemView: View,
        private val onIncrease: (CartItem) -> Unit,
        private val onDecrease: (CartItem) -> Unit,
        private val onDelete: (CartItem) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val ivCartItemImage: ImageView = itemView.findViewById(R.id.ivCartItemImage)
        private val tvCartItemName: TextView = itemView.findViewById(R.id.tvCartItemName)
        private val tvCartItemPrice: TextView = itemView.findViewById(R.id.tvCartItemPrice)
        private val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        private val btnIncrease: ImageButton = itemView.findViewById(R.id.btnIncreaseQuantity)
        private val btnDecrease: ImageButton = itemView.findViewById(R.id.btnDecreaseQuantity)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteCartItem)

        fun bind(cartItem: CartItem) {
            val fullImageUrl = if (cartItem.imageUrl?.startsWith("/") == true) {
                "http://10.0.2.2:8080${cartItem.imageUrl}"
            } else {
                cartItem.imageUrl
            }

            ivCartItemImage.load(fullImageUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_background)
                error(R.drawable.ic_launcher_background)
            }

            tvCartItemName.text = cartItem.name
            tvCartItemPrice.text = "${NumberFormat.getNumberInstance(Locale.KOREA).format(cartItem.price)}원"
            tvQuantity.text = cartItem.quantity.toString()

            btnIncrease.setOnClickListener { onIncrease(cartItem) }
            btnDecrease.setOnClickListener { onDecrease(cartItem) }
            btnDelete.setOnClickListener { onDelete(cartItem) }
        }
    }
}
