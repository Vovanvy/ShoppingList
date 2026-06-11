package ru.ulstu.shoppinglist.presentation.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.ulstu.shoppinglist.data.local.SettingsManager
import ru.ulstu.shoppinglist.domain.model.ShoppingItem
import ru.ulstu.shoppinglist.domain.repository.ShoppingRepository
import javax.inject.Inject

@HiltViewModel
class ShoppingViewModel @Inject constructor(
    private val repository: ShoppingRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {

    val items: StateFlow<List<ShoppingItem>> = repository.getItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isDarkMode = settingsManager.isDarkMode
    val languageCode = settingsManager.languageCode

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            settingsManager.setDarkMode(enabled)
        }
    }

    fun toggleLanguage() {
        viewModelScope.launch {
            val current = settingsManager.languageCode.first()
            val next = if (current == "ru") "en" else "ru"
            settingsManager.setLanguage(next)
        }
    }

    fun addItem(name: String) {
        viewModelScope.launch {
            repository.insertItem(ShoppingItem(name = name))
        }
    }

    fun toggleItem(item: ShoppingItem) {
        viewModelScope.launch {
            repository.updateItem(item.copy(isCrossedOut = !item.isCrossedOut))
        }
    }

    fun deleteItem(item: ShoppingItem) {
        viewModelScope.launch {
            repository.deleteItem(item)
        }
    }
}
