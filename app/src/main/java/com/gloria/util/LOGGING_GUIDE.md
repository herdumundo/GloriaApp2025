# 📋 Guía de Logging para Inventario

## 🎯 Propósito
Este sistema de logging está diseñado para debuggear problemas en la aplicación de inventario, especialmente cuando no se obtienen resultados esperados en consultas de subgrupos.

## 🔧 Componentes del Sistema

### 1. **InventoryLogger** - Clase Principal
Ubicación: `app/src/main/java/com/gloria/util/InventoryLogger.kt`

### 2. **Funciones de Logging Disponibles**

#### **Logging de Entidades:**
```kotlin
// Logging individual
InventoryLogger.logSucursal(sucursal, "OPERATION")
InventoryLogger.logDepartamento(departamento, "OPERATION")
InventoryLogger.logArea(area, "OPERATION")
InventoryLogger.logDpto(dpto, "OPERATION")
InventoryLogger.logSeccion(seccion, "OPERATION")
InventoryLogger.logFamilia(familia, "OPERATION")
InventoryLogger.logGrupo(grupo, "OPERATION")
InventoryLogger.logSubgrupo(subgrupo, "OPERATION")

// Logging de listas
InventoryLogger.logSucursales(sucursales, "OPERATION")
InventoryLogger.logDepartamentos(departamentos, "OPERATION")
InventoryLogger.logAreas(areas, "OPERATION")
InventoryLogger.logDptos(dptos, "OPERATION")
InventoryLogger.logSecciones(secciones, "OPERATION")
InventoryLogger.logFamilias(familias, "OPERATION")
InventoryLogger.logGrupos(grupos, "OPERATION")
InventoryLogger.logSubgrupos(subgrupos, "OPERATION")
```

#### **Logging de Operaciones:**
```kotlin
// Logging básico
InventoryLogger.logInfo("OPERATION", "Mensaje informativo")
InventoryLogger.logDebug("OPERATION", "Mensaje de debug")
InventoryLogger.logWarning("OPERATION", "Mensaje de advertencia")
InventoryLogger.logError("OPERATION", "Mensaje de error", exception)

// Logging de estado
InventoryLogger.logState("ESTADO", "Detalles del estado")
InventoryLogger.logQuery("SELECT * FROM tabla", mapOf("param" to "valor"))
InventoryLogger.logQueryResult("SELECT * FROM tabla", 5)
```

#### **Logging Especializado:**
```kotlin
// Estado completo de TomaManual
InventoryLogger.logTomaManualState(
    selectedSucursal,
    selectedDepartamento,
    selectedArea,
    selectedDpto,
    selectedSeccion,
    selectedFamilia,
    selectedGrupos,
    selectedSubgrupo
)

// Operaciones de base de datos
InventoryLogger.logDatabaseOperation("INSERT", "subgrupo", "1 registro insertado")
InventoryLogger.logDatabaseCount("subgrupo", 150)

// Filtros y sincronización
InventoryLogger.logFilter("area", 1, 25)
InventoryLogger.logSyncOperation("IMPORT", "Importando 100 registros")
```

## 🚀 Cómo Usar para Debuggear Subgrupos

### **Paso 1: Ejecutar la Aplicación**
1. Compila e instala la aplicación
2. Navega a "Registro de Toma" → "Toma Manual"
3. Completa la selección hasta llegar a Grupos

### **Paso 2: Seleccionar Grupos**
1. Selecciona uno o varios grupos
2. Observa los logs en Android Studio (Logcat)

### **Paso 3: Abrir Diálogo de Subgrupos**
1. Confirma la selección de grupos
2. Se abrirá automáticamente el diálogo de subgrupos
3. Revisa los logs para ver qué está pasando

## 📊 Logs que Verás

