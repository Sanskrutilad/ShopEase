package com.example.shopease.screens.checkout

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.shopease.CartItem
import com.example.shopease.Product
import com.example.shopease.components.BottomBar
import com.example.shopease.components.TopBar
import com.example.shopease.viewmodels.CheckoutViewModel

@Composable
fun OrderConfirmationScreen(
    navController: NavController,
    checkoutViewModel: CheckoutViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            TopBar(navController, false, "", {}, {})
        },
        bottomBar = {
            BottomBar(navController)
        },containerColor = Color(0xFFCDEFF5)
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🎉 Thank You!", fontSize = 28.sp, fontWeight = FontWeight.Bold, color =Color(0xFFEC407A))

            Spacer(modifier = Modifier.height(24.dp))
            Text("Your order has been placed successfully.",color = Color(0xFFEC407A))
            Spacer(modifier = Modifier.height(16.dp))

            Text("Name: ${checkoutViewModel.name}")
            Text("Address: ${checkoutViewModel.address} ,${checkoutViewModel.city},${checkoutViewModel.state},${checkoutViewModel.pincode} ")
            Text("Phone: ${checkoutViewModel.phone}")
            Text("Total Amount: ₹${checkoutViewModel.totalAmount}")

            Spacer(modifier = Modifier.height(24.dp))
            Text("Ordered Items:", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Show Product (Buy Now)
                if (checkoutViewModel.orderedItems.isNotEmpty()) {
                    items(checkoutViewModel.orderedItems.size) { index ->
                        val product = checkoutViewModel.orderedItems[index]
                        OrderProductItem(product, navController)
                    }
                }
                // Show Cart Items
                else if (checkoutViewModel.cartOrderedItems.isNotEmpty()) {
                    items(checkoutViewModel.cartOrderedItems.size) { index ->
                        val cartItem = checkoutViewModel.cartOrderedItems[index]
                        OrderCartItem(cartItem, navController)
                    }
                }
            }



            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    checkoutViewModel.name = ""
                    checkoutViewModel.address = ""
                    checkoutViewModel.phone = ""
                    checkoutViewModel.totalAmount = 0.0
                    checkoutViewModel.orderedItems = emptyList()
                    checkoutViewModel.cartOrderedItems = emptyList()
                    // Navigate back to home
                    navController.navigate("homescreen") {
                        popUpTo("order_confirmation") { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(Color(0xFFFEC8D8))
            ) {
                Text("Back to Home",  fontSize = 18.sp,color =Color(0xFFEC407A))
            }

        }
    }
}
@Composable
fun OrderCartItem(cartItem: CartItem, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(cartItem.imageUrl),
                contentDescription = cartItem.name,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(cartItem.name, fontWeight = FontWeight.Bold)
                Text("₹${cartItem.price} x ${cartItem.quantity} = ₹${cartItem.price * cartItem.quantity}")
                if (!cartItem.ebookUrl.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    TextButton(onClick = {
                        val intent = android.content.Intent(
                            android.content.Intent.ACTION_VIEW,
                            android.net.Uri.parse(cartItem.ebookUrl)
                        )
                        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                        navController.context.startActivity(intent)
                    }) {
                        Text("📘 Open eBook", color = Color(0xFF3F51B5))
                    }
                }
            }
        }
    }
}

@Composable
fun OrderProductItem(product: Product, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8E8E1))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(product.imageUrls.firstOrNull() ?: ""),
                contentDescription = product.name,
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(product.name, fontWeight = FontWeight.Bold)
                Text("₹${product.price}")
                if (!product.ebookUrl.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    TextButton(onClick = {
                        val intent = android.content.Intent(
                            android.content.Intent.ACTION_VIEW,
                            android.net.Uri.parse(product.ebookUrl)
                        )
                        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                        navController.context.startActivity(intent)
                    }) {
                        Text("📘 Open eBook", color = Color(0xFF4A7C59))
                    }
                }
            }
        }
    }
}
