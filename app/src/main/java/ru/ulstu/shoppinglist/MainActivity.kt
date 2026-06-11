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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
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
        setContent {
            val isDarkMode by settingsManager.isDarkMode.collectAsState(initial = false)
            val languageCode by settingsManager.languageCode.collectAsState(initial = "en")
            
            val viewModel: ShoppingViewModel = hiltViewModel()
            val context = updateLocale(LocalContext.current, languageCode)
            
            CompositionLocalProvider(LocalContext provides context) {
                ShoppingListTheme(darkTheme = isDarkMode) {
                    ShoppingScreen(viewModel = viewModel)
                }
            }
        }
    }

    private fun updateLocale(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}
