# 📚 Diccionario de Iconos - Material Design

## 🎯 Propósito

Este diccionario centraliza todos los iconos de Material Design que están **garantizados de existir** en el proyecto. Evita errores de compilación por iconos inexistentes y proporciona una referencia rápida para desarrolladores.

## 📂 Estructura del Archivo

### `AppIcons.kt`
Ubicación: `app/src/main/java/com/gloria/ui/theme/AppIcons.kt`

## 🔧 Iconos Básicos Disponibles

### Navegación y Acciones
- `ArrowBack` - Flecha hacia atrás (navegación)
- `ArrowForward` - Flecha hacia adelante (navegación)
- `ArrowDropDown` - Flecha desplegable
- `Close` - Cerrar/cancelar
- `Check` - Confirmar/aceptar
- `CheckCircle` - Confirmación con círculo
- `Add` - Agregar/añadir
- `Edit` - Editar/modificar
- `Delete` - Eliminar/borrar
- `Clear` - Limpiar
- `Refresh` - Actualizar/refrescar
- `Search` - Buscar
- `Menu` - Menú hamburguesa
- `MoreVert` - Más opciones (vertical)
- `Settings` - Configuración
- `Info` - Información
- `Warning` - Advertencia

### Ubicaciones y Lugares
- `Home` - Casa/inicio/sucursal
- `LocationOn` - Ubicación activa
- `Place` - Lugar/sitio
- `Build` - Construcción/herramientas

### Personas y Grupos
- `Person` - Persona/usuario
- `AccountCircle` - Cuenta de usuario

### Inventario y Almacenamiento
- `ShoppingCart` - Carrito de compras

### Listas y Organización
- `List` - Lista
- `Email` - Correo electrónico
- `Phone` - Teléfono
- `Notifications` - Notificaciones
- `DateRange` - Rango de fechas
- `PlayArrow` - Reproducir
- `Star` - Estrella
- `Favorite` - Favorito
- `Lock` - Bloquear/seguridad

## 🏪 Iconos Específicos para Inventario

### `AppIcons.Inventario`
Objeto que contiene iconos contextuales para el sistema de inventario:

```kotlin
AppIcons.Inventario.Sucursal      // 🏠 Home
AppIcons.Inventario.Deposito      // 🔧 Build  
AppIcons.Inventario.Area          // 📍 Place
AppIcons.Inventario.Departamento  // 📍 LocationOn
AppIcons.Inventario.Seccion       // 📋 List
AppIcons.Inventario.Familia       // 👤 Person
AppIcons.Inventario.Grupo         // ⭐ Star
AppIcons.Inventario.Subgrupo      // ❤️ Favorite
AppIcons.Inventario.Producto      // 🛒 ShoppingCart
AppIcons.Inventario.Fecha         // 📅 DateRange
AppIcons.Inventario.Usuario       // 👤 Person
AppIcons.Inventario.Estado        // ✅ CheckCircle
AppIcons.Inventario.Sincronizar   // 🔄 Refresh
AppIcons.Inventario.Configuracion // ⚙️ Settings
AppIcons.Inventario.Buscar        // 🔍 Search
AppIcons.Inventario.Editar        // ✏️ Edit
AppIcons.Inventario.Eliminar      // 🗑️ Delete
AppIcons.Inventario.Confirmar     // ✅ CheckCircle
```

## 🛠️ Función de Utilidad

### `getIconByName(name: String): ImageVector`

Permite obtener iconos por nombre en español o inglés:

```kotlin
val icono = AppIcons.getIconByName("sucursal")  // Retorna Home
val icono = AppIcons.getIconByName("deposito")  // Retorna Build
val icono = AppIcons.getIconByName("buscar")    // Retorna Search
```

