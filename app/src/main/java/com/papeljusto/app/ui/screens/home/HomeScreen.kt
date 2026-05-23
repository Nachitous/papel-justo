package com.papeljusto.app.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.papeljusto.app.ui.components.ProductCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    factory: HomeViewModel.Factory,
    onAgregarProducto: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = factory)
)
{
    val rankeados by viewModel.productosRankeados.collectAsState()
    var mostrarConfirmacionBorrar by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Papel Justo",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    if (rankeados.isNotEmpty())
                    {
                        IconButton(onClick = { mostrarConfirmacionBorrar = true })
                        {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Borrar todo"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAgregarProducto,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Agregar producto", style = MaterialTheme.typography.labelLarge) }
            )
        }
    )
    { paddingValues ->

        if (rankeados.isEmpty())
        {
            EstadoVacio(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        }
        else
        {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = paddingValues.calculateTopPadding() + 16.dp,
                    bottom = 120.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            )
            {
                items(rankeados, key = { it.product.id })
                { rankeado ->
                    ProductCard(
                        product = rankeado.product,
                        posicion = rankeado.posicion,
                        esMejorCompra = rankeado.esMejorCompra,
                        onDelete = { viewModel.eliminarProducto(rankeado.product) }
                    )
                }
            }
        }
    }

    if (mostrarConfirmacionBorrar)
    {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacionBorrar = false },
            title = { Text("¿Borrar todo?") },
            text = { Text("Se eliminarán todos los productos de la lista.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.limpiarTodo()
                    mostrarConfirmacionBorrar = false
                })
                {
                    Text("Borrar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmacionBorrar = false })
                {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun EstadoVacio(modifier: Modifier = Modifier)
{
    Box(modifier = modifier, contentAlignment = Alignment.Center)
    {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp))
        {
            Text(text = "🧻", style = MaterialTheme.typography.displayLarge)
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Ningún producto todavía",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Agregá productos para comparar cuál conviene más.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
    }
}
