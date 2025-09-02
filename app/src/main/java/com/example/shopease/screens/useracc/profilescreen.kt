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

@Composable
fun ProfileScreen(navController: NavController) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val cartOrders = remember { mutableStateListOf<Triple<List<CartItem>, Long,Double>>() }
    val productOrders = remember { mutableStateListOf<Triple<Product, Long,Double>>() }


    var name by remember { mutableStateOf("Loading...") }
    var email by remember { mutableStateOf("Loading...") }

    LaunchedEffect(uid) {
        uid?.let {
            val dbRef = FirebaseDatabase.getInstance().getReference("orders").child(uid)
            dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    cartOrders.clear()
                    productOrders.clear()

                    for (orderSnap in snapshot.children) {
                        val timestamp = orderSnap.child("timestamp").getValue(Long::class.java) ?: 0L
                        val totalAmount = orderSnap.child("totalAmount").getValue(Double::class.java) ?: 0.0

                        if (orderSnap.child("items").exists()) {
                            val items = orderSnap.child("items").children.mapNotNull {
                                it.getValue(CartItem::class.java)
                            }
                            cartOrders.add(Triple(items, timestamp, totalAmount))
                        } else {
                            val product = orderSnap.child("orderedItems").children.firstOrNull()?.getValue(Product::class.java)
                            if (product != null) {
                                productOrders.add(Triple(product, timestamp, totalAmount))
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
        bottomBar = { BottomBar(navController) },containerColor = Color(0xFFB2E4FF)
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
                // ✅ Logged in → Show Logout button
                Button(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true } // or your start destination
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC407A))
                ) {
                    Text("Logout", color = Color.White)
                }
            } else {
                // ❌ Not logged in → Show Login button
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

            cartOrders.forEach { (items, timestamp, totalAmount) ->
                val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(timestamp))
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text("📅 $date")
                        items.forEach { item ->
                            Text("${item.name} x${item.quantity}")
                            if (!item.ebookUrl.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                TextButton(onClick = {
                                    val intent = android.content.Intent(
                                        android.content.Intent.ACTION_VIEW,
                                        android.net.Uri.parse(item.ebookUrl)
                                    )
                                    intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                                    navController.context.startActivity(intent)
                                }) {
                                    Text("📘 Open eBook", color = Color(0xFF4A7C59))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Total: ₹$totalAmount", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            productOrders.forEach { (product, timestamp, totalAmount) ->
                val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(timestamp))
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text("📅 $date")
                        Text(product.name)
                        if (!product.ebookUrl.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            TextButton(onClick = {
                                val intent = android.content.Intent(
                                    android.content.Intent.ACTION_VIEW,
                                    android.net.Uri.parse(product.ebookUrl)
                                )
                                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                                navController.context.startActivity(intent)
                            }) {
                                Text("📘 Open eBook", color = Color(0xFF4A7C59))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Total: ₹$totalAmount", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("ℹ️ Help & Info", fontSize = 20.sp, fontWeight = FontWeight.SemiBold,color = Color(0xFFEC407A))

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
                            Text("OK",color = Color(0xFFEC407A))
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
