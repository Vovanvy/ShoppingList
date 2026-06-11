package ru.ulstu.shoppinglist.domain.model

data class ShoppingItem(
    val id: Long = 0,
    val name: String,
    val isCrossedOut: Boolean = false,
    val category: String = "Common"
)
