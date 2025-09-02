package com.example.shopease.screens.checkout

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.shopease.Product
import com.example.shopease.components.BottomBar
import com.example.shopease.components.TopBar
import com.example.shopease.viewmodels.CheckoutViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SingleOrderScreen(product: Product, navController: NavController, checkoutViewModel: CheckoutViewModel) {
    val context = LocalContext.current
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    Scaffold(
        topBar = { TopBar(navController, isSearching = false, searchQuery = "", onSearchClick = {}, onSearchChange = {})
        },
        bottomBar = { BottomBar(navController) }
        ,containerColor = Color(0xFFCDEFF5),
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = rememberAsyncImagePainter(product.imageUrls.firstOrNull() ?: ""),
                        contentDescription = product.name,
                        modifier = Modifier.size(100.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(product.name, style = MaterialTheme.typography.titleLarge)
                        Text("₹${product.price}", style = MaterialTheme.typography.bodyLarge)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        if (uid != null) {
                            checkoutViewModel.orderedItems = listOf(product)
                            checkoutViewModel.originalAmount = product.price.toDouble()
                            checkoutViewModel.totalAmount = product.price.toDouble()
                            //Log.d("buynow", "originalAmount: ${checkoutViewModel.originalAmount}, totalAmount: ${checkoutViewModel.totalAmount}")

                            navController.navigate("shipping_info")
                        } },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEC8D8))
                ) {
                    Text("Continue to Shipping", fontSize = 18.sp, color = Color.White)
                }
            }
        }
    )
}

