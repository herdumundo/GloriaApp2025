package com.gloria.ui.auth.screen

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Lock

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gloria.BuildConfig
import com.gloria.R
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val isSmallScreen = screenHeight < 600.dp
    
    // Gradiente rojo oscuro para el fondo
    val gradientColors: List<Color> = listOf(
        Color(0xFF8B0000),  // Rojo oscuro
        Color(0xFF4A0000)   // Rojo muy oscuro
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(brush = Brush.verticalGradient(colors = gradientColors)),
        verticalArrangement = Arrangement.spacedBy(if (isSmallScreen) 8.dp else 16.dp)
    ) {
        // Header
        Header(Modifier.align(Alignment.End))
        
        // Body - contenido principal
        Body(
            modifier = Modifier.fillMaxWidth(),
            username = username,
            password = password,
            showPassword = showPassword,
            isLoading = isLoading,
            isSmallScreen = isSmallScreen,
            onUsernameChange = { username = it },
            onPasswordChange = { password = it },
            onShowPasswordChange = { showPassword = it },
            onLoginClick = { onLoginClick(username, password) }
        )
        
        // Footer
        Footer(Modifier.align(Alignment.CenterHorizontally))
        
        // Espacio adicional para asegurar scroll en pantallas pequeñas
        if (isSmallScreen) {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
    
    // Mostrar diálogo de error cuando hay un mensaje de error
    if (errorMessage != null) {
        ErrorDialog(
            errorMessage = errorMessage,
            onDismiss = { /* El error se maneja desde el ViewModel */ }
        )
    }
}

@SuppressLint("ContextCastToActivity")
@Composable
fun Header(modifier: Modifier) {
    val activity = LocalContext.current as Activity
    IconButton(
        onClick = { activity.finish() },
        modifier = modifier.padding(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Cerrar aplicación",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun Body(
    modifier: Modifier,
    username: String,
    password: String,
    showPassword: Boolean,
    isLoading: Boolean,
    isSmallScreen: Boolean,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onShowPasswordChange: (Boolean) -> Unit,
    onLoginClick: () -> Unit
) {
    var contentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300) // Pequeña demora antes de que comience la animación
        contentVisible = true
    }

    AnimatedVisibility(
        visible = contentVisible,
        enter = fadeIn(animationSpec = androidx.compose.animation.core.tween(durationMillis = 1000)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = if (isSmallScreen) 16.dp else 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo/Icono del sistema
            SystemLogo(
                modifier = Modifier.padding(bottom = if (isSmallScreen) 16.dp else 32.dp),
                isSmallScreen = isSmallScreen
            )

            // Card contenedor del formulario
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(if (isSmallScreen) 12.dp else 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.1f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier.padding(if (isSmallScreen) 16.dp else 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
               
                    
                    Text(
                        text = "Iniciar Sesión",
                        style = if (isSmallScreen) MaterialTheme.typography.titleSmall 
                               else MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.padding(bottom = if (isSmallScreen) 16.dp else 24.dp)
                    )

                    // Campo de username
                    UsernameField(
                        username = username,
                        onUsernameChange = onUsernameChange,
                        isSmallScreen = isSmallScreen,
                        modifier = Modifier.padding(bottom = if (isSmallScreen) 12.dp else 16.dp)
                    )

                    // Campo de contraseña
                    PasswordField(
                        password = password,
                        showPassword = showPassword,
                        onPasswordChange = onPasswordChange,
                        onShowPasswordChange = onShowPasswordChange,
                        isSmallScreen = isSmallScreen,
                        modifier = Modifier.padding(bottom = if (isSmallScreen) 16.dp else 24.dp)
                    )

                    // Botón de login
                    LoginButton(
                        isLoading = isLoading,
                        enabled = username.isNotBlank() && password.isNotBlank(),
                        onLoginClick = onLoginClick,
                        isSmallScreen = isSmallScreen,
                        modifier = Modifier.padding(bottom = if (isSmallScreen) 8.dp else 16.dp)
                    )
                    
                    // Información adicional para asegurar scroll
                    if (isSmallScreen) {
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "App ${BuildConfig.VERSION_NAME}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        Text(
                            text = "Sistema de gestión de inventario",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SystemLogo(modifier: Modifier, isSmallScreen: Boolean) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo de Distribuidora Gloria
        Box(
            modifier = Modifier
                .size(if (isSmallScreen) 80.dp else 120.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFFFFF)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.dgbann),
                contentDescription = "Logo Distribuidora Gloria",
                modifier = Modifier.size(if (isSmallScreen) 70.dp else 100.dp),
                contentScale = ContentScale.Crop
            )
        }

    }
}

@Composable
fun UsernameField(
    username: String,
    onUsernameChange: (String) -> Unit,
    isSmallScreen: Boolean,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = username,
        onValueChange = onUsernameChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text("Usuario", color = Color.White.copy(alpha = 0.8f)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Usuario",
                tint = Color.White.copy(alpha = 0.8f)
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.White.copy(alpha = 0.6f),
            cursorColor = Color.White
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        shape = RoundedCornerShape(if (isSmallScreen) 8.dp else 12.dp)
    )
}

@Composable
fun PasswordField(
    password: String,
    showPassword: Boolean,
    onPasswordChange: (String) -> Unit,
    onShowPasswordChange: (Boolean) -> Unit,
    isSmallScreen: Boolean,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text("Contraseña", color = Color.White.copy(alpha = 0.8f)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Contraseña",
                tint = Color.White.copy(alpha = 0.8f)
            )
        },
        trailingIcon = {
            IconButton(onClick = { onShowPasswordChange(!showPassword) }) {
                Icon(
                    imageVector = if (showPassword) Icons.Default.Lock else Icons.Default.Person,
                    contentDescription = if (showPassword) "Ocultar contraseña" else "Mostrar contraseña",
                    tint = Color.White.copy(alpha = 0.8f)
                )
            }
        },
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.White.copy(alpha = 0.6f),
            cursorColor = Color.White
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        shape = RoundedCornerShape(if (isSmallScreen) 8.dp else 12.dp)
    )
}

@Composable
fun LoginButton(
    isLoading: Boolean,
    enabled: Boolean,
    onLoginClick: () -> Unit,
    isSmallScreen: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onLoginClick,
        modifier = modifier
            .fillMaxWidth()
            .height(if (isSmallScreen) 44.dp else 50.dp),
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF4A0000),  // Rojo muy oscuro
            disabledContainerColor = Color.Gray
        ),
        shape = RoundedCornerShape(if (isSmallScreen) 8.dp else 12.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        if (isLoading) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Iniciando sesión...",
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
                Text(
                    text = "Iniciar Sesión",
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = if (isSmallScreen) 14.sp else 16.sp
                )
        }
    }
}

