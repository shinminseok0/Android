package com.example.shintech

import java.io.Serializable

// 가짜 장바구니 아이템 데이터 클래스
data class FakeCartItem(
    val phone: Phone,
    var quantity: Int = 1
) : Serializable

object CartManager {
    private val cartItems = mutableListOf<FakeCartItem>()

    fun addToCart(phone: Phone) {
        val existingItem = cartItems.find { it.phone.id == phone.id }
        if (existingItem != null) {
            existingItem.quantity++
        } else {
            cartItems.add(FakeCartItem(phone = phone))
        }
    }

    fun decreaseCartItem(phone: Phone) {
        val existingItem = cartItems.find { it.phone.id == phone.id }
        if (existingItem != null) {
            if (existingItem.quantity > 1) {
                existingItem.quantity--
            } else {
                cartItems.remove(existingItem)
            }
        }
    }

    fun getCartItems(): List<FakeCartItem> {
        return cartItems.toList()
    }

    fun clearCart() {
        cartItems.clear()
    }
}
