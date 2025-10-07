package com.example.shopease.screens.useracc

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
import com.google.firebase.database.FirebaseDatabase

@Composable
fun SignupScreen(
    navController: NavController,
    product: Product? = null
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize().background(
                Brush.verticalGradient(
                    listOf(Color(0xFFFFF0F5), Color(0xFFE3F2FD))
                )
            )
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Sign Up", fontSize = 35.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF5759A))
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Full Name",color = Color(0xFFF5759A)) },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ))
        OutlinedTextField(value = email,
            onValueChange = { email = it },
            label = { Text("Email",color = Color(0xFFF5759A)) },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password",color =Color(0xFFF5759A)) },
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

        Button(onClick = {
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val uid = FirebaseAuth.getInstance().currentUser?.uid
                    val userRef = FirebaseDatabase.getInstance().getReference("users").child(uid ?: "")

                    val userMap = mapOf(
                        "fullName" to fullName,
                        "email" to email
                    )

                    userRef.setValue(userMap).addOnCompleteListener {

                        if (product != null && product.name.isNotBlank() && product.price.isNotBlank()) {
                            navController.navigate("product_detail/${product.productId}")
                            {
                                popUpTo("signup?productId={productId}") { inclusive = true }
                            }
                        } else {
                            navController.navigate("homescreen") {
                                popUpTo("signup") { inclusive = true }
                            }
                        }
                    }

                }
                .addOnFailureListener {
                    errorMessage = it.message
                }
        },colors = ButtonDefaults.buttonColors(Color(0xFFF5759A))) {

            Text("Create Account",fontSize = 16.sp)
        }


        errorMessage?.let {
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = it, color = Color.Red, fontSize = 14.sp)
        }

        TextButton(onClick = { navController.navigate("login?productId={productId}") }) {
            Text("Already have an account? Log in",color = Color(0xFFA4284D),fontSize = 18.sp)
        }
    }
}
