package com.gloria.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.gloria.data.dao.LoggedUserDao
import com.gloria.data.dao.SucursalDepartamentoDao
import com.gloria.data.dao.AreaDao
import com.gloria.data.dao.DepartamentoDao
import com.gloria.data.dao.SeccionDao
import com.gloria.data.dao.FamiliaDao
import com.gloria.data.dao.GrupoDao
import com.gloria.data.dao.SubgrupoDao
import com.gloria.data.dao.InventarioDetalleDao
import com.gloria.data.entity.LoggedUser
import com.gloria.data.entity.SucursalDepartamento
import com.gloria.data.entity.Area
import com.gloria.data.entity.Departamento
import com.gloria.data.entity.Seccion
import com.gloria.data.entity.Familia
import com.gloria.data.entity.Grupo
import com.gloria.data.entity.Subgrupo
import com.gloria.data.entity.InventarioDetalle

/**
 * Base de datos principal de la aplicación
 */
@Database(
    entities = [
        LoggedUser::class, 
        SucursalDepartamento::class,
        Area::class,
        Departamento::class,
        Seccion::class,
        Familia::class,
        Grupo::class,
        Subgrupo::class,
        InventarioDetalle::class
    ],
    version = 8,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun loggedUserDao(): LoggedUserDao
    abstract fun sucursalDepartamentoDao(): SucursalDepartamentoDao
    abstract fun areaDao(): AreaDao
    abstract fun departamentoDao(): DepartamentoDao
    abstract fun seccionDao(): SeccionDao
    abstract fun familiaDao(): FamiliaDao
    abstract fun grupoDao(): GrupoDao
    abstract fun subgrupoDao(): SubgrupoDao
    abstract fun inventarioDetalleDao(): InventarioDetalleDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
