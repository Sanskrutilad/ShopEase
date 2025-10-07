package com.example.shopease.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.shopease.components.BottomBar
import com.example.shopease.components.TopBar
import com.example.shopease.viewmodels.CartViewModel
import com.example.shopease.viewmodels.CheckoutViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CartScreen(
    navController: NavController,
    viewModel: CartViewModel,
    checkoutViewModel: CheckoutViewModel
) {
    val cartItems = viewModel.cartItems
    val total = cartItems.sumOf { it.price * it.quantity }
    var isSearching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val products = if (isSearching) {
        cartItems.filter {
            it.name.contains(searchQuery, ignoreCase = true)
        }
    } else {
        cartItems
    }

    Scaffold(
        topBar = {
            TopBar(
                navController,
                isSearching = isSearching,
                searchQuery = searchQuery,
                onSearchChange = { searchQuery = it },
                onSearchClick = { isSearching = !isSearching }
            )
        },
        bottomBar = { BottomBar(navController) },
        containerColor = Color(0xFFCDEFF5)
    ) {
        Column(
            modifier = Modifier
                .padding(top = 70.dp, start = 20.dp, end = 20.dp, bottom = 24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFFFEC8D8), Color(0xFFFFE6F0))
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(vertical = 30.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🛒 Your Cart",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFEC407A)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (products.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Your cart is empty 🛍️",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                }
            } else {
                products.forEach { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .shadow(4.dp, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(item.imageUrl),
                                contentDescription = item.name,
                                modifier = Modifier
                                    .size(80.dp)
                                    .shadow(2.dp, RoundedCornerShape(10.dp))
                            )

                            Spacer(Modifier.width(12.dp))

                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = item.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = Color(0xFF444444)
                                )
                                Text(
                                    text = "₹${item.price}",
                                    color = Color(0xFF888888),
                                    fontSize = 15.sp
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 6.dp)
                                ) {
                                    IconButton(onClick = { viewModel.decreaseQuantity(item) }) {
                                        Text("➖", fontSize = 18.sp)
                                    }
                                    Text("${item.quantity}", fontSize = 16.sp)
                                    IconButton(onClick = { viewModel.increaseQuantity(item) }) {
                                        Text("➕", fontSize = 18.sp)
                                    }
                                }
                            }

                            IconButton(onClick = { viewModel.removeFromCart(item) }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Remove",
                                    tint = Color(0xFFE57373)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Total & Checkout Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(6.dp, RoundedCornerShape(18.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFE6F0)
                    ),
                    shape = RoundedCornerShape(18.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .padding(18.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            "Total: ₹$total",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFEC407A)
                        )

                        Spacer(Modifier.height(12.dp))

                        Button(
                            onClick = {
                                val items = viewModel.cartItems.toList()
                                val totalAmount =
                                    items.sumOf { it.price.toDouble() * it.quantity }

                                checkoutViewModel.cartOrderedItems = items
                                checkoutViewModel.originalAmount = totalAmount
                                checkoutViewModel.totalAmount = totalAmount

                                navController.navigate("shipping_info")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEC8D8))
                        ) {
                            Text(
                                "Proceed to Checkout",
                                color = Color(0xFFEC407A),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}
