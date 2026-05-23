package com.papeljusto.app.domain.usecase

import com.papeljusto.app.domain.calculator.PaperCalculator
import com.papeljusto.app.domain.model.Product
import com.papeljusto.app.domain.model.ProductoRankeado

class CompareProductsUseCase
{
    fun execute(productos: List<Product>): List<ProductoRankeado>
    {
        val calculados = productos.map { p ->
            val result = PaperCalculator.calcular(p)
            p.copy(
                costoPorM2 = result.costoPorM2,
                confianza = result.confianza,
                usaEstimaciones = result.usaEstimaciones
            )
        }

        val ordenados = calculados.sortedBy { it.costoPorM2 }

        return ordenados.mapIndexed { index, producto ->
            ProductoRankeado(
                product = producto,
                posicion = index + 1,
                esMejorCompra = index == 0
            )
        }
    }
}
