package com.papeljusto.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Long = 0,
    val marca: String,
    val precio: Double,
    val cantidadRollos: Int,
    val metrosPorRollo: Double? = null,
    val cantidadHojas: Int? = null,
    val anchoCm: Double? = null,
    val largoCm: Double? = null,
    val plyType: PlyType = PlyType.DOBLE,
    val costoPorM2: Double = 0.0,
    val confianza: ConfidenceLevel = ConfidenceLevel.MEDIA,
    val usaEstimaciones: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class ProductoRankeado(
    val product: Product,
    val posicion: Int,
    val esMejorCompra: Boolean
)
