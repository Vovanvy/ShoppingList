package ru.ulstu.shoppinglist.presentation.shopping

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.ulstu.shoppinglist.R
import ru.ulstu.shoppinglist.domain.model.ShoppingItem
import ru.ulstu.shoppinglist.service.ShoppingService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingScreen(
    viewModel: ShoppingViewModel = hiltViewModel()
) {
    val items by viewModel.items.collectAsState()
    val isDarkModeStored by viewModel.isDarkMode.collectAsState(initial = null)
    val languageCode by viewModel.languageCode.collectAsState(initial = "en")
    val isServiceRunning by viewModel.isShoppingModeActive.collectAsState()
    
    var showAddDialog by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf("") }
    val context = LocalContext.current

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(context.resources.getString(R.string.add_item)) },
            text = {
                TextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    placeholder = { Text(context.resources.getString(R.string.enter_item_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (itemName.isNotBlank()) {
                        viewModel.addItem(itemName)
                        itemName = ""
                        showAddDialog = false
                    }
                }) {
                    Text(context.resources.getString(R.string.add))
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text(context.resources.getString(R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(context.resources.getString(R.string.app_name)) },
                actions = {
                    IconButton(onClick = { viewModel.deleteCompletedItems() }) {
                        Icon(
                            Icons.Default.DeleteSweep,
                            contentDescription = context.resources.getString(R.string.clear_completed)
                        )
                    }
                    IconButton(onClick = { viewModel.toggleLanguage() }) {
                        Text(languageCode.uppercase())
                    }
                    IconButton(onClick = {
                        viewModel.toggleDarkMode(!(isDarkModeStored ?: false))
                    }) {
                        Icon(
                            if (isDarkModeStored == true) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = context.resources.getString(R.string.toggle_theme)
                        )
                    }
                    IconButton(onClick = {
                        val intent = Intent(context, ShoppingService::class.java)
                        if (isServiceRunning) {
                            intent.action = "ACTION_STOP"
                            context.startService(intent)
                        } else {
                            context.startForegroundService(intent)
                        }
                    }) {
                        Icon(
                            if (isServiceRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = context.resources.getString(
                                if (isServiceRunning) R.string.stop_shopping else R.string.start_shopping
                            )
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = context.resources.getString(R.string.add))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(items) { item ->
                    ShoppingListItem(
                        item = item,
                        onToggle = { viewModel.toggleItem(item) },
                        onDelete = { viewModel.deleteItem(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun ShoppingListItem(
    item: ShoppingItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = item.isCrossedOut,
            onCheckedChange = { onToggle() }
        )
        Text(
            text = item.name,
            modifier = Modifier.weight(1f),
            textDecoration = if (item.isCrossedOut) TextDecoration.LineThrough else TextDecoration.None
        )
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete))
        }
    }
}
