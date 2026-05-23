package com.papeljusto.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.papeljusto.app.ui.navigation.AppNavigation
import com.papeljusto.app.ui.theme.PapelJustoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PapelJustoTheme {
                AppNavigation()
            }
        }
    }
}
