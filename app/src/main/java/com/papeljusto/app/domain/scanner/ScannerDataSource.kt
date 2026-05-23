package com.papeljusto.app.domain.scanner

import android.graphics.Bitmap
import com.papeljusto.app.domain.model.ScannedProductData

interface ScannerDataSource
{
    suspend fun scanFromBitmap(bitmap: Bitmap): ScannedProductData?
}
