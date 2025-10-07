package com.example.shopease.screens.useracc

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.shopease.CartItem
import com.example.shopease.Product
import com.example.shopease.components.BottomBar
import com.example.shopease.components.TopBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

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
        containerColor = Color.Transparent
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFFFF0F5), Color(0xFFE3F2FD))
                    )
                )
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxSize().padding(20.dp)
                ,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🌸 Profile Header Card
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(Color(0xFFFFF9FB)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEC407A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name.firstOrNull()?.uppercase() ?: "U",
                            color = Color.White,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEC407A))
                    Text(email, fontSize = 15.sp, color = Color(0xFF707070))
                    Spacer(modifier = Modifier.height(14.dp))

                    val currentUser = FirebaseAuth.getInstance().currentUser
                    Button(
                        onClick = {
                            if (currentUser != null) {
                                FirebaseAuth.getInstance().signOut()
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            } else {
                                navController.navigate("login")
                            }
                        },
                        shape = RoundedCornerShape(40),
                        colors = ButtonDefaults.buttonColors(Color(0xFFFF80A0)),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            text = if (currentUser != null) "Logout" else "Login",
                            color = Color.White,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Divider(thickness = 1.dp, color = Color(0xFFE6D7E7))
            Spacer(modifier = Modifier.height(16.dp))

            // 🧾 Past Orders Section
            Text("🧾 Past Orders", fontSize = 21.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFEC407A))
            Spacer(modifier = Modifier.height(12.dp))

            if (cartOrders.isEmpty() && productOrders.isEmpty()) {
                Text(
                    "No orders yet! Start shopping to see your orders here.",
                    color = Color(0xFF888888),
                    fontSize = 14.sp
                )
            } else {
                cartOrders.forEach { order ->
                    OrderCard(order = order, isCartOrder = true) {
                        navController.navigate("order_tracking/${order.orderId}")
                    }
                }
                productOrders.forEach { order ->
                    OrderCard(order = order, isCartOrder = false) {
                        navController.navigate("order_tracking/${order.orderId}")
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
            Divider(thickness = 1.dp, color = Color(0xFFE6D7E7))
            Spacer(modifier = Modifier.height(16.dp))

            // ℹ️ Help & Info Section
            Text("ℹ️ Help & Info", fontSize = 21.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFEC407A))
            Spacer(modifier = Modifier.height(10.dp))

            val showDialog = remember { mutableStateOf(false) }
            val selectedLabel = remember { mutableStateOf("") }

            listOf("📖 FAQs", "📩 Contact Us", "\uD83D\uDECD\uFE0F About ShopEase").forEach { label ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable {
                            selectedLabel.value = label
                            showDialog.value = true
                        },
                    colors = CardDefaults.cardColors(Color(0xFFFFF9FB)),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Text(
                        text = label,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 16.sp,
                        color = Color(0xFF444444)
                    )
                }
            }

            if (showDialog.value) {
                InfoDialog(selectedLabel.value) { showDialog.value = false }
            }
        }

    }
}

@Composable
fun InfoDialog(label: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("OK", color = Color(0xFFEC407A)) }
        },
        title = { Text(label) },
        text = {
            Text(
                when (label) {
                    "📖 FAQs" -> "We offer quality kids & women's products delivered within 2–5 days. Returns are accepted only if damaged."
                    "📩 Contact Us" -> "📞 94233-66903\n📧 shopease@gmail.com"
                    "\uD83D\uDECD\uFE0F About ShopEase" -> "Your go-to app for e-books and cute, affordable, and quality kids & women products!"
                    else -> ""
                },
                color = Color.Black
            )
        },
        containerColor = Color(0xFFFFE6EB)
    )
}

@Composable
fun OrderCard(order: OrderWithId, isCartOrder: Boolean, onOrderClick: (String) -> Unit) {
    val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(order.timestamp))
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onOrderClick(order.orderId) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("📅 $date", fontWeight = FontWeight.SemiBold)
                    Text("Tracking: ${order.trackingId ?: "N/A"}", fontSize = 12.sp, color = Color.Gray)
                }
                OrderStatusIndicator(order.orderStatus ?: "confirmed")
            }

            Spacer(modifier = Modifier.height(8.dp))
            if (isCartOrder && order.items != null) {
                order.items.forEach { item -> Text("• ${item.name} x${item.quantity}") }
            } else if (!isCartOrder && order.product != null) {
                Text("• ${order.product.name}")
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Total: ₹${order.totalAmount}", fontWeight = FontWeight.Bold, color = Color(0xFFEC407A))
            Text("Tap to track →", fontSize = 12.sp, color = Color.Gray, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
        }
    }
}

@Composable
fun OrderStatusIndicator(status: String) {
    val (text, color) = when (status) {
        "delivered" -> "Delivered" to Color(0xFF4CAF50)
        "shipped" -> "Shipped" to Color(0xFF2196F3)
        "out_for_delivery" -> "Out for Delivery" to Color(0xFFFF9800)
        "packed" -> "Packed" to Color(0xFF9C27B0)
        else -> "Confirmed" to Color(0xFF607D8B)
    }
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text, color = color, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
