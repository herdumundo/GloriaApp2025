package com.gloria.ui.theme

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

object ThemeManager {
    var isDarkTheme by mutableStateOf(false)
        private set
    
    fun toggleTheme() {
        isDarkTheme = !isDarkTheme
    }
}
