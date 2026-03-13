package com.scrapeverything.app.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemePreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)

    private val _themeModeFlow = MutableStateFlow(getThemeMode())
    val themeModeFlow: StateFlow<ThemeMode> = _themeModeFlow

    fun getThemeMode(): ThemeMode {
        val name = prefs.getString("theme_mode", ThemeMode.AUTO.name) ?: ThemeMode.AUTO.name
        return try {
            ThemeMode.valueOf(name)
        } catch (e: IllegalArgumentException) {
            ThemeMode.AUTO
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString("theme_mode", mode.name).apply()
        _themeModeFlow.value = mode
    }
}
