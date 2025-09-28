package com.example.shopease

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.shopease.ui.theme.ShopEaseTheme
import com.example.shopease.viewmodels.CheckoutViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.razorpay.PaymentResultListener

class MainActivity : ComponentActivity(), PaymentResultListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        createNotificationChannel()
        requestNotificationPermission()
        listenForNewBanners(applicationContext)
        enableEdgeToEdge()
        setContent {
            ShopEaseTheme {
                val navController = rememberNavController()

                Scaffold(containerColor = Color(0xFF80BCC5)) { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        AppNavigation(navController)
                    }
                }
            }
        }
    }
    override fun onPaymentSuccess(razorpayPaymentID: String?) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val vm = OrderSession.checkoutViewModel
        val ctx = OrderSession.context
        val nav = OrderSession.navController

        val baseOrderData = hashMapOf<String, Any?>(
            "name" to vm.name,
            "phone" to vm.phone,
            "address" to vm.address,
            "city" to vm.city,
            "state" to vm.state,
            "pincode" to vm.pincode,
            "totalAmount" to vm.totalAmount,
            "timestamp" to System.currentTimeMillis(),
            "paymentId" to razorpayPaymentID,
            "orderStatus" to "confirmed",
            "trackingId" to generateTrackingId(),
            "orderStages" to hashMapOf(
                "confirmed" to System.currentTimeMillis(),
                "packed" to null,
                "shipped" to null,
                "out_for_delivery" to null,
                "delivered" to null
            )
        )

        // Add coupon if applied
        vm.selectedCoupon?.let {
            baseOrderData["coupon"] = hashMapOf(
                "code" to it.code,
                "discount" to it.discount,
                "id" to it.id
            )
        }

        // Handle Buy Now orders
        if (vm.orderedItems.isNotEmpty()) {
            baseOrderData["orderedItems"] = vm.orderedItems.map { product ->
                hashMapOf(
                    "name" to product.name,
                    "price" to product.price,
                    "description" to product.description,
                    "imageUrl" to product.imageUrls,
                    "ebookUrl" to product.ebookUrl
                )
            }
        }

        // Handle Cart orders
        if (vm.cartOrderedItems.isNotEmpty()) {
            baseOrderData["items"] = vm.cartOrderedItems.map { item ->
                hashMapOf(
                    "name" to item.name,
                    "price" to item.price,
                    "quantity" to item.quantity,
                    "imageUrl" to item.imageUrl,
                    "ebookUrl" to item.ebookUrl
                )
            }
        }

        // Push order to Firebase
        val database = FirebaseDatabase.getInstance().reference
        val orderRef = database.child("orders").child(userId).push()
        val orderId = orderRef.key

        baseOrderData["orderId"] = orderId

        orderRef.setValue(baseOrderData)
            .addOnSuccessListener {
                if (vm.cartOrderedItems.isNotEmpty()) {
                    database.child("carts").child(userId).removeValue()
                }
                sendOrderNotification(ctx)
                Toast.makeText(ctx, "Order saved!", Toast.LENGTH_SHORT).show()

                // Navigate to order tracking with order ID
                nav.navigate("order_confirmation")
            }
            .addOnFailureListener {
                Toast.makeText(ctx, "Failed to save order", Toast.LENGTH_SHORT).show()
            }
    }

    private fun generateTrackingId(): String {
        val timestamp = System.currentTimeMillis()
        val random = (1000..9999).random()
        return "TRK${timestamp}${random}"
    }


    override fun onPaymentError(code: Int, response: String?) {
        Toast.makeText(this, "Payment Failed: $response", Toast.LENGTH_LONG).show()
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val orderChannel = NotificationChannel(
                "order_channel",
                "Order Confirmations",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Order placement updates" }

            val bannerChannel = NotificationChannel(
                "banner_channel",
                "New Banners",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "New banners and promotions" }

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(orderChannel)
            manager.createNotificationChannel(bannerChannel)
        }
    }



    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }
    }

    fun listenForNewBanners(context: Context) {
        val dbRef = FirebaseDatabase.getInstance().getReference("banners")

        dbRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val imageUrl = snapshot.child("link").getValue(String::class.java)
                if (!imageUrl.isNullOrEmpty()) {
                    sendBannerNotification(context, imageUrl)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }


}

object OrderSession {
    lateinit var checkoutViewModel: CheckoutViewModel
    lateinit var context: Context
    lateinit var navController: NavController
}
