package com.example.shopease.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import coil.compose.AsyncImage
import com.example.shopease.Product
import com.example.shopease.components.BottomBar
import com.example.shopease.components.TopBar
import com.example.shopease.viewmodels.BannerViewModel
import com.example.shopease.viewmodels.ProductViewModel

@Composable
fun HomeScreen(navController: NavController, viewModel: ProductViewModel = viewModel()) {
    var isSearching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val allProducts = viewModel.products
    val products = if (isSearching) {
        allProducts.filter {
            it.name.contains(searchQuery, ignoreCase = true) || it.category.contains(searchQuery, ignoreCase = true)
        }
    } else {
        allProducts
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
        bottomBar = { BottomBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFFFF0F5), Color(0xFFE3F2FD))
                    )
                )
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            HeroBannerCarousel()
            //Spacer(modifier = Modifier.height(16.dp))
            CategorySection(navController)
            Spacer(modifier = Modifier.height(16.dp))
            ProductHorizontalSection("Exclusive Picks", products, navController)
        }
    }
}

@Composable
fun HeroBannerCarousel(viewModel: BannerViewModel = viewModel()) {
    val banners = viewModel.bannerList

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(banners.size) { index ->
            Card(
                modifier = Modifier
                    .fillParentMaxWidth(0.75f)
                    .aspectRatio(1.7f)
                    .shadow(8.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Box {
                    AsyncImage(
                        model = banners[index],
                        contentDescription = "Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color(0xAA000000))
                                )
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun CategorySection(navController: NavController) {
    Text(
        text = "Shop by Category",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
        color = Color(0xFF4B0082), // Indigo
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold
    )

    val categories = listOf("Toys", "E-books", "Beauty", "General")
    val categoryColors = listOf(
        Color(0xFFFFC1E3), Color(0xFFFFE0B2),
        Color(0xFFD1C4E9), Color(0xFFA5D6A7)
    )

    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {
        items(categories.size) { index ->
            val category = categories[index]
            val bgColor = categoryColors[index % categoryColors.size]

            Card(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .width(130.dp)
                    .height(80.dp)
                    .clickable {
                        val encodedCategory = java.net.URLEncoder.encode(category, "UTF-8")
                        navController.navigate("category/$encodedCategory")
                    }
                    .shadow(6.dp, RoundedCornerShape(25.dp)),
                shape = RoundedCornerShape(25.dp),
                colors = CardDefaults.cardColors(containerColor = bgColor),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Optional: Add icons here with painterResource
                        Text(
                            text = category,
                            color = Color(0xFF4B0082),
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductHorizontalSection(title: String, products: List<Product>, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 12.dp, bottom = 12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFF4B0082),
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 14.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(products.size) { index ->
                ProductCard(product = products[index], navController = navController)
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, navController: NavController) {
    Card(
        modifier = Modifier
            .width(170.dp)
            .height(270.dp)
            .clickable { navController.navigate("product_detail/${product.productId}") }
            .shadow(6.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        // Gradient background inside the card
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFFFF0F5), Color(0xFFFFE6F0))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = product.imageUrls.firstOrNull() ?: "",
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(130.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    product.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFDA1884)
                )
                Text(
                    "₹${product.price}",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2F2F2F)
                )
            }
        }
    }

}

