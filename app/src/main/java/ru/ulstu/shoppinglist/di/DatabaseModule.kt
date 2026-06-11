package ru.ulstu.shoppinglist.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.ulstu.shoppinglist.data.local.AppDatabase
import ru.ulstu.shoppinglist.data.local.dao.ShoppingDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "shopping_db"
        ).build()
    }

    @Provides
    fun provideShoppingDao(database: AppDatabase): ShoppingDao {
        return database.shoppingDao()
    }
}
