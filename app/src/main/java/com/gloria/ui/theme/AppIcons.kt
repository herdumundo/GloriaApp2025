package com.gloria.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Diccionario centralizado de iconos de Material Design que están garantizados de existir.
 * Esto evita errores de compilación por iconos inexistentes.
 */
object AppIcons {
    
    // NAVEGACIÓN Y ACCIONES BÁSICAS
    val ArrowBack = Icons.Default.ArrowBack
    val ArrowForward = Icons.Default.ArrowForward
    val ArrowDropDown = Icons.Default.ArrowDropDown
    val Close = Icons.Default.Close
    val Check = Icons.Default.Check
    val CheckCircle = Icons.Default.CheckCircle
    val Add = Icons.Default.Add
    val Edit = Icons.Default.Edit
    val Delete = Icons.Default.Delete
    val Clear = Icons.Default.Clear
    val Refresh = Icons.Default.Refresh
    val Search = Icons.Default.Search
    val Menu = Icons.Default.Menu
    val MoreVert = Icons.Default.MoreVert
    val Settings = Icons.Default.Settings
    val Info = Icons.Default.Info
    val Warning = Icons.Default.Warning
    
    // UBICACIONES Y LUGARES
    val Home = Icons.Default.Home
    val LocationOn = Icons.Default.LocationOn
    val Place = Icons.Default.Place
    val Build = Icons.Default.Build
    
    // PERSONAS Y GRUPOS
    val Person = Icons.Default.Person
    val AccountCircle = Icons.Default.AccountCircle
    
    // INVENTARIO Y ALMACENAMIENTO
    val ShoppingCart = Icons.Default.ShoppingCart
    
    // LISTAS Y ORGANIZACIÓN
    val List = Icons.Default.List
    val Email = Icons.Default.Email
    val Phone = Icons.Default.Phone
    val Notifications = Icons.Default.Notifications
    val DateRange = Icons.Default.DateRange
    val PlayArrow = Icons.Default.PlayArrow
    val Star = Icons.Default.Star
    val Favorite = Icons.Default.Favorite
    val Lock = Icons.Default.Lock
    
    // ICONOS ESPECÍFICOS PARA INVENTARIO
    object Inventario {
        val Sucursal = Icons.Default.Home
        val Deposito = Icons.Default.Build
        val Area = Icons.Default.Place
        val Departamento = Icons.Default.LocationOn
        val Seccion = Icons.Default.List
        val Familia = Icons.Default.Person
        val Grupo = Icons.Default.Star
        val Subgrupo = Icons.Default.Favorite
        val Producto = Icons.Default.ShoppingCart
        val Fecha = Icons.Default.DateRange
        val Usuario = Icons.Default.Person
        val Estado = Icons.Default.CheckCircle
        val Sincronizar = Icons.Default.Refresh
        val Configuracion = Icons.Default.Settings
        val Buscar = Icons.Default.Search
        val Editar = Icons.Default.Edit
        val Eliminar = Icons.Default.Delete
        val Confirmar = Icons.Default.CheckCircle
    }
    
    // FUNCIÓN DE UTILIDAD PARA OBTENER ICONO POR NOMBRE
    fun getIconByName(name: String): ImageVector {
        return when (name.lowercase()) {
            "home", "casa", "sucursal" -> Home
            "build", "almacen", "deposito" -> Build
            "place", "categoria", "area" -> Place
            "location", "negocio", "departamento" -> LocationOn
            "list", "lista", "seccion" -> List
            "person", "grupo", "familia" -> Person
            "star", "etiqueta", "subgrupo" -> Star
            "check", "confirmar", "ok" -> Check
            "close", "cerrar", "cancelar" -> Close
            "edit", "editar", "modificar" -> Edit
            "delete", "eliminar", "borrar" -> Delete
            "search", "buscar", "encontrar" -> Search
            "settings", "configuracion", "ajustes" -> Settings
            "refresh", "sincronizar", "actualizar" -> Refresh
            "warning", "advertencia", "alerta" -> Warning
            "info", "informacion", "detalle" -> Info
            else -> Info // Icono por defecto
        }
    }
}
