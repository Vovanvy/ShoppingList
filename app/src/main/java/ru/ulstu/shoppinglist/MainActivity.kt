package ru.ulstu.shoppinglist

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import ru.ulstu.shoppinglist.data.local.SettingsManager
import ru.ulstu.shoppinglist.presentation.shopping.ShoppingScreen
import ru.ulstu.shoppinglist.presentation.shopping.ShoppingViewModel
import ru.ulstu.shoppinglist.ui.theme.ShoppingListTheme
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsManager: SettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0)
            }
        }

        enableEdgeToEdge()

        // Read initial theme preference synchronously to avoid flicker
        val initialDarkMode = runBlocking { settingsManager.isDarkMode.first() }

        setContent {
            val isDarkModeStored by settingsManager.isDarkMode.collectAsState(initial = initialDarkMode)
            val isDarkMode = isDarkModeStored ?: isSystemInDarkTheme()

            val languageCode by settingsManager.languageCode.collectAsState(initial = "en")
            
            key(languageCode) {
                val viewModel: ShoppingViewModel = hiltViewModel()
                val context = updateLocale(LocalContext.current, languageCode)
                val configuration = context.resources.configuration
                
                CompositionLocalProvider(
                    LocalContext provides context,
                    LocalConfiguration provides configuration
                ) {
                    ShoppingListTheme(darkTheme = isDarkMode) {
                        ShoppingScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }

    private fun updateLocale(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        return context.createConfigurationContext(config)
    }
}
