package com.papeljusto.app

import android.content.Context
import com.papeljusto.app.data.repository.ProductRepository
import com.papeljusto.app.domain.usecase.CompareProductsUseCase

class AppContainer(context: Context)
{
    val repository = ProductRepository(context)
    val compareProductsUseCase = CompareProductsUseCase()
}
