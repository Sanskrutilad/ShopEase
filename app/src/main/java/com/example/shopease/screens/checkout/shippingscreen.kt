package com.example.shopease.screens.checkout


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

    //Log.d("shipping", "originalAmount: ${viewModel.originalAmount}, totalAmount: ${viewModel.totalAmount}")


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
        bottomBar = { BottomBar(navController) },
        containerColor = Color(0xFFCDEFF5)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text("Shipping Details", fontSize = 25.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEC407A))

            Spacer(modifier = Modifier.height(16.dp))

            // Full Name
            OutlinedTextField(
                value = viewModel.name,
                onValueChange = { viewModel.name = it },
                label = { Text("Full Name") },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            // Address
            OutlinedTextField(
                value = viewModel.address,
                onValueChange = { viewModel.address = it },
                label = { Text("Street Address") },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            // City
            OutlinedTextField(
                value = viewModel.city,
                onValueChange = { viewModel.city = it },
                label = { Text("City") },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            // State
            OutlinedTextField(
                value = viewModel.state,
                onValueChange = { viewModel.state = it },
                label = { Text("State") },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            // Pincode
            OutlinedTextField(
                value = viewModel.pincode,
                onValueChange = { viewModel.pincode = it },
                label = { Text("Pincode") },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            // Phone
            OutlinedTextField(
                value = viewModel.phone,
                onValueChange = { viewModel.phone = it },
                label = { Text("Phone Number") },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )


            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (isFormValid) {
                        navController.navigate("payment_options")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEC8D8)),
                enabled = isFormValid
            ) {
                Text("Continue to Payment", color = Color(0xFFEC407A), fontSize = 18.sp)
            }
        }
    }
}



