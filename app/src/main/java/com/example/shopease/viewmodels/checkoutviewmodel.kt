package com.example.shopease.viewmodels

import com.example.shopease.CartItem
import com.example.shopease.Coupon
import com.example.shopease.Product
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase

class CheckoutViewModel : ViewModel() {
    var name by mutableStateOf("")
    var address by mutableStateOf("")
    var city by mutableStateOf("")
    var state by mutableStateOf("")
    var pincode by mutableStateOf("")
    var phone by mutableStateOf("")
    var originalAmount by mutableStateOf(0.0)

    var totalAmount by mutableStateOf(0.0)
    var orderedItems: List<Product> = listOf()
    var cartOrderedItems: List<CartItem> = listOf()
    var selectedCoupon: Coupon? = null
    var hasVisitedPaymentScreen by mutableStateOf(false)


    fun fetchLastOrderDetails(userId: String) {
        val db = FirebaseDatabase.getInstance().getReference("orders").child(userId)

        db.limitToLast(1).get().addOnSuccessListener { snapshot ->
            val lastOrder = snapshot.children.lastOrNull()
            lastOrder?.let {
                name = it.child("name").value as? String ?: ""
                address = it.child("address").value as? String ?: ""
                city = it.child("city").value as? String ?: ""
                state = it.child("state").value as? String ?: ""
                pincode = it.child("pincode").value as? String ?: ""
                phone = it.child("phone").value as? String ?: ""
            }
        }.addOnFailureListener {
            Log.e("CheckoutViewModel", "Failed to fetch last order: ${it.message}")
        }
    }


}
