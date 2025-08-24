# Driver Oracle JDBC

✅ **El driver de Oracle ya está configurado automáticamente usando Maven**

## Configuración actual:

En tu `app/build.gradle` ya tienes configurado:
```gradle
implementation 'com.oracle.database.jdbc:ojdbc8:21.5.0.0'
```

## ¿Qué significa esto?

- **No necesitas descargar manualmente** el driver de Oracle
- **Gradle descarga automáticamente** la dependencia desde Maven Central
- **La versión 21.5.0.0** es la más reciente y estable
- **Compatible con Java 8+** y Android

## Verificación:

Después de sincronizar tu proyecto, deberías poder importar:
```kotlin
import oracle.jdbc.driver.OracleDriver
```

## Si quieres usar una versión específica:

Puedes cambiar la versión en `build.gradle`:
```gradle
implementation 'com.oracle.database.jdbc:ojdbc8:19.8.0.0' // Versión más antigua
implementation 'com.oracle.database.jdbc:ojdbc8:21.9.0.0' // Versión más reciente
```

## Nota importante:

- El driver de Oracle es propietario y requiere aceptar los términos de licencia
- Para desarrollo, se usa la versión "Thin" que es más ligera
- La conexión se establece usando JDBC estándar
