package ru.ulstu.shoppinglist.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.ulstu.shoppinglist.data.local.entity.ShoppingItemEntity

@Dao
interface ShoppingDao {
    @Query("SELECT * FROM shopping_items")
    fun getItems(): Flow<List<ShoppingItemEntity>>

    @Query("SELECT * FROM shopping_items WHERE id = :id")
    suspend fun getItemById(id: Long): ShoppingItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ShoppingItemEntity)

    @Delete
    suspend fun deleteItem(item: ShoppingItemEntity)

    @Update
    suspend fun updateItem(item: ShoppingItemEntity)

    @Query("DELETE FROM shopping_items WHERE isCrossedOut = 1")
    suspend fun deleteCompletedItems()
}
