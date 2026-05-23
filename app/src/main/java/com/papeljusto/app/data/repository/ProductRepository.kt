package com.papeljusto.app.data.repository

import com.papeljusto.app.data.db.ProductDao
import com.papeljusto.app.data.db.toDomain
import com.papeljusto.app.data.db.toEntity
import com.papeljusto.app.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductRepository @Inject constructor(private val dao: ProductDao)
{
    fun getAllProducts(): Flow<List<Product>> = dao.getAllProducts().map { list ->
        list.map { it.toDomain() }
    }

    suspend fun saveProduct(product: Product): Long = dao.insertProduct(product.toEntity())

    suspend fun deleteProduct(product: Product) = dao.deleteProduct(product.toEntity())

    suspend fun deleteAll() = dao.deleteAll()
}
