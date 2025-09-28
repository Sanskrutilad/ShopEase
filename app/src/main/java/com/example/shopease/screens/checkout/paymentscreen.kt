package com.example.shopease.screens.checkout


import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shopease.Coupon
import com.example.shopease.OrderSession
import com.example.shopease.components.BottomBar
import com.example.shopease.components.TopBar
import com.example.shopease.viewmodels.CheckoutViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.razorpay.Checkout
import org.json.JSONObject

@Composable
fun PaymentOptionsScreen(
    navController: NavController,
    viewModel: CheckoutViewModel
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var showCouponDialog by remember { mutableStateOf(false) }
    var availableCoupons by remember { mutableStateOf<List<Coupon>>(emptyList()) }

    LaunchedEffect(Unit) {
        // Always reset coupon and totalAmount to originalAmount
        viewModel.selectedCoupon = null
        viewModel.totalAmount = viewModel.originalAmount
    }

    //Log.d("checkout", "originalAmount: ${viewModel.originalAmount}, totalAmount: ${viewModel.totalAmount}")


    Scaffold(
        topBar = {
            TopBar(
                navController,
                isSearching = false,
                searchQuery = "",
                onSearchClick = {},
                onSearchChange = {}
            )
        },
        bottomBar = { BottomBar(navController) },containerColor = Color(0xFFCDEFF5)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("💳 Payment", fontSize = 22.sp,color = Color(0xFFEC407A))
            Spacer(modifier = Modifier.height(24.dp))
            Text("Please confirm your payment.")
            Spacer(modifier = Modifier.height(16.dp))

            // Coupon Button
            Button(
                onClick = {
                    FirebaseDatabase.getInstance().reference.child("coupons")
                        .get().addOnSuccessListener { snapshot ->
                            availableCoupons = snapshot.children.mapNotNull {
                                it.getValue(Coupon::class.java)
                            }.filter { it.active }
                            showCouponDialog = true
                        }
                }
                ,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEC8D8))
            ) {
                Text("🎟️ Apply Coupon",fontSize = 18.sp,color =Color(0xFFEC407A))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Payment Button
            Button(
                onClick = {
                    if (activity != null) {
                        OrderSession.checkoutViewModel = viewModel
                        OrderSession.context = context
                        OrderSession.navController = navController

                        startRazorpayCheckout(
                            context = context,
                            activity = activity,
                            amount = viewModel.totalAmount,
                            userName = viewModel.name,
                            userEmail = FirebaseAuth.getInstance().currentUser?.email.orEmpty(),
                            userPhone = viewModel.phone
                        )
                    } else {
                        Toast.makeText(context, "Context error", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(Color(0xFFFEC8D8))
            ) {
                Text("Pay ₹${viewModel.totalAmount}", color = Color(0xFFEC407A), fontSize = 18.sp)
            }
        }

        if (showCouponDialog) {
            AlertDialog(
                onDismissRequest = { showCouponDialog = false },
                title = { Text("Select a Coupon") },
                text = {
                    if (availableCoupons.isEmpty()) {
                        Text("No active coupons available.")
                    } else {
                        Column {
                            availableCoupons.forEach { coupon ->
                                Text(
                                    text = "${coupon.code} - ${coupon.discount.toDouble()}%",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.selectedCoupon = coupon
                                            val discounted = viewModel.originalAmount * (1 - coupon.discount.toDouble() / 100)
                                            viewModel.totalAmount = discounted
                                            showCouponDialog = false
                                            Toast.makeText(context, "Coupon Applied!", Toast.LENGTH_SHORT).show()
                                        }
                                        .padding(vertical = 8.dp)
                                )
                            }

                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showCouponDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}

fun startRazorpayCheckout(
    context: Context,
    activity: Activity,
    amount: Double,
    userName: String,
    userEmail: String,
    userPhone: String
) {
    val checkout = Checkout()
    checkout.setKeyID("rzp_test_RMsHiDScug3dt6")
    try {
        val options = JSONObject().apply {
            put("name", "Baby Dino")
            put("description", "Payment for order")
            put("theme.color", "#D3AB9E")
            put("currency", "INR")
            put("amount", (amount * 100).toInt()) // in paise
            put("prefill", JSONObject().apply {
                put("email", userEmail)
                put("contact", userPhone)
                put("name", userName)
            })
        }

        checkout.open(activity, options)

    } catch (e: Exception) {
        Toast.makeText(context, "Error in payment: ${e.message}", Toast.LENGTH_SHORT).show()
        e.printStackTrace()
    }
}
