package com.papeljusto.app.domain.model

enum class PlyType(val label: String, val factor: Double) {
    SIMPLE("Hoja simple", 1.0),
    DOBLE("Doble hoja", 1.8),
    TRIPLE("Triple hoja", 2.5)
}
