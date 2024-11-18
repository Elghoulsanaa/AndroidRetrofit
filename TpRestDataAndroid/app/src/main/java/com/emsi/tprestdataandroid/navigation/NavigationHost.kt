package com.emsi.tprestdataandroid

import MainViewModel
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.emsi.tprestdataandroid.screens.AccountListScreen
import com.emsi.tprestdataandroid.screens.CreateAccountScreen
import com.emsi.tprestdataandroid.screens.UpdateAccountScreen

@Composable
fun NavigationHost(viewModel: MainViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController, mainViewModel = viewModel) }
        composable("accounts") {
            AccountListScreen(
                viewModel = viewModel,
                navController = navController
            )
        }

        composable("create_account") { CreateAccountScreen(viewModel = viewModel) }

        composable("update_account/{compteId}") { backStackEntry ->
            val compteId = backStackEntry.arguments?.getString("compteId")?.toLongOrNull()
            if (compteId != null) {
                UpdateAccountScreen(viewModel = viewModel, compteId = compteId)
            }
        }
    }
}
