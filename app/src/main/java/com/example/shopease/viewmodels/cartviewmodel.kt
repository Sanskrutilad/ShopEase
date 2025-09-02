package com.example.shopease.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.shopease.CartItem
import com.example.shopease.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartViewModel : ViewModel() {
    private val _cartItems = mutableStateListOf<CartItem>()
    val cartItems: List<CartItem> = _cartItems

    private var dbRef = FirebaseDatabase.getInstance().getReference("carts")

    init {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            dbRef = dbRef.child(userId)
            fetchCartFromFirebase()
        } else {
            _cartItems.clear() // no user = no cart
        }
    }

    private fun fetchCartFromFirebase() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _cartItems.clear()
                for (child in snapshot.children) {
                    val item = child.getValue(CartItem::class.java)
                    if (item != null) {
                        _cartItems.add(item)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun saveCartToFirebase() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            dbRef.setValue(_cartItems)
        }
    }

    fun addProductToCart(product: Product) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val existing = _cartItems.find { it.name == product.name }
        if (existing != null) {
            val updated = existing.copy(quantity = existing.quantity + 1)
            _cartItems[_cartItems.indexOf(existing)] = updated
        } else {
            val newItem = CartItem(
                name = product.name,
                price = product.price.toDoubleOrNull() ?: 0.0,
                quantity = 1,
                imageUrl = product.imageUrls.firstOrNull() ?: ""

            )
            _cartItems.add(newItem)
        }
        saveCartToFirebase()
    }

    fun removeFromCart(item: CartItem) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        _cartItems.remove(item)
        saveCartToFirebase()
    }

    fun increaseQuantity(item: CartItem) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val index = _cartItems.indexOfFirst { it.name == item.name }
        if (index != -1) {
            _cartItems[index] = _cartItems[index].copy(quantity = _cartItems[index].quantity + 1)
            saveCartToFirebase()
        }
    }

    fun decreaseQuantity(item: CartItem) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val index = _cartItems.indexOfFirst { it.name == item.name }
        if (index != -1 && _cartItems[index].quantity > 1) {
            _cartItems[index] = _cartItems[index].copy(quantity = _cartItems[index].quantity - 1)
            saveCartToFirebase()
        } else if (index != -1) {
            removeFromCart(item)
        }
    }
}
