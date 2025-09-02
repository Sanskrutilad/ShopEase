package com.example.shopease.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun BottomBar(navController : NavController) {

    NavigationBar(containerColor = Color(0xFF9EDCEA),windowInsets = WindowInsets(0)) {
        Spacer(modifier = Modifier.width(60.dp))
        IconButton(onClick = { navController.navigate("homescreen") }) {
            Icon(Icons.Default.Home, contentDescription = "Home",tint = Color.Black)
        }
        Spacer(modifier = Modifier.width(150.dp))
        IconButton(onClick = {navController.navigate("profile") }) {
            Icon(Icons.Default.Person, contentDescription = "Profile",tint = Color.Black)
        }
    }
}