@Composable
fun Footer(modifier: Modifier) {
    val configuration = LocalConfiguration.current
    val isSmallScreen = configuration.screenHeightDp.dp < 600.dp
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(if (isSmallScreen) 16.dp else 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LoginDivider()
        Spacer(modifier = Modifier.height(if (isSmallScreen) 8.dp else 16.dp))
    }
}

@Composable
fun LoginDivider() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier
                .weight(1f)
                .height(1.dp),
            color = Color.White.copy(alpha = 0.3f)
        )
        Text(
            text = "App Inventario v1.0.0",
            modifier = Modifier.padding(horizontal = 16.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.6f)
        )
        HorizontalDivider(
            modifier = Modifier
                .weight(1f)
                .height(1.dp),
            color = Color.White.copy(alpha = 0.3f)
        )
    }
}

@Composable
fun ErrorDialog(
    errorMessage: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Icono de error
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚠️",
                        fontSize = 20.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Error de conexión",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
            }
        },
        text = {
            Column {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666),
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Línea decorativa
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFFE0E0E0))
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8B0000),  // Rojo oscuro
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 4.dp
                )
            ) {
                Text(
                    text = "Entendido",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun SyncDialog(
    syncMessage: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { /* No se puede cancelar */ },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 3.dp,
                    color = Color(0xFF8B0000)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Sincronizando Datos Maestros",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF8B0000)
                )
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = syncMessage,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF666666),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Se están sincronizando los datos maestros",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF888888)
                )
            }
        },
        confirmButton = {
            // No hay botón de confirmación durante la sincronización
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.padding(16.dp)
    )
}
