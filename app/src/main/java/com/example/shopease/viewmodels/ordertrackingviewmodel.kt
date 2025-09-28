package com.example.shopease.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrderTrackingViewModel : ViewModel() {
    private val _orderData = MutableStateFlow<Map<String, Any>?>(null)
    val orderData: StateFlow<Map<String, Any>?> = _orderData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val database = FirebaseDatabase.getInstance().reference
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    fun fetchOrderDetails(orderId: String) {
        if (userId.isEmpty()) {
            _error.value = "User not authenticated"
            return
        }

        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val orderRef = database.child("orders").child(userId).child(orderId)

                orderRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        _isLoading.value = false

                        if (snapshot.exists()) {
                            val orderMap = snapshot.value as? Map<String, Any>
                            orderMap?.let {
                                _orderData.value = it + ("orderId" to orderId)
                            } ?: run {
                                _error.value = "Invalid order data"
                            }
                        } else {
                            _error.value = "Order not found"
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _isLoading.value = false
                        _error.value = "Failed to fetch order: ${error.message}"
                    }
                })
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = "Error: ${e.message}"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}