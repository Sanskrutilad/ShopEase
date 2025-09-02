package com.example.shopease

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.shopease.screens.CartScreen
import com.example.shopease.screens.HomeScreen
import com.example.shopease.screens.ProductDetailScreen
import com.example.shopease.screens.ProductListScreen
import com.example.shopease.screens.SplashScreen
import com.example.shopease.screens.checkout.OrderConfirmationScreen
import com.example.shopease.screens.checkout.PaymentOptionsScreen
import com.example.shopease.screens.checkout.ShippingInfoScreen
import com.example.shopease.screens.checkout.SingleOrderScreen
import com.example.shopease.screens.useracc.LoginScreen
import com.example.shopease.screens.useracc.ProfileScreen
import com.example.shopease.screens.useracc.SignupScreen
import com.example.shopease.viewmodels.CartViewModel
import com.example.shopease.viewmodels.CheckoutViewModel
import com.example.shopease.viewmodels.ProductViewModel


@Composable
fun AppNavigation(navController: NavHostController) {
    val checkoutViewModel: CheckoutViewModel = viewModel()
    val cartViewModel : CartViewModel = viewModel ()
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {

        composable("splash") {
            SplashScreen(navController)
        }
        composable(
            route = "signup?productId={productId}",
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            val productViewModel: ProductViewModel = viewModel()
            val product = productId?.let { productViewModel.getProductById(it) }

            SignupScreen(navController = navController, product = product)
        }


        composable("homescreen") {
            HomeScreen(navController)
        }
        composable("cartscreen") {
            CartScreen(navController, viewModel = cartViewModel,checkoutViewModel = checkoutViewModel)
        }
        composable("product_detail/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            val productViewModel: ProductViewModel = viewModel()

            val product = productViewModel.getProductById(productId ?: "")

            if (product != null) {
                val checkoutViewModel: CheckoutViewModel = viewModel()
                ProductDetailScreen(navController, product, checkoutViewModel)
            } else {
                Text("Loading product...", modifier = Modifier.padding(16.dp))
            }
        }

        composable("login") {
            LoginScreen(
                navController = navController,
                product = null
            )
        }
        composable("signup") {
            SignupScreen(
                navController = navController,
                product = null
            )
        }

        composable(
            route = "category/{categoryName}"
        ) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
            ProductListScreen(navController, categoryName)
        }
        composable(
            "login?productId={productId}",
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            val productViewModel: ProductViewModel = viewModel()
            val product = productId?.let { productViewModel.getProductById(it) }

            LoginScreen(
                product = product,
                navController = navController
            )
        }

        composable("profile"){
            ProfileScreen(navController)
        }
        composable("shipping_info") { ShippingInfoScreen(navController,checkoutViewModel) }
        composable("payment_options") { PaymentOptionsScreen(navController,checkoutViewModel) }
        composable("order_confirmation") { OrderConfirmationScreen(navController,checkoutViewModel) }
        composable(
            route = "single_order?productId={productId}",
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            val productViewModel: ProductViewModel = viewModel()

            val product = productViewModel.getProductById(productId)

            if (product != null) {
                SingleOrderScreen(navController = navController, product = product, checkoutViewModel = checkoutViewModel)
            } else {
                Text("Loading product...", modifier = Modifier.padding(16.dp))
            }
        }
    }
}
