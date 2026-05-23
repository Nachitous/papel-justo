package com.papeljusto.app.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.papeljusto.app.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "products")
private val PRODUCTS_KEY = stringPreferencesKey("products_json")
private val productListSerializer = ListSerializer(Product.serializer())

class ProductRepository(private val context: Context)
{
    private val json = Json { ignoreUnknownKeys = true }

    fun getAllProducts(): Flow<List<Product>> = context.dataStore.data.map { prefs ->
        val raw = prefs[PRODUCTS_KEY] ?: return@map emptyList()
        runCatching { json.decodeFromString(productListSerializer, raw) }.getOrDefault(emptyList())
    }

    suspend fun saveProduct(product: Product)
    {
        context.dataStore.edit { prefs ->
            val current = currentList(prefs)
            val nextId = (current.maxOfOrNull { it.id } ?: 0L) + 1
            val updated = current + product.copy(id = nextId)
            prefs[PRODUCTS_KEY] = json.encodeToString(productListSerializer, updated)
        }
    }

    suspend fun deleteProduct(product: Product)
    {
        context.dataStore.edit { prefs ->
            val updated = currentList(prefs).filter { it.id != product.id }
            prefs[PRODUCTS_KEY] = json.encodeToString(productListSerializer, updated)
        }
    }

    suspend fun deleteAll()
    {
        context.dataStore.edit { prefs ->
            prefs[PRODUCTS_KEY] = "[]"
        }
    }

    private fun currentList(prefs: Preferences): List<Product>
    {
        val raw = prefs[PRODUCTS_KEY] ?: return emptyList()
        return runCatching { json.decodeFromString(productListSerializer, raw) }.getOrDefault(emptyList())
    }
}
