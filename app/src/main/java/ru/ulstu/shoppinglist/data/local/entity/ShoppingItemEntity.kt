package ru.ulstu.shoppinglist.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.ulstu.shoppinglist.domain.model.ShoppingItem

@Entity(tableName = "shopping_items")
data class ShoppingItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val isCrossedOut: Boolean,
    val category: String
)

fun ShoppingItemEntity.toDomain() = ShoppingItem(
    id = id,
    name = name,
    isCrossedOut = isCrossedOut,
    category = category
)

fun ShoppingItem.toEntity() = ShoppingItemEntity(
    id = id,
    name = name,
    isCrossedOut = isCrossedOut,
    category = category
)
