package com.papeljusto.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.papeljusto.app.ui.navigation.AppNavigation
import com.papeljusto.app.ui.theme.PapelJustoTheme

class MainActivity : ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as PapelJustoApp).container
        setContent {
            PapelJustoTheme {
                AppNavigation(container = container)
            }
        }
    }
}
