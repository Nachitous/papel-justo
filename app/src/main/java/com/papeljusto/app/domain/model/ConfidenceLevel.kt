package com.papeljusto.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class ConfidenceLevel(val label: String) {
    ALTA("Alta"),
    MEDIA("Media"),
    BAJA("Baja")
}
