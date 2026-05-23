package com.papeljusto.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.papeljusto.app.data.repository.ProductRepository
import com.papeljusto.app.domain.model.Product
import com.papeljusto.app.domain.model.ProductoRankeado
import com.papeljusto.app.domain.usecase.CompareProductsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: ProductRepository,
    private val compareProducts: CompareProductsUseCase
) : ViewModel()
{
    val productosRankeados: StateFlow<List<ProductoRankeado>> = repository
        .getAllProducts()
        .map { lista -> compareProducts.execute(lista) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun eliminarProducto(product: Product)
    {
        viewModelScope.launch { repository.deleteProduct(product) }
    }

    fun limpiarTodo()
    {
        viewModelScope.launch { repository.deleteAll() }
    }

    class Factory(
        private val repository: ProductRepository,
        private val compareProducts: CompareProductsUseCase
    ) : ViewModelProvider.Factory
    {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            HomeViewModel(repository, compareProducts) as T
    }
}
