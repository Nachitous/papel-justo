package com.papeljusto.app.domain

import com.papeljusto.app.domain.calculator.PaperCalculator
import com.papeljusto.app.domain.model.ConfidenceLevel
import com.papeljusto.app.domain.model.PlyType
import com.papeljusto.app.domain.model.Product
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PaperCalculatorTest
{
    private fun producto(
        precio: Double = 1000.0,
        rollos: Int = 4,
        metros: Double? = null,
        hojas: Int? = null,
        ancho: Double? = null,
        largo: Double? = null,
        ply: PlyType = PlyType.DOBLE
    ) = Product(
        marca = "Test",
        precio = precio,
        cantidadRollos = rollos,
        metrosPorRollo = metros,
        cantidadHojas = hojas,
        anchoCm = ancho,
        largoCm = largo,
        plyType = ply
    )

    @Test
    fun `datos completos calcula correctamente`()
    {
        val p = producto(precio = 5000.0, rollos = 4, metros = 30.0, ancho = 10.0, ply = PlyType.SIMPLE)
        val result = PaperCalculator.calcular(p)

        // area = 4 * 30 * 0.10 * 1.0 = 12 m²
        // costo = 5000 / 12 ≈ 416.67
        assertEquals(416.67, result.costoPorM2, 0.5)
        assertEquals(ConfidenceLevel.ALTA, result.confianza)
        assertFalse(result.usaEstimaciones)
    }

    @Test
    fun `sin metros usa estimacion y baja confianza`()
    {
        val p = producto(precio = 1000.0, rollos = 4)
        val result = PaperCalculator.calcular(p)

        assertTrue(result.usaEstimaciones)
        assertTrue(result.costoPorM2 > 0)
    }

    @Test
    fun `doble hoja tiene mayor area que simple`()
    {
        val simple = producto(precio = 1000.0, rollos = 4, metros = 20.0, ancho = 10.0, ply = PlyType.SIMPLE)
        val doble = producto(precio = 1000.0, rollos = 4, metros = 20.0, ancho = 10.0, ply = PlyType.DOBLE)

        val costoSimple = PaperCalculator.calcular(simple).costoPorM2
        val costoDoble = PaperCalculator.calcular(doble).costoPorM2

        assertTrue("Doble hoja debe tener menor costo/m² por mayor área efectiva", costoDoble < costoSimple)
    }

    @Test
    fun `triple hoja es mas economica que simple en costo por area`()
    {
        val simple = producto(precio = 1000.0, rollos = 4, metros = 20.0, ancho = 10.0, ply = PlyType.SIMPLE)
        val triple = producto(precio = 1000.0, rollos = 4, metros = 20.0, ancho = 10.0, ply = PlyType.TRIPLE)

        val costoSimple = PaperCalculator.calcular(simple).costoPorM2
        val costoTriple = PaperCalculator.calcular(triple).costoPorM2

        assertTrue(costoTriple < costoSimple)
    }

    @Test
    fun `confianza alta con todos los datos`()
    {
        val p = producto(metros = 25.0, ancho = 10.0)
        val result = PaperCalculator.calcular(p)
        assertEquals(ConfidenceLevel.ALTA, result.confianza)
    }

    @Test
    fun `confianza media con un dato faltante`()
    {
        val p = producto(metros = 25.0) // falta ancho
        val result = PaperCalculator.calcular(p)
        assertEquals(ConfidenceLevel.MEDIA, result.confianza)
    }

    @Test
    fun `confianza baja con multiples datos faltantes`()
    {
        val p = producto() // faltan metros y ancho
        val result = PaperCalculator.calcular(p)
        assertEquals(ConfidenceLevel.BAJA, result.confianza)
    }

    @Test
    fun `pack mas barato por m2 identificado correctamente`()
    {
        // Pack A: $5000, 4 rollos x 30m x 10cm simple = 12m² → $416/m²
        // Pack B: $7200, 12 rollos x 20m x 10cm simple = 24m² → $300/m² (mejor)
        val packA = producto(precio = 5000.0, rollos = 4, metros = 30.0, ancho = 10.0, ply = PlyType.SIMPLE)
        val packB = producto(precio = 7200.0, rollos = 12, metros = 20.0, ancho = 10.0, ply = PlyType.SIMPLE)

        val costoA = PaperCalculator.calcular(packA).costoPorM2
        val costoB = PaperCalculator.calcular(packB).costoPorM2

        assertTrue("Pack B debería ser más conveniente", costoB < costoA)
    }
}
