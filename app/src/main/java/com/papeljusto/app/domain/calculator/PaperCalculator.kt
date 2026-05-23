package com.papeljusto.app.domain.calculator

import com.papeljusto.app.domain.model.ConfidenceLevel
import com.papeljusto.app.domain.model.PlyType
import com.papeljusto.app.domain.model.Product

object PaperCalculator {

    private const val ANCHO_ESTANDAR_M = 0.10
    private const val ANCHO_PREMIUM_M = 0.105
    private const val LARGO_HOJA_ESTANDAR_M = 0.12

    data class CalculationResult(
        val costoPorM2: Double,
        val confianza: ConfidenceLevel,
        val usaEstimaciones: Boolean
    )

    fun calcular(product: Product): CalculationResult
    {
        var datosCompletos = 0
        var datosUsados = 0

        val metrosPorRollo = resolverMetrosPorRollo(product).also { (valor, estimado) ->
            if (!estimado) datosCompletos++ else datosUsados++
        }.first

        val anchoCm = resolverAncho(product).also { (_, estimado) ->
            if (!estimado) datosCompletos++ else datosUsados++
        }

        val anchoM = anchoCm.first / 100.0
        val plyCfactor = product.plyType.factor

        val areaTotal = product.cantidadRollos * metrosPorRollo * anchoM * plyCfactor
        val costoPorM2 = if (areaTotal > 0) product.precio / areaTotal else Double.MAX_VALUE

        val confianza = when
        {
            datosUsados == 0 -> ConfidenceLevel.ALTA
            datosUsados == 1 -> ConfidenceLevel.MEDIA
            else -> ConfidenceLevel.BAJA
        }

        return CalculationResult(
            costoPorM2 = costoPorM2,
            confianza = confianza,
            usaEstimaciones = datosUsados > 0
        )
    }

    private fun resolverMetrosPorRollo(product: Product): Pair<Double, Boolean>
    {
        product.metrosPorRollo?.let { return Pair(it, false) }

        product.cantidadHojas?.let { hojas ->
            val largoHojaM = if (product.largoCm != null) product.largoCm / 100.0 else LARGO_HOJA_ESTANDAR_M
            return Pair(hojas * largoHojaM, product.largoCm == null)
        }

        return Pair(20.0, true)
    }

    private fun resolverAncho(product: Product): Pair<Double, Boolean>
    {
        product.anchoCm?.let { return Pair(it, false) }
        val anchoEstimado = when (product.plyType)
        {
            PlyType.TRIPLE -> ANCHO_PREMIUM_M * 100
            else -> ANCHO_ESTANDAR_M * 100
        }
        return Pair(anchoEstimado, true)
    }
}
