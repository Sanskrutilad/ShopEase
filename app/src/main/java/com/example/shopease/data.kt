package com.example.shopease

data class Product(
    val productId: String = "",
    val category : String="",
    val description: String = "",
    val imageUrls: List<String> = emptyList(),
    val name: String = "",
    val price: String = "",
    val ebookUrl: String? = null
)

data class Coupon(
    val id: String = "",
    val code: String = "",
    val discount: String = "",
    val active: Boolean = false
)
data class CartItem(
    val name: String = "",
    val price: Double = 0.0,
    val quantity: Int = 1,
    val imageUrl: String = "",
    val ebookUrl: String? = null
)