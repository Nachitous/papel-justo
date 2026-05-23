package com.papeljusto.app.domain.usecase

import android.graphics.Bitmap
import com.papeljusto.app.domain.model.ScannedProductData
import com.papeljusto.app.domain.scanner.ScannerDataSource

class ScanProductUseCase(private val scanner: ScannerDataSource)
{
    suspend fun execute(bitmap: Bitmap): ScannedProductData? = scanner.scanFromBitmap(bitmap)
}
