package ru.ulstu.shoppinglist.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.ulstu.shoppinglist.data.local.dao.ShoppingDao
import ru.ulstu.shoppinglist.data.local.entity.toDomain
import ru.ulstu.shoppinglist.data.local.entity.toEntity
import ru.ulstu.shoppinglist.domain.model.ShoppingItem
import ru.ulstu.shoppinglist.domain.repository.ShoppingRepository

class ShoppingRepositoryImpl(
    private val dao: ShoppingDao
) : ShoppingRepository {
    override fun getItems(): Flow<List<ShoppingItem>> {
        return dao.getItems().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getItemById(id: Long): ShoppingItem? {
        return dao.getItemById(id)?.toDomain()
    }

    override suspend fun insertItem(item: ShoppingItem) {
        dao.insertItem(item.toEntity())
    }

    override suspend fun deleteItem(item: ShoppingItem) {
        dao.deleteItem(item.toEntity())
    }

    override suspend fun updateItem(item: ShoppingItem) {
        dao.updateItem(item.toEntity())
    }

    override suspend fun deleteCompletedItems() {
        dao.deleteCompletedItems()
    }
}
