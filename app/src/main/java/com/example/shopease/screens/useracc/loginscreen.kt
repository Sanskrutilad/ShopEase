package com.example.shopease.screens.useracc

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shopease.Product
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(
    product: Product?,
    navController : NavController
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    Scaffold(modifier = Modifier.fillMaxSize().background(
        Brush.verticalGradient(
            listOf(Color(0xFFFFF0F5), Color(0xFFE3F2FD))
        )
    )){
            innerpadiing->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Login", fontSize = 35.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF5759A))
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = email,
                onValueChange = { email = it },
                label = { Text("Email",color= Color(0xFFF5759A)) },
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
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password",color = Color(0xFFF5759A)) },
                visualTransformation = PasswordVisualTransformation(),
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
                    FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            if (product != null && product.name.isNotBlank()) {
                                navController.navigate("product_detail/${product.productId}")
                                {
                                    popUpTo("login?productId={productId}") { inclusive = true }
                                }
                            } else {
                                navController.navigate("homescreen") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        }
                        .addOnFailureListener {
                            errorMessage = it.message
                        }
                },
                colors = ButtonDefaults.buttonColors(Color(0xFFF5759A)),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 2.dp
                )
            ) {
                Text("Login",fontSize = 18.sp)
            }


            errorMessage?.let {
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = it, color = Color.Red, fontSize = 14.sp)
            }

            TextButton(onClick = { navController.navigate("signup?productId={productId}") }) {
                Text("Don't have an account? Sign up",color = Color(0xFFA4284D), fontSize = 16.sp)
            }
            TextButton(onClick = { FirebaseAuth.getInstance().signOut()
                navController.navigate("homescreen") }) {
                Text("Skip for now", color = Color(0xFFFA9EB9),fontSize = 18.sp)
            }
        }
    }

}


