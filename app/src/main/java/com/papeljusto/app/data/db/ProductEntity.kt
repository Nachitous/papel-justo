package com.papeljusto.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.papeljusto.app.domain.model.ConfidenceLevel
import com.papeljusto.app.domain.model.PlyType
import com.papeljusto.app.domain.model.Product

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val marca: String,
    val precio: Double,
    val cantidadRollos: Int,
    val metrosPorRollo: Double?,
    val cantidadHojas: Int?,
    val anchoCm: Double?,
    val largoCm: Double?,
    val plyType: String,
    val costoPorM2: Double,
    val confianza: String,
    val usaEstimaciones: Boolean,
    val timestamp: Long
)

fun ProductEntity.toDomain() = Product(
    id = id,
    marca = marca,
    precio = precio,
    cantidadRollos = cantidadRollos,
    metrosPorRollo = metrosPorRollo,
    cantidadHojas = cantidadHojas,
    anchoCm = anchoCm,
    largoCm = largoCm,
    plyType = PlyType.valueOf(plyType),
    costoPorM2 = costoPorM2,
    confianza = ConfidenceLevel.valueOf(confianza),
    usaEstimaciones = usaEstimaciones,
    timestamp = timestamp
)

fun Product.toEntity() = ProductEntity(
    id = id,
    marca = marca,
    precio = precio,
    cantidadRollos = cantidadRollos,
    metrosPorRollo = metrosPorRollo,
    cantidadHojas = cantidadHojas,
    anchoCm = anchoCm,
    largoCm = largoCm,
    plyType = plyType.name,
    costoPorM2 = costoPorM2,
    confianza = confianza.name,
    usaEstimaciones = usaEstimaciones,
    timestamp = timestamp
)
