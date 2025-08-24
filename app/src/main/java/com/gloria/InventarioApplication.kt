package com.gloria

import android.app.Application
import com.gloria.data.AppDatabase

class InventarioApplication : Application() {
    
    val database by lazy { AppDatabase.getDatabase(this) }
    
}