### **Al Seleccionar Familia:**
```
[FAMILIA_SELECTED] Familia seleccionada: 1 - LACTEOS
[FAMILIA] [SELECTED] Familia: 1 - LACTEOS (Área: 1, Dpto: 1, Sección: 1)
[CARGAR_GRUPOS_FAMILIA] Cargando grupos para familia: 1
[GRUPOS_CARGADOS_FAMILIA] Grupos cargados para familia 1: 5
[GRUPO] [FAMILIA_1] Grupo: 1 - QUESOS (Área: 1, Dpto: 1, Sección: 1, Familia: 1)
[GRUPO] [FAMILIA_1] Grupo: 2 - YOGURT (Área: 1, Dpto: 1, Sección: 1, Familia: 1)
...
```

### **Al Seleccionar Grupos:**
```
[GRUPO_TOGGLED] Alternando grupo: 1 - QUESOS
[GRUPO_ADDED] Grupo agregado: 1
[GRUPOS_SELECTION] Total grupos seleccionados: 1, Todos seleccionados: false
[GRUPO] [SELECTED] Grupo: 1 - QUESOS (Área: 1, Dpto: 1, Sección: 1, Familia: 1)
```

### **Al Confirmar Selección:**
```
[CONFIRM_GRUPO_SELECTION] Confirmando selección de grupos
[CARGAR_SUBGRUPOS] Iniciando carga de subgrupos para 1 grupos seleccionados
[GRUPO] [CONFIRMED_SELECTION] Grupo: 1 - QUESOS (Área: 1, Dpto: 1, Sección: 1, Familia: 1)
[PROCESANDO_GRUPO_SUBGRUPOS] Procesando grupo: 1 - QUESOS
[SUBGRUPOS_ENCONTRADOS_SIMPLE] Para grupo 1: 0 subgrupos
[RESULTADO_FINAL_SIMPLE] Total de subgrupos únicos: 0
```

### **Al Abrir Diálogo de Subgrupos:**
```
[SHOW_SUBGRUPO_DIALOG] Abriendo diálogo de subgrupos
[TOMA_MANUAL_STATE] Estado actual:
  - Sucursal: 1 - SUCURSAL CENTRAL
  - Departamento: 1 - DEPARTAMENTO PRINCIPAL
  - Área: 1 - ÁREA LACTEOS
  - Dpto: 1 - DPTO LACTEOS
  - Sección: 1 - SECCIÓN QUESOS
  - Familia: 1 - LACTEOS
  - Grupos seleccionados: 1
    * 1 - QUESOS
  - Subgrupo: No seleccionado
[LOAD_SUBGRUPOS] Cargando subgrupos para 1 grupos
[PROCESANDO_GRUPO] Procesando grupo: 1 - QUESOS
[SUBGRUPOS_ENCONTRADOS] Para grupo 1: 0 subgrupos
[RESULTADO_FINAL] Total de subgrupos únicos: 0
```

## 🔍 Análisis de Logs

### **Si No Hay Subgrupos:**

#### **Caso 1: Consulta Simple Retorna 0**
```
[SUBGRUPOS_ENCONTRADOS_SIMPLE] Para grupo 1: 0 subgrupos
```
**Problema:** La consulta `getSubgruposByGrupo(grupCodigo)` no encuentra resultados.

#### **Caso 2: Consulta con Contexto Retorna 0**
```
[SUBGRUPOS_ENCONTRADOS] Para grupo 1: 0 subgrupos
```
**Problema:** La consulta `getSubgruposByGrupoWithContext()` no encuentra resultados.

### **Posibles Causas:**

1. **Datos No Sincronizados:**
   - Verificar si se ejecutó "Sincronizar Datos"
   - Revisar si hay datos en la tabla `subgrupo`

2. **Filtros Muy Restrictivos:**
   - La consulta con contexto puede ser demasiado específica
   - Verificar que los códigos coincidan exactamente

3. **Problema de Estructura:**
   - Verificar que las claves primarias compuestas estén correctas
   - Revisar que los índices estén bien definidos

