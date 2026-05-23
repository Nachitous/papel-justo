package com.papeljusto.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.papeljusto.app.ui.screens.addproduct.AddProductScreen
import com.papeljusto.app.ui.screens.home.HomeScreen

private const val RUTA_HOME = "home"
private const val RUTA_AGREGAR = "agregar"

@Composable
fun AppNavigation()
{
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = RUTA_HOME)
    {
        composable(RUTA_HOME)
        {
            HomeScreen(onAgregarProducto = { navController.navigate(RUTA_AGREGAR) })
        }
        composable(RUTA_AGREGAR)
        {
            AddProductScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
