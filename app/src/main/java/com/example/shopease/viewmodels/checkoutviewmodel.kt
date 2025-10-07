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
    // Keep a copy of the last fetched address
    private var lastSavedName = ""
    private var lastSavedAddress = ""
    private var lastSavedCity = ""
    private var lastSavedState = ""
    private var lastSavedPincode = ""
    private var lastSavedPhone = ""

    // Call this after fetching user details
    fun saveLastFetchedAddress() {
        lastSavedName = name
        lastSavedAddress = address
        lastSavedCity = city
        lastSavedState = state
        lastSavedPincode = pincode
        lastSavedPhone = phone
    }

    // Check if user changed the address
    fun hasAddressChanged(): Boolean {
        return name != lastSavedName ||
                address != lastSavedAddress ||
                city != lastSavedCity ||
                state != lastSavedState ||
                pincode != lastSavedPincode ||
                phone != lastSavedPhone
    }

    // Update user address in Firebase
    fun updateUserAddressInFirebase(uid: String) {
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(uid)
        val updatedData = mapOf(
            "name" to name,
            "address" to address,
            "city" to city,
            "state" to state,
            "pincode" to pincode,
            "phone" to phone
        )
        userRef.updateChildren(updatedData).addOnSuccessListener {
            // Save new address as lastSaved
            saveLastFetchedAddress()
        }
    }


}
