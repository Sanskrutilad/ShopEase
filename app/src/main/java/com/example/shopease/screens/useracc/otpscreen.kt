package com.example.shopease.screens.useracc

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase

@Composable
fun OTPScreen(
    navController: NavController,
    verificationId: String,
    isSignup: Boolean,
    productId: String? = null // Pass this when navigating here
) {
    var otp by remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseDatabase.getInstance().getReference("users")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Enter OTP", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = otp,
            onValueChange = { otp = it },
            label = { Text("OTP") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                val credential = PhoneAuthProvider.getCredential(verificationId, otp)
                auth.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (isSignup) {
                            val userId = auth.currentUser!!.uid
                            val args = navController.previousBackStackEntry?.savedStateHandle
                            val name = args?.get<String>("name")
                            val email = args?.get<String>("email")
                            val phone = args?.get<String>("phone")

                            val userMap = mapOf(
                                "name" to name,
                                "email" to email,
                                "phone" to phone
                            )

                            db.child(userId).setValue(userMap)
                                .addOnSuccessListener {
                                    navigateAfterLogin(navController, productId)
                                }
                        } else {
                            navigateAfterLogin(navController, productId)
                        }
                    } else {
                        Toast.makeText(
                            navController.context,
                            "Invalid OTP",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Verify OTP")
        }
    }
}

private fun navigateAfterLogin(navController: NavController, productId: String?) {
    if (!productId.isNullOrEmpty()) {
        // Go back to product detail for the same product
        navController.navigate("product_detail/$productId") {
            popUpTo("login") { inclusive = true }
            popUpTo("signup") { inclusive = true }
        }
    } else {
        // Default to home screen
        navController.navigate("homescreen") {
            popUpTo("login") { inclusive = true }
            popUpTo("signup") { inclusive = true }
        }
    }
}
