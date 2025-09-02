package com.example.shopease.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.shopease.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProductViewModel : ViewModel() {

    private val _products = mutableStateListOf<Product>()
    val products: List<Product> = _products

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        val dbRef = FirebaseDatabase.getInstance().getReference("products")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _products.clear()
                for (child in snapshot.children) {
                    val product = child.getValue(Product::class.java)
                    product?.let {
                        _products.add(it)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error fetching products", error.toException())
            }
        })
    }

    fun getProductsByCategory(category: String): List<Product> {
        return products.filter { it.category.equals(category, ignoreCase = true) }
    }

    fun getProductById(productId: String): Product? {
        return _products.find { it.productId == productId }
    }

}
