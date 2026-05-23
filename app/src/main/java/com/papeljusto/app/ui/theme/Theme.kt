package com.papeljusto.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = AzulPrimario,
    secondary = AzulClaro,
    background = GrisFondo,
    surface = BlancoTarjeta,
    onPrimary = BlancoTarjeta,
    onBackground = TextoPrincipal,
    onSurface = TextoPrincipal
)

private val DarkColors = darkColorScheme(
    primary = AzulClaro,
    secondary = AzulPrimario,
    background = TextoPrincipal,
    surface = TextoSecundario,
    onPrimary = TextoPrincipal,
    onBackground = BlancoTarjeta,
    onSurface = BlancoTarjeta
)

@Composable
fun PapelJustoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
)
{
    val colorScheme = when
    {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
        {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