## 🛠️ Soluciones Recomendadas

### **1. Verificar Datos en Base de Datos:**
```kotlin
// Agregar este logging en el ViewModel
suspend fun debugDatabaseState() {
    val totalSubgrupos = subgrupoDao.getCount()
    InventoryLogger.logDatabaseCount("subgrupo", totalSubgrupos)
    
    // Verificar subgrupos por grupo específico
    subgrupoDao.getAllSubgrupos().collect { subgrupos ->
        InventoryLogger.logSubgrupos(subgrupos, "DEBUG_ALL")
    }
}
```

### **2. Simplificar Consultas:**
```kotlin
// Usar consulta simple en lugar de con contexto
subgrupoDao.getSubgruposByGrupo(grupCodigo).collect { subgrupos ->
    // Logging aquí
}
```

### **3. Verificar Sincronización:**
```kotlin
// En SincronizarDatosScreen, agregar logging
InventoryLogger.logSyncOperation("IMPORT_SUBGRUPOS", "Iniciando importación de subgrupos")
// ... después de importar
InventoryLogger.logDatabaseCount("subgrupo", subgrupoDao.getCount())
```

## 📱 Cómo Ver los Logs

### **En Android Studio:**
1. Abre **Logcat** (View → Tool Windows → Logcat)
2. Filtra por tag: `InventoryLogger`
3. Ejecuta la aplicación y navega por Toma Manual
4. Observa los logs en tiempo real

### **Filtros Útiles:**
```
// Solo logs de subgrupos
tag:InventoryLogger message:subgrupo

// Solo logs de grupos
tag:InventoryLogger message:grupo

// Solo errores
tag:InventoryLogger level:ERROR

// Solo información
tag:InventoryLogger level:INFO
```

## 🎯 Ejemplo de Debugging Completo

### **Escenario:** No se obtienen subgrupos al seleccionar grupo "QUESOS"

1. **Verificar Estado:**
   ```
   [TOMA_MANUAL_STATE] Estado actual:
     - Grupo: 1 - QUESOS (Área: 1, Dpto: 1, Sección: 1, Familia: 1)
   ```

2. **Verificar Consulta:**
   ```
   [PROCESANDO_GRUPO] Procesando grupo: 1 - QUESOS
   [SUBGRUPOS_ENCONTRADOS] Para grupo 1: 0 subgrupos
   ```

3. **Verificar Base de Datos:**
   ```
   [DB_COUNT] Tabla subgrupo: 0 registros
   ```

4. **Conclusión:** No hay datos sincronizados

### **Solución:**
1. Ejecutar "Sincronizar Datos"
2. Verificar que se importen subgrupos
3. Probar nuevamente la selección

## 🔧 Personalización

### **Agregar Nuevos Logs:**
```kotlin
// En cualquier ViewModel o Repository
InventoryLogger.logInfo("MI_OPERACION", "Descripción de la operación")
InventoryLogger.logError("MI_ERROR", "Descripción del error", exception)
```

### **Cambiar Nivel de Log:**
```kotlin
// En InventoryLogger.kt, cambiar Log.d por Log.i, Log.w, o Log.e según necesidad
Log.i(TAG, "[$operation] $message")  // Información
Log.w(TAG, "[$operation] $message")  // Advertencia
Log.e(TAG, "[$operation] $message")  // Error
```

## 📞 Soporte

Si encuentras problemas con el sistema de logging:

1. **Verificar compilación:** `./gradlew assembleDebug`
2. **Revisar imports:** Asegurar que `InventoryLogger` esté importado
3. **Verificar permisos:** Los logs aparecen en Logcat sin permisos especiales
4. **Limpiar proyecto:** `./gradlew clean` si hay problemas de compilación

---

**🎉 ¡Con este sistema de logging podrás debuggear fácilmente cualquier problema con subgrupos!**

