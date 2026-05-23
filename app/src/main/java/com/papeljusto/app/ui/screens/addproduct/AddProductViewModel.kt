package com.papeljusto.app.ui.screens.addproduct

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.papeljusto.app.data.repository.ProductRepository
import com.papeljusto.app.domain.calculator.PaperCalculator
import com.papeljusto.app.domain.model.PlyType
import com.papeljusto.app.domain.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddProductUiState(
    val marca: String = "",
    val precio: String = "",
    val cantidadRollos: String = "",
    val metrosPorRollo: String = "",
    val cantidadHojas: String = "",
    val anchoCm: String = "",
    val largoCm: String = "",
    val plyType: PlyType = PlyType.DOBLE,
    val modoAvanzado: Boolean = false,
    val guardado: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AddProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel()
{
    private val _uiState = MutableStateFlow(AddProductUiState())
    val uiState: StateFlow<AddProductUiState> = _uiState.asStateFlow()

    fun onMarcaChange(value: String) = _uiState.update { it.copy(marca = value, error = null) }
    fun onPrecioChange(value: String) = _uiState.update { it.copy(precio = value, error = null) }
    fun onCantidadRollosChange(value: String) = _uiState.update { it.copy(cantidadRollos = value, error = null) }
    fun onMetrosPorRolloChange(value: String) = _uiState.update { it.copy(metrosPorRollo = value) }
    fun onCantidadHojasChange(value: String) = _uiState.update { it.copy(cantidadHojas = value) }
    fun onAnchoCmChange(value: String) = _uiState.update { it.copy(anchoCm = value) }
    fun onLargoCmChange(value: String) = _uiState.update { it.copy(largoCm = value) }
    fun onPlyTypeChange(value: PlyType) = _uiState.update { it.copy(plyType = value) }
    fun toggleModoAvanzado() = _uiState.update { it.copy(modoAvanzado = !it.modoAvanzado) }

    fun guardar()
    {
        val state = _uiState.value

        if (state.marca.isBlank())
        {
            _uiState.update { it.copy(error = "Ingresá el nombre o marca del producto.") }
            return
        }

        val precio = state.precio.toDoubleOrNull()
        if (precio == null || precio <= 0)
        {
            _uiState.update { it.copy(error = "Ingresá un precio válido.") }
            return
        }

        val rollos = state.cantidadRollos.toIntOrNull()
        if (rollos == null || rollos <= 0)
        {
            _uiState.update { it.copy(error = "Ingresá la cantidad de rollos.") }
            return
        }

        val product = Product(
            marca = state.marca.trim(),
            precio = precio,
            cantidadRollos = rollos,
            metrosPorRollo = state.metrosPorRollo.toDoubleOrNull(),
            cantidadHojas = state.cantidadHojas.toIntOrNull(),
            anchoCm = state.anchoCm.toDoubleOrNull(),
            largoCm = state.largoCm.toDoubleOrNull(),
            plyType = state.plyType
        )

        val result = PaperCalculator.calcular(product)
        val productConCosto = product.copy(
            costoPorM2 = result.costoPorM2,
            confianza = result.confianza,
            usaEstimaciones = result.usaEstimaciones
        )

        viewModelScope.launch {
            repository.saveProduct(productConCosto)
            _uiState.update { it.copy(guardado = true) }
        }
    }
}
