package com.example.shopease.screens.checkout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
fun SingleOrderScreen(
    product: Product,
    navController: NavController,
    checkoutViewModel: CheckoutViewModel
) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid

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
        bottomBar = { BottomBar(navController) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFFFF0F5), Color(0xFFE3F2FD))
                    )
                )
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                ,
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "BUY NOW",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFFEC407A),
                fontWeight = FontWeight.Bold
            )
            // Product Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .shadow(4.dp, shape = RoundedCornerShape(16.dp))
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = rememberAsyncImagePainter(product.imageUrls.firstOrNull() ?: ""),
                    contentDescription = product.name,
                    modifier = Modifier.size(100.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        product.name,
                        fontSize = 18.sp,
                        color = Color(0xFFEC407A)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "₹${product.price}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

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
                        "Total: ₹${product.price}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFEC407A)
                    )

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = {
                            checkoutViewModel.orderedItems = listOf(product)
                            checkoutViewModel.originalAmount = product.price.toDouble()
                            checkoutViewModel.totalAmount = product.price.toDouble()

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
