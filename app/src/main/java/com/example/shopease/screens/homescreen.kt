package com.example.shopease.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
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
        }, bottomBar = { BottomBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFCDEFF5))
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            HeroBannerCarousel()
            Spacer(modifier = Modifier.height(15.dp))
            CategorySection(navController)
            Spacer(modifier = Modifier.height(15.dp))
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
            .height(180.dp), // Optional, to control row height
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(banners.size) { index ->
            Card(
                modifier = Modifier
                    .fillParentMaxWidth(0.7f) // Takes 70% of the LazyRow width
                    .aspectRatio(1.6f),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                AsyncImage(
                    model = banners[index],
                    contentDescription = "Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

}


@Composable
fun CategorySection(navController: NavController) {
    Text(
        text = "Shop by Category",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(start = 16.dp, bottom = 12.dp),
        color = Color(0xFF1B1464), // Darker pink
        fontSize = 21.sp,
        fontWeight = FontWeight.Bold
    )

    val categories = listOf("Toys", "E-books", "Beauty", "General")

    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {
        items(categories.size) { index ->
            val category = categories[index]

            Card(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .width(120.dp)
                    .height(70.dp)
                    .clickable {
                        val encodedCategory = java.net.URLEncoder.encode(category, "UTF-8")
                        navController.navigate("category/$encodedCategory")
                    },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF5D3DE) // light pink background
                ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = category,
                        color = Color(0xFFEC407A), // matching text color
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}


@Composable
fun ProductHorizontalSection(title: String, products: List<Product>, navController: NavController) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 16.dp, top = 12.dp, bottom = 12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFF1B1464), // Darker, readable pink
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(start = 0.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
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
            .padding(end = 12.dp)
            .clickable {
                navController.navigate("product_detail/${product.productId}")
            }

            .width(160.dp)
            .height(240.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5D3DE)), // Cotton white
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = product.imageUrls.firstOrNull() ?: "", // Updated here
                contentDescription = product.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.height(25.dp))
            Text(
                product.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFEC407A) // Rose Pink
            )
            Text(
                "₹${product.price}",
                fontSize = 18.sp,
                color = Color(0xFF0D0E0E) // Cool Gray
            )
        }
    }

}


