package com.papeljusto.app.data.scanner

import android.graphics.Bitmap
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.papeljusto.app.domain.model.PlyType
import com.papeljusto.app.domain.model.ScannedProductData
import com.papeljusto.app.domain.model.ScanOrigen
import com.papeljusto.app.domain.scanner.ScannerDataSource
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class OcrProductScanner : ScannerDataSource
{
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private val barcodeScanner = BarcodeScanning.getClient()

    override suspend fun scanFromBitmap(bitmap: Bitmap): ScannedProductData?
    {
        val image = InputImage.fromBitmap(bitmap, 0)

        val barcodeResult = tryBarcode(image)
        if (barcodeResult != null) return barcodeResult

        return tryOcr(image)
    }

    private suspend fun tryBarcode(image: InputImage): ScannedProductData?
    {
        val barcodes = barcodeScanner.process(image).await()
        val barcode = barcodes.firstOrNull { it.valueType == Barcode.TYPE_PRODUCT } ?: return null
        val rawValue = barcode.rawValue ?: return null
        return ScannedProductData(marca = rawValue, origen = ScanOrigen.BARCODE)
    }

    private suspend fun tryOcr(image: InputImage): ScannedProductData?
    {
        val result = textRecognizer.process(image).await()
        val text = result.text.lowercase()
        if (text.isBlank()) return null

        return ScannedProductData(
            marca = extractMarca(result.text),
            precio = extractPrecio(text),
            cantidadRollos = extractRollos(text),
            metrosPorRollo = extractMetros(text),
            cantidadHojas = extractHojas(text),
            plyType = extractPlyType(text),
            origen = ScanOrigen.OCR
        )
    }

    private fun extractMarca(raw: String): String?
    {
        val firstLine = raw.lines().firstOrNull { it.trim().length >= 3 } ?: return null
        return firstLine.trim().take(40)
    }

    private fun extractPrecio(text: String): Double?
    {
        val patterns = listOf(
            Regex("""\$\s*(\d[\d.,]*)"""),
            Regex("""precio[:\s]+\$?\s*(\d[\d.,]*)"""),
            Regex("""(\d{3,6})[,.]?(\d{2})?\s*(?:ars|pesos)?""")
        )
        for (pattern in patterns)
        {
            val match = pattern.find(text) ?: continue
            val raw = match.groupValues[1].replace(",", "").replace(".", "")
            return raw.toDoubleOrNull()
        }
        return null
    }

    private fun extractRollos(text: String): Int?
    {
        val pattern = Regex("""(\d{1,3})\s*(?:rollos|unidades|rollo)""")
        return pattern.find(text)?.groupValues?.get(1)?.toIntOrNull()
    }

    private fun extractMetros(text: String): Double?
    {
        val patterns = listOf(
            Regex("""(\d+(?:[.,]\d+)?)\s*(?:m|metros)\s*(?:por\s*rollo|c/u|cada)?"""),
            Regex("""(\d+(?:[.,]\d+)?)\s*m\b""")
        )
        for (pattern in patterns)
        {
            val match = pattern.find(text) ?: continue
            val raw = match.groupValues[1].replace(",", ".")
            val value = raw.toDoubleOrNull() ?: continue
            if (value in 5.0..100.0) return value
        }
        return null
    }

    private fun extractHojas(text: String): Int?
    {
        val pattern = Regex("""(\d{2,4})\s*(?:hojas|hjs)""")
        return pattern.find(text)?.groupValues?.get(1)?.toIntOrNull()
    }

    private fun extractPlyType(text: String): PlyType?
    {
        return when
        {
            "triple" in text || "3 capas" in text || "3 hojas" in text -> PlyType.TRIPLE
            "doble" in text || "2 capas" in text || "2 hojas" in text -> PlyType.DOBLE
            "simple" in text || "1 capa" in text -> PlyType.SIMPLE
            else -> null
        }
    }
}