### Mapeo de Nombres:
- `"home", "casa", "sucursal"` → `Home`
- `"build", "almacen", "deposito"` → `Build`
- `"place", "categoria", "area"` → `Place`
- `"location", "negocio", "departamento"` → `LocationOn`
- `"list", "lista", "seccion"` → `List`
- `"person", "grupo", "familia"` → `Person`
- `"star", "etiqueta", "subgrupo"` → `Star`
- `"search", "buscar", "encontrar"` → `Search`
- `"settings", "configuracion", "ajustes"` → `Settings`
- `"refresh", "sincronizar, "actualizar"` → `Refresh`

## 📝 Uso en el Código

### Importación
```kotlin
import com.gloria.ui.theme.AppIcons
```

### Uso Directo
```kotlin
Icon(
    imageVector = AppIcons.Inventario.Sucursal,
    contentDescription = "Sucursal"
)
```

### Uso con ModernSelectionCard
```kotlin
ModernSelectionCard(
    label = "Sucursal",
    selectedText = "001 - Sucursal Principal",
    icon = AppIcons.Inventario.Sucursal,
    onClick = { /* acción */ }
)
```

## ✅ Iconos Verificados

Todos los iconos en este diccionario han sido **verificados** y **compilados exitosamente**. No causan errores de:
- ❌ `Unresolved reference`
- ❌ `Symbol not found`
- ❌ Errores de compilación

## 🚫 Iconos que NO Existen (Evitar)

Estos iconos **NO** deben usarse porque causan errores de compilación:

### ❌ Iconos Problemáticos:
- `Icons.Default.Business` ❌
- `Icons.Default.Category` ❌
- `Icons.Default.Storage` ❌
- `Icons.Default.Group` ❌
- `Icons.Default.Inventory` ❌
- `Icons.Default.Department` ❌
- `Icons.Default.Section` ❌
- `Icons.Default.Family` ❌
- `Icons.Default.SubdirectoryArrowRight` ❌
- `Icons.Default.ChevronRight` ❌
- `Icons.Default.People` ❌

## 🔄 Mantenimiento

### Agregar Nuevos Iconos:
1. **Verificar** que el icono existe compilando primero
2. **Agregar** al objeto `AppIcons` correspondiente
3. **Actualizar** esta documentación
4. **Compilar** para confirmar que no hay errores

### Proceso de Verificación:
```kotlin
// 1. Probar el icono individualmente
val testIcon = Icons.Default.NuevoIcono

// 2. Si compila sin errores, agregarlo al diccionario
val NuevoIcono = Icons.Default.NuevoIcono

// 3. Compilar proyecto completo
./gradlew assembleDebug
```

## 📈 Beneficios

### ✅ Ventajas del Diccionario:
- **Evita errores** de compilación por iconos inexistentes
- **Centraliza** todos los iconos en un lugar
- **Proporciona contexto** con nombres descriptivos
- **Facilita mantenimiento** y actualizaciones
- **Mejora legibilidad** del código
- **Estandariza** el uso de iconos en el proyecto
- **Incluye documentación** integrada

### 🎯 Casos de Uso:
- Desarrollo de nuevas pantallas
- Refactoring de componentes existentes
- Onboarding de nuevos desarrolladores
- Mantenimiento de código legacy
- Testing de interfaces de usuario

## 🔍 Búsqueda Rápida

### Por Funcionalidad:
- **Navegación**: `ArrowBack`, `ArrowForward`, `Close`, `Menu`
- **Acciones**: `Add`, `Edit`, `Delete`, `Search`, `Refresh`
- **Estados**: `Check`, `CheckCircle`, `Warning`, `Info`
- **Inventario**: `AppIcons.Inventario.*`
- **Usuarios**: `Person`, `AccountCircle`
- **Ubicaciones**: `Home`, `Place`, `LocationOn`

### Por Contexto Visual:
- **Flechas**: `ArrowBack`, `ArrowForward`, `ArrowDropDown`
- **Círculos**: `CheckCircle`, `AccountCircle`
- **Formas**: `Star`, `Favorite`, `Build`
- **Símbolos**: `Add`, `Clear`, `Lock`

---

**📌 Nota**: Este diccionario se actualiza continuamente. Siempre verificar la compilación después de agregar nuevos iconos.
