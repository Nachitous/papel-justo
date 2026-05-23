package com.papeljusto.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.papeljusto.app.AppContainer
import com.papeljusto.app.ui.screens.addproduct.AddProductScreen
import com.papeljusto.app.ui.screens.addproduct.AddProductViewModel
import com.papeljusto.app.ui.screens.home.HomeScreen
import com.papeljusto.app.ui.screens.home.HomeViewModel

private const val RUTA_HOME = "home"
private const val RUTA_AGREGAR = "agregar"

@Composable
fun AppNavigation(container: AppContainer)
{
    val navController = rememberNavController()

    val homeFactory = HomeViewModel.Factory(container.repository, container.compareProductsUseCase)
    val addFactory = AddProductViewModel.Factory(container.repository)

    NavHost(navController = navController, startDestination = RUTA_HOME)
    {
        composable(RUTA_HOME)
        {
            HomeScreen(
                factory = homeFactory,
                onAgregarProducto = { navController.navigate(RUTA_AGREGAR) }
            )
        }
        composable(RUTA_AGREGAR)
        {
            AddProductScreen(
                factory = addFactory,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
