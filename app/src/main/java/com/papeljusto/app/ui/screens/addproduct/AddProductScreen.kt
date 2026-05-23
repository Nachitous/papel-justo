package com.papeljusto.app.ui.screens.addproduct

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.papeljusto.app.domain.model.PlyType
import com.papeljusto.app.ui.scanner.ScannerOverlay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    factory: AddProductViewModel.Factory,
    onNavigateBack: () -> Unit,
    viewModel: AddProductViewModel = viewModel(factory = factory)
)
{
    val state by viewModel.uiState.collectAsState()
    var mostrarScanner by remember { mutableStateOf(false) }

    LaunchedEffect(state.guardado)
    {
        if (state.guardado) onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar producto", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack)
                    {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    )
    { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        )
        {
            OutlinedButton(
                onClick = { mostrarScanner = true },
                modifier = Modifier.fillMaxWidth()
            )
            {
                Text(
                    text = "Escanear empaque",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            SeccionBasica(state = state, viewModel = viewModel)

            TextButton(
                onClick = viewModel::toggleModoAvanzado,
                modifier = Modifier.align(Alignment.Start)
            )
            {
                Icon(
                    imageVector = if (state.modoAvanzado) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
                Text(
                    text = if (state.modoAvanzado) "Ocultar datos adicionales" else "Agregar datos adicionales",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }

            AnimatedVisibility(
                visible = state.modoAvanzado,
                enter = expandVertically(),
                exit = shrinkVertically()
            )
            {
                SeccionAvanzada(state = state, viewModel = viewModel)
            }

            state.error?.let { mensajeError ->
                Text(
                    text = mensajeError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = viewModel::guardar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = MaterialTheme.shapes.medium
            )
            {
                Text(
                    text = "Guardar y comparar",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (mostrarScanner)
    {
        ScannerOverlay(
            scanUseCase = viewModel.scanUseCase,
            onResult = { data ->
                viewModel.cargarDesdeEscaneo(data)
                mostrarScanner = false
            },
            onDismiss = { mostrarScanner = false }
        )
    }
}

@Composable
private fun SeccionBasica(state: AddProductUiState, viewModel: AddProductViewModel)
{
    Text(
        text = "Datos básicos",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )

    CampoTexto(
        label = "Marca o nombre del producto",
        value = state.marca,
        onValueChange = viewModel::onMarcaChange
    )

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp))
    {
        CampoTexto(
            label = "Precio ($)",
            value = state.precio,
            onValueChange = viewModel::onPrecioChange,
            keyboardType = KeyboardType.Decimal,
            modifier = Modifier.weight(1f)
        )
        CampoTexto(
            label = "Cantidad de rollos",
            value = state.cantidadRollos,
            onValueChange = viewModel::onCantidadRollosChange,
            keyboardType = KeyboardType.Number,
            modifier = Modifier.weight(1f)
        )
    }

    CampoTexto(
        label = "Metros por rollo (ej: 30)",
        value = state.metrosPorRollo,
        onValueChange = viewModel::onMetrosPorRolloChange,
        keyboardType = KeyboardType.Decimal,
        placeholder = "Opcional — se estimará si falta"
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeccionAvanzada(state: AddProductUiState, viewModel: AddProductViewModel)
{
    Column(verticalArrangement = Arrangement.spacedBy(16.dp))
    {
        Text(
            text = "Datos adicionales",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp))
        {
            CampoTexto(
                label = "Hojas por rollo",
                value = state.cantidadHojas,
                onValueChange = viewModel::onCantidadHojasChange,
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f)
            )
            CampoTexto(
                label = "Ancho hoja (cm)",
                value = state.anchoCm,
                onValueChange = viewModel::onAnchoCmChange,
                keyboardType = KeyboardType.Decimal,
                modifier = Modifier.weight(1f)
            )
        }

        CampoTexto(
            label = "Largo de hoja (cm)",
            value = state.largoCm,
            onValueChange = viewModel::onLargoCmChange,
            keyboardType = KeyboardType.Decimal,
            placeholder = "Opcional"
        )

        SelectorPlyType(
            seleccionado = state.plyType,
            onSeleccion = viewModel::onPlyTypeChange
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectorPlyType(seleccionado: PlyType, onSeleccion: (PlyType) -> Unit)
{
    var expandido by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = { expandido = !expandido }
    )
    {
        OutlinedTextField(
            value = seleccionado.label,
            onValueChange = {},
            readOnly = true,
            label = { Text("Tipo de hoja") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false }
        )
        {
            PlyType.entries.forEach { tipo ->
                DropdownMenuItem(
                    text = { Text(tipo.label, style = MaterialTheme.typography.bodyLarge) },
                    onClick = {
                        onSeleccion(tipo)
                        expandido = false
                    }
                )
            }
        }
    }
}

@Composable
private fun CampoTexto(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    placeholder: String? = null
)
{
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = placeholder?.let { { Text(it, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)) } },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        modifier = modifier.fillMaxWidth(),
        textStyle = MaterialTheme.typography.bodyLarge
    )
}
