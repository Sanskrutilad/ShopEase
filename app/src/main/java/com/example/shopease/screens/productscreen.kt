package com.example.shopease.screens


import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import kotlin.text.contains


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

    val allproducts = if (isSearching) {
        products.filter {
            it.name.contains(searchQuery, ignoreCase = true) || it.category.contains(searchQuery, ignoreCase = true)
        }
    } else {
        products
    }
    Scaffold(topBar = {
        TopBar(
            navController,
            isSearching = isSearching,
            searchQuery = searchQuery,
            onSearchChange = { searchQuery = it },
            onSearchClick = { isSearching = !isSearching }
        )
    },
        bottomBar = {
            BottomBar(navController)
        },containerColor = Color(0xFFCDEFF5)
    )
    { Column(modifier = Modifier.padding(top = 30.dp)) {
        Text("Products in $categoryName", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
            items(products.size) { index ->
                val product = products[index]
                Card(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("product_detail/${product.productId}")
                        }
                    ,
                    colors = CardDefaults.cardColors(Color.White)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = rememberAsyncImagePainter(product.imageUrls.firstOrNull() ?: ""),
                            contentDescription = product.name,
                            modifier = Modifier.size(100.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(product.name, style = MaterialTheme.typography.titleSmall)
                            Text("₹${product.price}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }  }

}
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProductDetailScreen(
    navController: NavController,
    product: Product,
    checkoutViewModel: CheckoutViewModel
) {
    val viewModel: ProductViewModel = viewModel()

    var isSearching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val currentUser = FirebaseAuth.getInstance().currentUser
    val isLoggedIn = currentUser != null


    val allProducts = viewModel.products
    val products = if (isSearching) {
        allProducts.filter {
            it.name.contains(searchQuery, ignoreCase = true) || it.category.contains(searchQuery, ignoreCase = true)
        }
    } else {
        allProducts
    }
    Scaffold(topBar = {
        TopBar(
            navController,
            isSearching = isSearching,
            searchQuery = searchQuery,
            onSearchChange = { searchQuery = it },
            onSearchClick = { isSearching = !isSearching }
        )
    },
        bottomBar = {
            BottomBar(navController)
        }, containerColor=Color(0xFFCDEFF5)) {
            innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            val cartViewModel: CartViewModel = viewModel()
            Spacer(modifier = Modifier.height(40.dp))

            // Product images
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                product.imageUrls.forEach { imageUrl ->
                    Image(
                        painter = rememberAsyncImagePainter(imageUrl),
                        contentDescription = product.name,
                        modifier = Modifier
                            .height(240.dp)
                            .width(370.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White),
                        contentScale = ContentScale.Crop
                    )
                }
            }


            Spacer(modifier = Modifier.height(24.dp))

            // Card with product info
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = product.name,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "₹${product.price}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = product.description,
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    if (isLoggedIn) {
                        cartViewModel.addProductToCart(product)
                    } else {
                        navController.navigate("login?productId=${product.productId}")
                    }
                }, shape = RoundedCornerShape(50) ,
                    modifier = Modifier.width(180.dp).height(50.dp),colors = ButtonDefaults.buttonColors(Color(0xFFF5D3DE))) {
                    Text("Add to Cart" , fontSize = 20.sp,color =Color(0xFFEC407A))
                }
                Button(
                    onClick = {
                        checkoutViewModel.orderedItems = listOf(product)
                        checkoutViewModel.originalAmount = product.price.toDouble()

                        if (FirebaseAuth.getInstance().currentUser != null) {
                            navController.navigate("single_order?productId=${product.productId}")
                        } else {
                            navController.navigate("login?productId=${product.productId}")
                        }
                    },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.width(180.dp).height(50.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFFF5D3DE))
                ) {
                    Text("Buy Now", fontSize = 20.sp, color = Color(0xFFEC407A))
                }

            }
        }
    }

}





