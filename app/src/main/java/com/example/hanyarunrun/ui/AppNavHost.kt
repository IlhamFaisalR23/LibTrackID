package com.example.hanyarunrun.ui

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hanyarunrun.viewmodel.DataViewModel

@Composable
fun AppNavHost(viewModel: DataViewModel) {
    val navController = rememberNavController()
    val activity = LocalContext.current as? Activity

    NavHost(navController = navController, startDestination = "list") {
        composable("form") {
            DataEntryScreen(navController = navController, viewModel = viewModel)
        }
        composable("list") {
            DataListScreen(navController = navController, viewModel = viewModel)
        }
        composable(
            route = "edit/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            EditScreen(navController = navController, viewModel = viewModel, dataId = id)
        }
        composable(
            route = "delete/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            DataListScreen(navController = navController, viewModel = viewModel)
        }
        composable("exit") {
            activity?.finish()
        }
    }
}
