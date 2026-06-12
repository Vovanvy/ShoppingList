package ru.ulstu.shoppinglist.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "settings")

@Singleton
class SettingsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val isDarkModeKey = booleanPreferencesKey("is_dark_mode")
    private val languageKey = stringPreferencesKey("language_code")

    val isDarkMode: Flow<Boolean?> = context.dataStore.data.map { preferences ->
        preferences[isDarkModeKey]
    }

    val languageCode: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[languageKey] ?: Locale.getDefault().language
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[isDarkModeKey] = enabled
        }
    }

    suspend fun setLanguage(languageCode: String) {
        context.dataStore.edit { preferences ->
            preferences[languageKey] = languageCode
        }
    }
}
