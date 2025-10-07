package com.example.shopease.screens.checkout


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shopease.components.BottomBar
import com.example.shopease.components.TopBar
import com.example.shopease.viewmodels.CheckoutViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ShippingInfoScreen(navController: NavController, viewModel: CheckoutViewModel) {

    val user = FirebaseAuth.getInstance().currentUser
    LaunchedEffect(user?.uid) {
        user?.uid?.let { viewModel.fetchLastOrderDetails(it) }
        viewModel.totalAmount = viewModel.originalAmount
    }

    val isFormValid = viewModel.name.isNotBlank() &&
            viewModel.address.isNotBlank() &&
            viewModel.city.isNotBlank() &&
            viewModel.state.isNotBlank() &&
            viewModel.pincode.isNotBlank() &&
            viewModel.phone.isNotBlank()

    Scaffold(
        topBar = {
            TopBar(
                navController = navController,
                isSearching = false,
                searchQuery = "",
                onSearchClick = {},
                onSearchChange = {}
            )
        },
        bottomBar = { BottomBar(navController) }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFFFF0F5), Color(0xFFE3F2FD))
                    )
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            // Title Card
            Text(
                text = "Shipping Details",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFEC407A),
                modifier = Modifier
                    .background(
                        Color.White.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Input Field Helper
            @Composable
            fun customTextField(value: String, label: String, onValueChange: (String) -> Unit) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = { Text(label, color = Color.Gray) },
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .shadow(2.dp, RoundedCornerShape(12.dp))
                )
            }

            customTextField(viewModel.name, "Full Name") { viewModel.name = it }
            customTextField(viewModel.address, "Street Address") { viewModel.address = it }
            customTextField(viewModel.city, "City") { viewModel.city = it }
            customTextField(viewModel.state, "State") { viewModel.state = it }
            customTextField(viewModel.pincode, "Pincode") { viewModel.pincode = it }
            customTextField(viewModel.phone, "Phone Number") { viewModel.phone = it }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    if (isFormValid) {
                        // Check if current input differs from the last saved user info
                        user?.uid?.let { uid ->
                            if (viewModel.hasAddressChanged()) {
                                viewModel.updateUserAddressInFirebase(uid)
                            }
                        }
                        navController.navigate("payment_options")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEC8D8)),
                shape = RoundedCornerShape(16.dp),
                enabled = isFormValid
            ) {
                Text(
                    "Continue to Payment",
                    color = Color(0xFFEC407A),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }

        }
    }
}
