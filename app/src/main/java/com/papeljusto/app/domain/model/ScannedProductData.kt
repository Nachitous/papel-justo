package com.papeljusto.app.domain.model

data class ScannedProductData(
    val marca: String? = null,
    val precio: Double? = null,
    val cantidadRollos: Int? = null,
    val metrosPorRollo: Double? = null,
    val cantidadHojas: Int? = null,
    val anchoCm: Double? = null,
    val largoCm: Double? = null,
    val plyType: PlyType? = null,
    val origen: ScanOrigen = ScanOrigen.OCR
)

enum class ScanOrigen { BARCODE, OCR }
