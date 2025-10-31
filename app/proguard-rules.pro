# Add project specific ProGuard rules here.

# ================================
# Reglas generales de Kotlin
# ================================
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
}

# ================================
# Retrofit
# ================================
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**

# ================================
# OkHttp
# ================================
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# ================================
# Gson
# ================================
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Mantener todas las clases de datos (modelos) del proyecto
-keep class com.gloria.data.entity.** { *; }
-keep class com.gloria.data.model.** { *; }

# Mantener clases con anotaciones @SerializedName
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# ================================
# Room Database
# ================================
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**
-keep class androidx.room.** { *; }
-keep interface androidx.room.** { *; }

# Mantener DAOs
-keep class com.gloria.data.dao.** { *; }

# ================================
# Hilt / Dagger
# ================================
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }
-keep @dagger.hilt.** class * { *; }
-keep @javax.inject.** class * { *; }
-keepclasseswithmembers class * {
    @dagger.** <fields>;
}
-keepclasseswithmembers class * {
    @dagger.** <methods>;
}
-keepclasseswithmembers class * {
    @javax.inject.** <fields>;
}
-keepclasseswithmembers class * {
    @javax.inject.** <methods>;
}

# ================================
# ZXing (Código de barras)
# ================================
-keep class com.google.zxing.** { *; }
-keep class com.journeyapps.barcodescanner.** { *; }
-dontwarn com.google.zxing.**

# ================================
# JDBC Oracle (ojdbc5.jar)
# ================================
-keep class oracle.jdbc.** { *; }
-keep class oracle.sql.** { *; }
-keep class oracle.net.** { *; }
-dontwarn oracle.jdbc.**
-dontwarn oracle.sql.**
-dontwarn oracle.net.**

# ================================
# Jetpack Compose
# ================================
-keep class androidx.compose.** { *; }
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }
-keep class androidx.compose.foundation.** { *; }
-keep class androidx.compose.material.** { *; }
-keep class androidx.compose.material3.** { *; }
-dontwarn androidx.compose.**

# ================================
# ViewModels
# ================================
-keep class * extends androidx.lifecycle.ViewModel {
    <init>();
}
-keep class * extends androidx.lifecycle.AndroidViewModel {
    <init>(android.app.Application);
}

# ================================
# Navigation
# ================================
-keep class androidx.navigation.** { *; }
-keepnames class androidx.navigation.fragment.NavHostFragment

# ================================
# Serialización general
# ================================
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# Mantener información de líneas para stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ================================
# Clases específicas del proyecto
# ================================
# Mantener todas las clases en el paquete principal
-keep class com.gloria.** { *; }

# Mantener todas las interfaces
-keep interface com.gloria.** { *; }

# ================================
# Reflection
# ================================
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleParameterAnnotations

# ================================
# Enums
# ================================
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ================================
# Parcelable
# ================================
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}

# ================================
# Suprimir warnings comunes
# ================================
-dontwarn javax.annotation.**
-dontwarn javax.inject.**
-dontwarn sun.misc.Unsafe
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement