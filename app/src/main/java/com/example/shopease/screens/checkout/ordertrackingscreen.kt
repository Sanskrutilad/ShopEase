package com.example.shopease.screens.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.shopease.viewmodels.OrderTrackingViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    orderId: String?,
    navController: NavController
) {
    val viewModel: OrderTrackingViewModel = viewModel()
    val orderData by viewModel.orderData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(orderId) {
        orderId?.let { viewModel.fetchOrderDetails(it) }
    }

    error?.let { errorMessage ->
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("Error") },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) {
                    Text("OK")
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFFFF0F5), Color(0xFFE3F2FD))
                )
            )
    ) {
        // Heading and back button at the top
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(25.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF4B0082))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "🛍️ Order Tracking",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color(0xFF4B0082)
            )
        }

        // Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 72.dp) // padding to avoid overlap with heading
        ) {
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFFEC407A))
                    }
                }

                orderData != null -> {
                    OrderTrackingContent(orderData = orderData!!)
                }

                else -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Order not found", color = Color.Gray)
                    }
                }
            }
        }
    }
}


@Composable
fun OrderTrackingContent(orderData: Map<String, Any>) {
    val stages = getOrderStages(orderData)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            OrderHeaderSection(orderData)
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Text(
                "📦 Order Status",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4B0082),
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            OrderTimeline(stages = stages)
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Text(
                "🛒 Items in Order",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4B0082),
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            OrderItemsSection(orderData)
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Text(
                "📍 Delivery Address",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4B0082),
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            OrderAddressSection(orderData)
        }
    }
}

@Composable
fun OrderHeaderSection(orderData: Map<String, Any>) {
    val orderStatus = orderData["orderStatus"] as? String ?: ""
    val timestamp = orderData["timestamp"] as? Long ?: 0L
    val totalAmount = when (val amount = orderData["totalAmount"]) {
        is Double -> amount
        is Float -> amount.toDouble()
        is Int -> amount.toDouble()
        is Long -> amount.toDouble()
        is String -> amount.toDoubleOrNull() ?: 0.0
        else -> 0.0
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Order", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                OrderStatusChip(orderStatus = orderStatus)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Placed on ${formatDate(timestamp)}",
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Total: ₹$totalAmount",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = Color(0xFFEC407A)
            )
        }
    }
}

@Composable
fun OrderTimeline(stages: List<OrderStage>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            stages.forEachIndexed { index, stage ->
                TimelineItem(
                    stage = stage,
                    isFirst = index == 0,
                    isLast = index == stages.size - 1
                )
                if (index < stages.size - 1) Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun OrderItemsSection(orderData: Map<String, Any>) {
    val items = getOrderItems(orderData)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Order Items (${items.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            items.forEachIndexed { index, item ->
                OrderItemRow(item)
                if (index < items.size - 1) Divider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}

@Composable
fun OrderAddressSection(orderData: Map<String, Any>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Delivery Address",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(orderData["name"] as? String ?: "", fontWeight = FontWeight.SemiBold)
            Text(orderData["phone"] as? String ?: "", color = Color.Gray)

            Spacer(modifier = Modifier.height(8.dp))

            Text(orderData["address"] as? String ?: "")
            Text(
                "${orderData["city"] as? String ?: ""}, ${orderData["state"] as? String ?: ""} - ${orderData["pincode"] as? String ?: ""}",
                color = Color.Gray
            )
        }
    }
}


@Composable
fun OrderStatusChip(orderStatus: String) {
    val (backgroundColor, textColor) = when (orderStatus) {
        "delivered" -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        "shipped" -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        "out_for_delivery" -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .background(backgroundColor, shape = RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = orderStatus.replace("_", " ").capitalizeWords(),
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun TimelineItem(stage: OrderStage, isFirst: Boolean, isLast: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.width(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isFirst) {
                VerticalLine()
            }

            Icon(
                imageVector = if (stage.completed) Icons.Default.CheckCircle else Icons.Default.AddCircle,
                contentDescription = null,
                tint = if (stage.completed) Color.Green else Color.Gray,
                modifier = Modifier.size(24.dp)
            )

            if (!isLast) {
                VerticalLine()
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                stage.description,
                style = MaterialTheme.typography.bodyMedium,
                color = if (stage.completed) MaterialTheme.colorScheme.onSurface else Color.Gray,
                fontWeight = if (stage.completed) FontWeight.Bold else FontWeight.Normal
            )

            stage.timestamp?.let { timestamp ->
                Text(
                    formatDate(timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun VerticalLine() {
    Box(
        modifier = Modifier
            .width(2.dp)
            .height(16.dp)
            .background(Color.Gray.copy(alpha = 0.5f))
    )
}

@Composable
fun OrderItemRow(item: Map<String, Any>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val imageUrl = item["imageUrl"] as? String
        if (!imageUrl.isNullOrEmpty()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                item["name"] as? String ?: "Unknown",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                "₹${item["price"]}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )

            val quantity = item["quantity"] as? Int
            if (quantity != null && quantity > 1) {
                Text(
                    "Qty: $quantity",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}


// Helper data class for timeline
data class OrderStage(
    val stage: String,
    val timestamp: Long?,
    val description: String,
    val completed: Boolean
)

// Helper functions
fun getOrderStages(orderData: Map<String, Any>): List<OrderStage> {
    val orderStages = orderData["orderStages"] as? Map<String, Any> ?: emptyMap()

    return listOf(
        OrderStage("confirmed", orderStages["confirmed"] as? Long, "Order Confirmed", true),
        OrderStage("packed", orderStages["packed"] as? Long, "Packed", orderStages["packed"] != null),
        OrderStage("shipped", orderStages["shipped"] as? Long, "Shipped", orderStages["shipped"] != null),
        OrderStage("out_for_delivery", orderStages["out_for_delivery"] as? Long, "Out for Delivery", orderStages["out_for_delivery"] != null),
        OrderStage("delivered", orderStages["delivered"] as? Long, "Delivered", orderStages["delivered"] != null)
    )
}

fun getOrderItems(orderData: Map<String, Any>): List<Map<String, Any>> {
    val items = mutableListOf<Map<String, Any>>()

    // Check for cart items
    val cartItems = orderData["items"] as? List<Map<String, Any>>
    cartItems?.let { items.addAll(it) }

    // Check for single product orders
    val orderedItems = orderData["orderedItems"] as? List<Map<String, Any>>
    orderedItems?.let { items.addAll(it) }

    return items
}

fun String.capitalizeWords(): String = split(" ").joinToString(" ") {
    it.replaceFirstChar { char ->
        if (char.isLowerCase()) char.titlecase(Locale.getDefault())
        else char.toString()
    }
}

fun formatDate(timestamp: Long): String {
    return SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(timestamp))
}