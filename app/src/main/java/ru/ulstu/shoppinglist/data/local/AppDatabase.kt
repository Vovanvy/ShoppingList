package ru.ulstu.shoppinglist.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.ulstu.shoppinglist.data.local.dao.ShoppingDao
import ru.ulstu.shoppinglist.data.local.entity.ShoppingItemEntity

@Database(entities = [ShoppingItemEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shoppingDao(): ShoppingDao
}
