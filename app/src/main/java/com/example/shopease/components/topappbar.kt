package com.example.shopease.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController : NavController,
    isSearching: Boolean,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onSearchClick: () -> Unit
) {
    TopAppBar(
        title = {
            if (isSearching) {
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = {
                        Text(
                            "Search products",color = Color.White
                        )
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF9BC7EE),
                        unfocusedContainerColor = Color(0xFF9BC7EE),
                        disabledContainerColor = Color(0xFF9BC7EE),
                        focusedTextColor = Color.Black,      // 👈 actual text color
                        unfocusedTextColor = Color.Black,    // 👈 actual text color
                        disabledTextColor = Color.Black,
                        cursorColor = Color.Black,           // 👈 cursor visible
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

            } else {
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Spacer(modifier = Modifier.width(8.dp))
//                    Image(
//                        painter = painterResource(id = R.drawable.babydinologo),
//                        contentDescription = "Logo",
//                        modifier = Modifier
//                            .size(55.dp)
//                            .padding(end = 8.dp)
//                    )
                    Text(
                        text = "ShopEase",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Default.Search, contentDescription = "Search",tint = Color.White)
            }
            IconButton(onClick = { navController.navigate("cartscreen")}) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Cart",tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor =  Color(0xFF9BC7EE)
        ),
        modifier = Modifier.height(95.dp)
    )
}

