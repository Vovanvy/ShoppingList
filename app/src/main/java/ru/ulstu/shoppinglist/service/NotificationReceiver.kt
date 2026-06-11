package ru.ulstu.shoppinglist.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.ulstu.shoppinglist.domain.model.ShoppingItem
import ru.ulstu.shoppinglist.domain.repository.ShoppingRepository
import javax.inject.Inject

@AndroidEntryPoint
class NotificationReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: ShoppingRepository

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "ACTION_CROSS_OUT") {
            val itemId = intent.getLongExtra("ITEM_ID", -1L)
            if (itemId != -1L) {
                scope.launch {
                    repository.getItemById(itemId)?.let { item ->
                        repository.updateItem(item.copy(isCrossedOut = true))
                    }
                }
            }
        }
    }
}
