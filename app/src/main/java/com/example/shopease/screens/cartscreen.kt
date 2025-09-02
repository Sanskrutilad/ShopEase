package com.example.shopease.screens

import com.example.shopease.viewmodels.CartViewModel
import com.example.shopease.viewmodels.CheckoutViewModel
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.shopease.components.BottomBar
import com.example.shopease.components.TopBar
import kotlin.text.contains

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CartScreen(navController: NavController, viewModel: CartViewModel, checkoutViewModel: CheckoutViewModel) {
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
                .padding(top = 70.dp, start = 24.dp, end = 24.dp, bottom = 24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "🛒 Your Cart",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFEC407A)
            )

            Spacer(modifier = Modifier.height(12.dp))

            cartItems.forEach { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(item.imageUrl),
                            contentDescription = item.name,
                            modifier = Modifier.size(70.dp)
                        )

                        Spacer(Modifier.width(12.dp))

                        Column(Modifier.weight(1f)) {
                            Text(item.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("₹${item.price}", color = Color.Gray)

                            Row(verticalAlignment = Alignment.CenterVertically) {
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
                                tint = Color.Red
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "Total: ₹$total",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFEC407A),
                modifier = Modifier.align(Alignment.End)
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    val items = viewModel.cartItems.toList()
                    val totalAmount = items.sumOf { it.price.toDouble() * it.quantity }

                    checkoutViewModel.cartOrderedItems = items
                    checkoutViewModel.originalAmount = totalAmount
                    checkoutViewModel.totalAmount = totalAmount

                    navController.navigate("shipping_info")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEC8D8))
            ) {
                Text("Proceed to Checkout", color = Color(0xFFEC407A), fontSize = 18.sp)
            }
        }
    }
}
