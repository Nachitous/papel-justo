package com.papeljusto.app.data.scanner

import android.graphics.Bitmap
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.papeljusto.app.domain.model.PlyType
import com.papeljusto.app.domain.model.ScannedProductData
import com.papeljusto.app.domain.model.ScanOrigen
import com.papeljusto.app.domain.scanner.ScannerDataSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

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

    private suspend fun tryBarcode(image: InputImage): ScannedProductData? =
        suspendCancellableCoroutine { cont ->
            barcodeScanner.process(image)
                .addOnSuccessListener { barcodes ->
                    val barcode = barcodes.firstOrNull { b -> b.valueType == Barcode.TYPE_PRODUCT }
                    val rawValue = barcode?.rawValue
                    cont.resume(
                        if (rawValue != null) ScannedProductData(marca = rawValue, origen = ScanOrigen.BARCODE)
                        else null
                    )
                }
                .addOnFailureListener { cont.resume(null) }
        }

    private suspend fun tryOcr(image: InputImage): ScannedProductData? =
        suspendCancellableCoroutine { cont ->
            textRecognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val text = visionText.text.lowercase()
                    cont.resume(
                        if (text.isBlank()) null
                        else ScannedProductData(
                            marca = extractMarca(visionText),
                            precio = extractPrecio(text),
                            cantidadRollos = extractRollos(text),
                            metrosPorRollo = extractMetros(text),
                            cantidadHojas = extractHojas(text),
                            plyType = extractPlyType(text),
                            origen = ScanOrigen.OCR
                        )
                    )
                }
                .addOnFailureListener { cont.resume(null) }
        }

    private fun extractMarca(visionText: Text): String?
    {
        val firstLine = visionText.textBlocks
            .flatMap { it.lines }
            .firstOrNull { it.text.trim().length >= 3 }
        return firstLine?.text?.trim()?.take(40)
    }

    private fun extractPrecio(text: String): Double?
    {
        val patterns = listOf(
            Regex("""\$\s*(\d[\d.,]*)"""),
            Regex("""precio[:\s]+\$?\s*(\d[\d.,]*)""")
        )
        for (pattern in patterns)
        {
            val match = pattern.find(text) ?: continue
            val raw = match.groupValues[1].replace(".", "").replace(",", "")
            val value = raw.toDoubleOrNull() ?: continue
            if (value > 0) return value
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
        val pattern = Regex("""(\d+(?:[.,]\d+)?)\s*m\b""")
        val match = pattern.find(text) ?: return null
        val value = match.groupValues[1].replace(",", ".").toDoubleOrNull() ?: return null
        return if (value in 5.0..100.0) value else null
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
            "triple" in text || "3 capas" in text -> PlyType.TRIPLE
            "doble" in text || "2 capas" in text -> PlyType.DOBLE
            "simple" in text || "1 capa" in text -> PlyType.SIMPLE
            else -> null
        }
    }
}
