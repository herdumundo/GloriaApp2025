# Migración de Permisos de Usuario: Oracle DB → API REST

## Problema Identificado

El error que aparecía al sincronizar datos maestros era:

```
Error al obtener permisos desde Oracle para invap
java.lang.Exception: No se pudo establecer conexión con Oracle
```

Esto ocurría porque el `SyncUserPermissionsFromOracleUseCase` seguía intentando conectarse directamente a Oracle para obtener los permisos del usuario, mientras que ya habíamos migrado la sincronización de datos maestros a la API.

## Solución Implementada

### Archivos Creados

#### 1. Modelos de API (`UserPermissionsResponse.kt`)
- **Ubicación**: `app/src/main/java/com/gloria/data/entity/api/UserPermissionsResponse.kt`
- **Descripción**: Modelos de datos para la respuesta de la API de permisos
- **Contenido**:
  - `UserPermissionsResponse`: Respuesta principal con success, message, data, error
  - `UserPermissionsData`: Contenedor de datos del usuario y sus permisos
  - `UserPermissionApi`: Modelo individual para cada permiso (formulario, nombre)

#### 2. Servicio API (`UserPermissionsApiService.kt`)
- **Ubicación**: `app/src/main/java/com/gloria/data/service/UserPermissionsApiService.kt`
- **Descripción**: Interfaz Retrofit para el endpoint de permisos de usuario
- **Endpoint**: `GET /api/auth/user-permissions`
- **Parámetros**: `username`, `userdb`, `passdb`

#### 3. Repositorio API (`UserPermissionsApiRepository.kt`)
- **Ubicación**: `app/src/main/java/com/gloria/data/repository/UserPermissionsApiRepository.kt`
- **Descripción**: Capa de abstracción para manejar llamadas a la API de permisos
- **Funcionalidad**: Manejo de errores HTTP y respuestas de la API

### Archivos Modificados

#### 1. SyncUserPermissionsFromOracleUseCase.kt
- **Cambio principal**: Reemplazó las consultas SQL directas por llamadas a la API
- **Dependencias actualizadas**: 
  - ❌ Eliminado: `UserPermissionOracleDao`
  - ✅ Agregado: `UserPermissionsApiRepository`
- **Métodos actualizados**:
  - `invoke()`: Ahora usa la API en lugar de Oracle
  - `checkAndSyncUserPermission()`: Actualizado para usar la API
  - ❌ Eliminado: `syncAllUsersPermissions()` (dependía de Oracle)

#### 2. NetworkModule.kt
- **Agregados**: Proveedores de Dagger Hilt para:
  - `UserPermissionsApiService`
  - `UserPermissionsApiRepository`

#### 3. UseCaseModule.kt
- **Cambio**: Actualizado el proveedor de `SyncUserPermissionsFromOracleUseCase` para usar `UserPermissionsApiRepository` en lugar de `UserPermissionOracleDao`

## Endpoint de la API de Permisos

```bash
curl -X 'GET' \
  'http://localhost:8081/backend-gloria/api/auth/user-permissions?username=invap&userdb=invap&passdb=invext2024' \
  -H 'accept: */*'
```

## Estructura de la Respuesta Esperada

```json
{
  "success": true,
  "message": "string",
  "data": {
    "username": "invap",
    "permissions": [
      {
        "formulario": "INVENTARIO",
        "nombre": "Inventario"
      },
      {
        "formulario": "TOMA_MANUAL",
        "nombre": "Toma Manual"
      }
    ],
    "totalPermissions": 2
  },
  "error": "string"
}
```

## Flujo de Sincronización Actualizado

1. **Datos Maestros**: Se obtienen desde `/api/auth/datos-maestros`
2. **Permisos de Usuario**: Se obtienen desde `/api/auth/user-permissions`
3. **Almacenamiento**: Ambos se almacenan en la base de datos local Room
4. **Sin Conexión Oracle**: Ya no hay dependencia directa de Oracle

## Ventajas de la Nueva Implementación

1. **Consistencia**: Toda la sincronización ahora usa APIs REST
2. **Simplicidad**: Eliminación de lógica compleja de conexión Oracle
3. **Mantenibilidad**: Separación clara entre API y lógica de negocio
4. **Confiabilidad**: Mejor manejo de errores de red vs errores de BD
5. **Performance**: Llamadas HTTP optimizadas vs consultas SQL complejas

## Compatibilidad

- ✅ Mantiene la misma interfaz pública del UseCase
- ✅ Preserva el formato de datos esperado por Room
- ✅ No requiere cambios en las pantallas que usan permisos
- ✅ El `SincronizacionCompletaRepository` sigue funcionando igual

## Configuración Requerida

- La `BASE_URL` ya está configurada correctamente
- Los parámetros `userdb` y `passdb` tienen valores por defecto
- Retrofit y Gson ya estaban configurados previamente

## Notas de Implementación

- Se mantiene la conversión de permisos API a formato `Pair<String, String>` para compatibilidad con Room
- El manejo de errores es robusto tanto para errores HTTP como errores de la API
- Los logs mantienen la misma estructura para facilitar debugging
- Se eliminó la dependencia de `UserPermissionOracleDao` que ya no es necesaria

## Estado Final

✅ **Problema Resuelto**: Ya no habrá errores de conexión Oracle durante la sincronización
✅ **Arquitectura Consistente**: Toda la sincronización usa APIs REST
✅ **Funcionalidad Preservada**: Los permisos siguen funcionando igual para el usuario final
