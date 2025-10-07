package com.example.shopease.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.shopease.Product
import com.example.shopease.components.BottomBar
import com.example.shopease.components.TopBar
import com.example.shopease.viewmodels.CartViewModel
import com.example.shopease.viewmodels.CheckoutViewModel
import com.example.shopease.viewmodels.ProductViewModel
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProductListScreen(
    navController: NavController,
    categoryName: String,
    viewModel: ProductViewModel = viewModel()
) {
    val products = viewModel.getProductsByCategory(categoryName)
    var isSearching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredProducts = if (isSearching) {
        products.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.category.contains(searchQuery, ignoreCase = true)
        }
    } else products

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
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFFFF0F5), Color(0xFFE3F2FD))
                    )
                )
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)) {
                Text(
                    text = "Products in $categoryName",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4B0082),
                        fontSize = 24.sp
                    ),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredProducts.size) { index ->
                        val product = filteredProducts[index]
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { navController.navigate("product_detail/${product.productId}") }
                                .shadow(6.dp, RoundedCornerShape(20.dp)),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(product.imageUrls.firstOrNull() ?: ""),
                                    contentDescription = product.name,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(RoundedCornerShape(16.dp)),
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        product.name,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF333333)
                                        )
                                    )
                                    Text(
                                        "₹${product.price}",
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            color = Color(0xFFEC407A),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProductDetailScreen(
    navController: NavController,
    product: Product,
    checkoutViewModel: CheckoutViewModel
) {
    val viewModel: ProductViewModel = viewModel()
    val cartViewModel: CartViewModel = viewModel()

    var isSearching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val currentUser = FirebaseAuth.getInstance().currentUser
    val isLoggedIn = currentUser != null

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
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFFFF0F5), Color(0xFFE3F2FD))
                    )
                )
                .padding(16.dp)
        ) {
            // Image carousel
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                product.imageUrls.forEach { imageUrl ->
                    Image(
                        painter = rememberAsyncImagePainter(imageUrl),
                        contentDescription = product.name,
                        modifier = Modifier
                            .height(260.dp)
                            .width(360.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White)
                            .shadow(6.dp, RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = product.name,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4B0082)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "₹${product.price}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFEC407A)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = formatDescription(product.description),
                        fontSize = 17.sp,
                        color = Color(0xFF444444),
                        lineHeight = 24.sp
                    )

                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        if (isLoggedIn) cartViewModel.addProductToCart(product)
                        else navController.navigate("login?productId=${product.productId}")
                    },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .width(170.dp)
                        .height(55.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFE6EF)
                    )
                ) {
                    Text(
                        "Add to Cart",
                        fontSize = 18.sp,
                        color = Color(0xFFEC407A),
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = {
                        checkoutViewModel.orderedItems = listOf(product)
                        checkoutViewModel.originalAmount = product.price.toDouble()
                        if (isLoggedIn)
                            navController.navigate("single_order?productId=${product.productId}")
                        else
                            navController.navigate("login?productId=${product.productId}")
                    },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .width(170.dp)
                        .height(55.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFE6EF)
                    )
                ) {
                    Text(
                        "Buy Now",
                        fontSize = 18.sp,
                        color = Color(0xFFEC407A),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
fun formatDescription(description: String): String {
    return description
        .replace(". ", ".\n")  // adds a new line after every full stop
        .trim()
}
