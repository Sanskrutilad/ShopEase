package com.example.shopease.screens.useracc

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.runtime.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import androidx.compose.material3.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.shopease.CartItem
import com.example.shopease.Product
import com.example.shopease.components.BottomBar
import com.example.shopease.components.TopBar
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

// Simple data class to hold order information with orderId
data class OrderWithId(
    val orderId: String,
    val items: List<CartItem>? = null,
    val product: Product? = null,
    val timestamp: Long,
    val totalAmount: Double,
    val trackingId: String? = null,
    val orderStatus: String? = null
)

@Composable
fun ProfileScreen(navController: NavController) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val cartOrders = remember { mutableStateListOf<OrderWithId>() }
    val productOrders = remember { mutableStateListOf<OrderWithId>() }

    var name by remember { mutableStateOf("Loading...") }
    var email by remember { mutableStateOf("Loading...") }

    LaunchedEffect(uid) {
        uid?.let {
            val dbRef = FirebaseDatabase.getInstance().getReference("orders").child(it)
            dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    cartOrders.clear()
                    productOrders.clear()

                    for (orderSnap in snapshot.children) {
                        val orderId = orderSnap.key ?: ""
                        val timestamp = orderSnap.child("timestamp").getValue(Long::class.java) ?: 0L
                        val totalAmount = orderSnap.child("totalAmount").getValue(Double::class.java) ?: 0.0
                        val trackingId = orderSnap.child("trackingId").getValue(String::class.java)
                        val orderStatus = orderSnap.child("orderStatus").getValue(String::class.java) ?: "confirmed"

                        if (orderSnap.child("items").exists()) {
                            val items = orderSnap.child("items").children.mapNotNull {
                                it.getValue(CartItem::class.java)
                            }
                            cartOrders.add(OrderWithId(orderId, items = items, timestamp = timestamp,
                                totalAmount = totalAmount, trackingId = trackingId, orderStatus = orderStatus))
                        } else if (orderSnap.child("orderedItems").exists()) {
                            val product = orderSnap.child("orderedItems").children.firstOrNull()?.getValue(Product::class.java)
                            if (product != null) {
                                productOrders.add(OrderWithId(orderId, product = product, timestamp = timestamp,
                                    totalAmount = totalAmount, trackingId = trackingId, orderStatus = orderStatus))
                            }
                        } else {
                            // Fallback for orders without items/orderedItems
                            cartOrders.add(OrderWithId(orderId, timestamp = timestamp,
                                totalAmount = totalAmount, trackingId = trackingId, orderStatus = orderStatus))
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })

            val ref = FirebaseDatabase.getInstance().getReference("users").child(it)
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    name = snapshot.child("fullName").getValue(String::class.java) ?: "N/A"
                    email = snapshot.child("email").getValue(String::class.java) ?: "N/A"
                }

                override fun onCancelled(error: DatabaseError) {
                    name = "Error loading name"
                    email = "Error loading email"
                }
            })
        } ?: run {
            name = "Guest"
            email = "Not logged in"
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                navController, isSearching = false, searchQuery = "",
                onSearchClick = {}, onSearchChange = {}
            )
        },
        bottomBar = { BottomBar(navController) },
        containerColor = Color(0xFFB2E4FF)
    ) { innerPadding ->
        Column(
            modifier = Modifier.background(Color(0xFFCDEFF5))
                .padding(innerPadding)
                .fillMaxSize().verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text("Profile", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEC407A))
            Spacer(modifier = Modifier.height(24.dp))

            Text("Name: $name", fontSize = 18.sp)
            Text("Email: $email", fontSize = 18.sp)

            Spacer(modifier = Modifier.height(24.dp))
            val currentUser = FirebaseAuth.getInstance().currentUser

            if (currentUser != null) {
                // Logout Button
                Button(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC407A))
                ) {
                    Text("Logout", color = Color.White)
                }
            } else {
                // Login Button
                Button(
                    onClick = {
                        navController.navigate("login")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC407A))
                ) {
                    Text("Login", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("🧾 Past Orders", fontSize = 22.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFEC407A))
            Spacer(modifier = Modifier.height(10.dp))

            // Display cart orders with click functionality
            cartOrders.forEach { order ->
                OrderCard(
                    order = order,
                    isCartOrder = true,
                    onOrderClick = { orderId ->
                        navController.navigate("order_tracking/$orderId")
                    }
                )
            }

            // Display product orders with click functionality
            productOrders.forEach { order ->
                OrderCard(
                    order = order,
                    isCartOrder = false,
                    onOrderClick = { orderId ->
                        navController.navigate("order_tracking/$orderId")
                    }
                )
            }

            // Show message if no orders
            if (cartOrders.isEmpty() && productOrders.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No orders yet!", fontSize = 16.sp, color = Color.Gray)
                        Text("Start shopping to see your orders here", fontSize = 14.sp, color = Color.LightGray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("ℹ️ Help & Info", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFEC407A))

            Spacer(modifier = Modifier.height(16.dp))
            val showDialog = remember { mutableStateOf(false) }
            val selectedLabel = remember { mutableStateOf("") }

            Column {
                listOf("📖 FAQs", "📩 Contact Us", "\uD83E\uDD95 About Baby Dino").forEach { label ->
                    Text(
                        label,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .clickable {
                                selectedLabel.value = label
                                showDialog.value = true
                            }
                    )
                }
            }

            if (showDialog.value) {
                AlertDialog(
                    onDismissRequest = { showDialog.value = false },
                    confirmButton = {
                        TextButton(onClick = { showDialog.value = false }) {
                            Text("OK", color = Color(0xFFEC407A))
                        }
                    },
                    title = { Text(text = selectedLabel.value) },
                    text = {
                        Text(
                            when (selectedLabel.value) {
                                "📖 FAQs" -> """
        Here are some frequently asked questions about Baby Dino:

        1. What types of products do you offer?
        We offer a curated range of kids' products (toys, books, stationery) and women's accessories 
        (purses, skincare, keychains, etc.) with a focus on quality and affordability.

        2. How long does delivery usually take?
        Our delivery time typically ranges from 2 to 5 business days, depending on your location. 
        You'll receive tracking updates after your order is placed.

        3. Can I return a product?
        We only accept returns if the product is received in a damaged or broken condition. 
        Please report it within 24 hours with proper photo or video proof.

    """.trimIndent()

                                "📩 Contact Us" -> """
        You can reach out to us:

        📞 Contact: 8209029726
        📧 Email: babydinoindia01@gmail.com
    """.trimIndent()

                                "\uD83E\uDD95 About Baby Dino" -> """
        Baby Dino is your go-to app for delightful kids and women products!
        We bring you affordable, quality products with fast delivery and great support.
    """.trimIndent()

                                else -> ""
                            },
                            color = Color.Black
                        )
                    },
                    containerColor = Color(0xFFFEC8D8)
                )
            }
        }
    }
}

