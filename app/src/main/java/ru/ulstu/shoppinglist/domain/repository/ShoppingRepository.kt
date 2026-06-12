package ru.ulstu.shoppinglist.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.ulstu.shoppinglist.domain.model.ShoppingItem

interface ShoppingRepository {
    fun getItems(): Flow<List<ShoppingItem>>
    suspend fun getItemById(id: Long): ShoppingItem?
    suspend fun insertItem(item: ShoppingItem)
    suspend fun deleteItem(item: ShoppingItem)
    suspend fun updateItem(item: ShoppingItem)
    suspend fun deleteCompletedItems()
}
