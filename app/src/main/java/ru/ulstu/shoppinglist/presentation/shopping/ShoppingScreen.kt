package ru.ulstu.shoppinglist.presentation.shopping

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
    val isDarkMode by viewModel.isDarkMode.collectAsState(initial = false)
    val languageCode by viewModel.languageCode.collectAsState(initial = "en")
    var itemName by remember { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.shopping_list)) },
                actions = {
                    IconButton(onClick = { viewModel.toggleLanguage() }) {
                        Text(languageCode.uppercase())
                    }
                    IconButton(onClick = {
                        viewModel.toggleDarkMode(!isDarkMode)
                    }) {
                        Icon(
                            if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = stringResource(R.string.toggle_theme)
                        )
                    }
                    IconButton(onClick = {
                        val intent = Intent(context, ShoppingService::class.java)
                        context.startForegroundService(intent)
                    }) {
                        Icon(Icons.Default.PlayArrow, contentDescription = stringResource(R.string.start_shopping))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (itemName.isNotBlank()) {
                    viewModel.addItem(itemName)
                    itemName = ""
                }
            }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(stringResource(R.string.enter_item_name)) }
                )
            }

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
