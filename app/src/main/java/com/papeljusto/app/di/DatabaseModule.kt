package com.papeljusto.app.di

import android.content.Context
import androidx.room.Room
import com.papeljusto.app.data.db.AppDatabase
import com.papeljusto.app.data.db.ProductDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule
{
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "papel_justo_db").build()

    @Provides
    fun provideProductDao(db: AppDatabase): ProductDao = db.productDao()
}
