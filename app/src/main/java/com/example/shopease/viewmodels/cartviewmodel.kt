package com.example.shopease.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.shopease.CartItem
import com.example.shopease.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CartViewModel : ViewModel() {
    private val _cartItems = mutableStateListOf<CartItem>()
    val cartItems: List<CartItem> get() = _cartItems

    private var dbRef: DatabaseReference? = null

    init {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            dbRef = FirebaseDatabase.getInstance().getReference("carts").child(user.uid)
            fetchCartFromFirebase()
        } else {
            _cartItems.clear() // no user = no cart
        }

        // Listen for login/logout changes in real time
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            if (auth.currentUser == null) {
                _cartItems.clear()
                dbRef = null
            } else {
                dbRef = FirebaseDatabase.getInstance().getReference("carts").child(auth.currentUser!!.uid)
                fetchCartFromFirebase()
            }
        }
    }

    private fun fetchCartFromFirebase() {
        dbRef?.addValueEventListener(object : ValueEventListener {
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
        dbRef?.setValue(_cartItems)
    }

    fun addProductToCart(product: Product) {
        val user = FirebaseAuth.getInstance().currentUser ?: return

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

        // 🔥 Immediately update UI & sync Firebase in background
        saveCartToFirebase()
    }

    fun removeFromCart(item: CartItem) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        _cartItems.remove(item)
        saveCartToFirebase()
    }

    fun increaseQuantity(item: CartItem) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val index = _cartItems.indexOfFirst { it.name == item.name }
        if (index != -1) {
            _cartItems[index] = _cartItems[index].copy(quantity = _cartItems[index].quantity + 1)
            saveCartToFirebase()
        }
    }

    fun decreaseQuantity(item: CartItem) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val index = _cartItems.indexOfFirst { it.name == item.name }
        if (index != -1) {
            val current = _cartItems[index]
            if (current.quantity > 1) {
                _cartItems[index] = current.copy(quantity = current.quantity - 1)
            } else {
                _cartItems.removeAt(index)
            }
            saveCartToFirebase()
        }
    }
}
