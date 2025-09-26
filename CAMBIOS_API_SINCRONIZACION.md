# Migración de Sincronización: Oracle DB → API REST

## Resumen de Cambios

Se ha migrado el sistema de sincronización de datos maestros desde consultas directas a la base de datos Oracle hacia una API REST que devuelve todos los datos en una sola llamada.

## Archivos Creados

### 1. Modelos de API (`DatosMaestrosResponse.kt`)
- **Ubicación**: `app/src/main/java/com/gloria/data/entity/api/DatosMaestrosResponse.kt`
- **Descripción**: Modelos de datos que representan la respuesta de la API
- **Contenido**:
  - `DatosMaestrosResponse`: Respuesta principal con success, message, data, error
  - `DatosMaestrosData`: Contenedor de todos los datos maestros
  - Modelos API individuales: `AreaApi`, `DepartamentoApi`, `SeccionApi`, `FamiliaApi`, `GrupoApi`, `SubgrupoApi`, `SucursalDepartamentoApi`

### 2. Servicio API (`DatosMaestrosApiService.kt`)
- **Ubicación**: `app/src/main/java/com/gloria/data/service/DatosMaestrosApiService.kt`
- **Descripción**: Interfaz Retrofit para el endpoint de datos maestros
- **Endpoint**: `GET /api/auth/datos-maestros`
- **Parámetros**: `userdb` y `passdb`

### 3. Repositorio API (`DatosMaestrosApiRepository.kt`)
- **Ubicación**: `app/src/main/java/com/gloria/data/repository/DatosMaestrosApiRepository.kt`
- **Descripción**: Capa de abstracción para manejar llamadas a la API
- **Funcionalidad**: Manejo de errores HTTP y respuestas de la API

## Archivos Modificados

### 1. SincronizacionCompletaRepository.kt
- **Cambio principal**: Reemplazó las consultas SQL directas por una sola llamada a la API
- **Métodos eliminados**: Todos los métodos `sincronizar*()` que hacían consultas SQL
- **Métodos agregados**: Métodos `convertir*FromApi()` para transformar modelos API a entidades de BD
- **Mejoras**:
  - Sincronización más rápida (una sola llamada vs múltiples consultas SQL)
  - Menos dependencia de conexiones directas a Oracle
  - Mejor manejo de errores de red

### 2. NetworkModule.kt
- **Agregados**: Proveedores de Dagger Hilt para:
  - `DatosMaestrosApiService`
  - `DatosMaestrosApiRepository`

### 3. UseCaseModule.kt
- **Cambio**: Actualizado el proveedor de `SincronizacionCompletaRepository` para incluir `DatosMaestrosApiRepository`

## Endpoint de la API

```bash
curl -X 'GET' \
  'http://localhost:8081/backend-gloria/api/auth/datos-maestros?userdb=invap&passdb=invext2024' \
  -H 'accept: */*'
```

## Estructura de la Respuesta

```json
{
  "success": true,
  "message": "string",
  "data": {
    "areas": [...],
    "departamentos": [...],
    "secciones": [...],
    "familias": [...],
    "grupos": [...],
    "subgrupos": [...],
    "sucursalesDepartamentos": [...],
    "totalRegistros": 0
  },
  "error": "string"
}
```

## Ventajas de la Nueva Implementación

1. **Performance**: Una sola llamada HTTP vs múltiples consultas SQL
2. **Simplicidad**: Eliminación de lógica compleja de conexión Oracle
3. **Mantenibilidad**: Separación clara entre API y lógica de negocio
4. **Escalabilidad**: La API puede ser optimizada independientemente
5. **Confiabilidad**: Mejor manejo de errores de red vs errores de BD

## Compatibilidad

- ✅ Mantiene la misma interfaz pública (`sincronizarTodasLasTablas`)
- ✅ Preserva el callback de progreso
- ✅ Mantiene el formato de `SincronizacionResult`
- ✅ No requiere cambios en las pantallas que usan la sincronización

## Configuración Requerida

- La `BASE_URL` ya está configurada en `app/build.gradle`
- Los parámetros `userdb` y `passdb` tienen valores por defecto pero pueden ser personalizados
- Retrofit y Gson ya estaban configurados previamente

## Uso

```kotlin
// La API no cambia para los consumidores
val result = sincronizacionCompletaRepository.sincronizarTodasLasTablas(
    onProgress = { message, current, total -> 
        // Callback de progreso
    }
)
```

## Notas de Implementación

- Se mantiene el timestamp de sincronización en cada entidad
- La conversión de modelos API a entidades conserva todos los campos necesarios  
- El manejo de errores es robusto tanto para errores HTTP como errores de la API
- Los logs mantienen la misma estructura para facilitar debugging