@Composable
fun OrderCard(
    order: OrderWithId,
    isCartOrder: Boolean,
    onOrderClick: (String) -> Unit
) {
    val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(order.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onOrderClick(order.orderId) },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            // Order header with status and click indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("📅 $date", fontWeight = FontWeight.SemiBold)
                    order.trackingId?.let { trackingId ->
                        Text("Tracking: $trackingId", fontSize = 12.sp, color = Color.Gray)
                    }
                }

                // Status indicator
                OrderStatusIndicator(order.orderStatus ?: "confirmed")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Order items
            if (isCartOrder && order.items != null) {
                order.items.forEach { item ->
                    Text("• ${item.name} x${item.quantity}")
                    if (!item.ebookUrl.isNullOrEmpty()) {
                        Text("   📘 eBook available", color = Color(0xFF4A7C59), fontSize = 12.sp)
                    }
                }
            } else if (!isCartOrder && order.product != null) {
                Text("• ${order.product.name}")
                if (!order.product.ebookUrl.isNullOrEmpty()) {
                    Text("   📘 eBook available", color = Color(0xFF4A7C59), fontSize = 12.sp)
                }
            } else {
                Text("• Order details loading...", color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Total: ₹${order.totalAmount}", fontWeight = FontWeight.SemiBold, color = Color(0xFFEC407A))
            Text("Tap to track order →", fontSize = 12.sp, color = Color.Gray, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
        }
    }
}

@Composable
fun OrderStatusIndicator(orderStatus: String) {
    val (statusText, statusColor) = when (orderStatus) {
        "delivered" -> "Delivered" to Color(0xFF4CAF50)
        "shipped" -> "Shipped" to Color(0xFF2196F3)
        "out_for_delivery" -> "Out for Delivery" to Color(0xFFFF9800)
        "packed" -> "Packed" to Color(0xFF9C27B0)
        else -> "Confirmed" to Color(0xFF607D8B)
    }

    Box(
        modifier = Modifier
            .background(statusColor.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = statusText,
            color = statusColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}