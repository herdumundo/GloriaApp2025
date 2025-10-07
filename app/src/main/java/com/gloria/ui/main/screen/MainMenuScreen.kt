package com.gloria.ui.main.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.gloria.R
import com.gloria.domain.model.MenuItem
import com.gloria.domain.model.MenuItems
import com.gloria.domain.usecase.permission.GetUserAllowedScreensUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import com.gloria.ui.inventario.screen.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.gloria.ui.inventario.screen.ConteoInventarioScreen
import com.gloria.ui.inventario.screen.ArticulosTomaScreen
import com.gloria.ui.theme.ThemeManager
import com.gloria.ui.components.SeleccionTipoTomaDialog
import com.gloria.domain.model.TipoToma
import com.gloria.ui.inventario.viewmodel.TomaManualViewModel
import com.gloria.repository.SincronizacionCompletaRepository
import com.gloria.data.repository.InventarioSincronizacionRepository
import com.gloria.ui.inventario.viewmodel.RegistroInventarioViewModel
import com.gloria.ui.inventario.viewmodel.SincronizacionViewModel
import com.gloria.ui.inventario.viewmodel.ArticulosTomaViewModel
import com.gloria.ui.components.ConfirmationDialog
import com.gloria.data.AppDatabase
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.hilt.navigation.compose.hiltViewModel
import com.gloria.ui.exportaciones.screen.ExportacionesScreen
import com.gloria.ui.exportaciones.viewmodel.ExportacionesViewModel
import com.gloria.ui.informe.screen.InformeConteosPendientesScreen
import com.gloria.util.ConnectionOracle
import android.util.Log
import com.gloria.BuildConfig

// EntryPoint para inyección de dependencias en MainMenuScreen
@EntryPoint
@InstallIn(SingletonComponent::class)
interface MainMenuEntryPoint {
    fun getUserAllowedScreensUseCase(): GetUserAllowedScreensUseCase
}

// Mapa para controlar sincronización automática por usuario (solo una vez por usuario por sesión)
private val hasAutoSyncedByUser = mutableMapOf<String, Boolean>()

/**
 * Función para limpiar el estado de sincronización cuando un usuario cierra sesión
 */
fun clearSyncStateForUser(username: String) {
    hasAutoSyncedByUser.remove(username)
    Log.d("PROCESO_LOGIN", "🧹 Estado de sincronización limpiado para usuario: $username")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(
    navController: NavHostController,
    username: String,
    sucursal: String,
    onLogoutClick: () -> Unit,
    authViewModel: com.gloria.ui.auth.viewmodel.AuthViewModel
) {
    var selectedMenuItem by remember { mutableStateOf("registro_toma") }
    var showTipoTomaDialog by remember { mutableStateOf(false) }
    var selectedTipoToma by remember { mutableStateOf<TipoToma?>(null) }
    var nroTomaSeleccionado by remember { mutableStateOf<Int?>(null) }
    
    // Estados para sincronización automática
    var showSyncDialog by remember { mutableStateOf(false) }
    var isSyncing by remember { mutableStateOf(false) }
    var syncSuccess by remember { mutableStateOf(false) }
    var syncError by remember { mutableStateOf<String?>(null) }
    
    // Estados para permisos del menú
    var allowedMenuItems by remember { mutableStateOf<List<MenuItem>>(emptyList()) }
    var isLoadingPermissions by remember { mutableStateOf(true) }
    var permissionsError by remember { mutableStateOf<String?>(null) }
    
    // ViewModel para sincronización
    val sincronizacionViewModel: SincronizacionViewModel = hiltViewModel()
    
    // UseCase para permisos del menú - lo inyectamos usando LocalContext
    val context = LocalContext.current
    val getUserAllowedScreensUseCase: GetUserAllowedScreensUseCase = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            MainMenuEntryPoint::class.java
        ).getUserAllowedScreensUseCase()
    }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    // Observar estado de sincronización
    val syncState by sincronizacionViewModel.uiState.collectAsState()
    
    // Cargar permisos del usuario
    LaunchedEffect(username) {
        try {
            isLoadingPermissions = true
            permissionsError = null
            val allowedScreens = getUserAllowedScreensUseCase(username)
            allowedMenuItems = allowedScreens
            isLoadingPermissions = false
        } catch (e: Exception) {
            isLoadingPermissions = false
            permissionsError = "Error al cargar permisos: ${e.message}"
            // En caso de error, mostrar todos los items
            allowedMenuItems = MenuItems.items
        }
    }
    
    // Sincronización automática al cargar la pantalla por primera vez (solo una vez por usuario por sesión)
    LaunchedEffect(username) {
        if (hasAutoSyncedByUser[username] != true) {
            hasAutoSyncedByUser[username] = true
            Log.d("PROCESO_LOGIN", "🔄 Primera vez en MainMenuScreen para usuario $username, iniciando sincronización automática")
            
            // Verificar conexión a la base de datos antes de sincronizar
            val connection = withContext(Dispatchers.IO) {
                Log.d("PROCESO_LOGIN", "🔄 Verificando conexión en hilo IO: ${Thread.currentThread().name}")
                
                try {
                    // Intentar usar credenciales del usuario logueado
                    val authState = authViewModel.state.value
                    if (authState.currentUser != null && authState.tempPassword != null) {
                        Log.d("PROCESO_LOGIN", "🔍 Usando credenciales del AuthViewModel")
                        val conn = ConnectionOracle.getConnection(authState.currentUser, authState.tempPassword)
                        if (conn != null) {
                            Log.d("PROCESO_LOGIN", "✅ Conexión encontrada, cerrando conexión de verificación")
                            conn.close()
                        }
                        return@withContext conn
                    } else {
                        Log.d("PROCESO_LOGIN", "🔍 No hay credenciales en AuthViewModel, intentando método sin parámetros")
                        val conn = ConnectionOracle.getConnection()
                        if (conn != null) {
                            Log.d("PROCESO_LOGIN", "✅ Conexión encontrada, cerrando conexión de verificación")
                            conn.close()
                        }
                        return@withContext conn
                    }
                } catch (e: Exception) {
                    Log.w("PROCESO_LOGIN", "⚠️ No se pudo verificar conexión: ${e.message}")
                    Log.d("PROCESO_LOGIN", "🔄 Continuando sin verificación de conexión")
                    return@withContext null
                }
            }
            
            if (connection != null) {
                // Hay conexión, proceder con la sincronización
                Log.d("PROCESO_LOGIN", "✅ Conexión verificada exitosamente, iniciando sincronización automática")
                showSyncDialog = true
                sincronizacionViewModel.sincronizarDatos()
            } else {
                // No hay conexión, no mostrar diálogo de sincronización
                Log.d("PROCESO_LOGIN", "❌ No hay conexión a la base de datos, omitiendo sincronización automática")
            }
        } else {
            Log.d("PROCESO_LOGIN", "⏭️ Ya se sincronizó automáticamente en esta sesión, omitiendo")
        }
    }
    
    LaunchedEffect(syncState) {
        if (syncState.isLoading) {
            isSyncing = true
            syncError = null
            syncSuccess = false
        } else if (syncState.isSuccess) {
            isSyncing = false
            syncSuccess = true
            // Solo cerrar el diálogo de sincronización, NO mostrar diálogo de tipo de toma
            showSyncDialog = false
            // Limpiar el estado de éxito del ViewModel
            sincronizacionViewModel.clearSuccess()
        } else if (syncState.errorMessage != null) {
            isSyncing = false
            syncError = syncState.errorMessage
            syncSuccess = false
        }
    }
    
    // Deshabilitar el botón atrás en el menú principal
    BackHandler {
    }
    
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(320.dp),
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                drawerContentColor = MaterialTheme.colorScheme.onSurface
            ) {
                NavigationDrawerContent(
                    username = username,
                    sucursal = sucursal,
                    selectedItem = selectedMenuItem,
                    allowedMenuItems = allowedMenuItems,
                    isLoadingPermissions = isLoadingPermissions,
                    permissionsError = permissionsError,
                    onItemClick = { itemId ->
                        selectedMenuItem = itemId
                        if (itemId == "registro_toma") {
                            // Mostrar directamente el diálogo de tipo de toma sin sincronización automática
                            showTipoTomaDialog = true
                        }
                        scope.launch { 
                            drawerState.close() 
                        }
                    },
                    onLogout = {
                        // Limpiar estado de sincronización antes de hacer logout
                        clearSyncStateForUser(username)
                        onLogoutClick()
                    },
                    authViewModel = authViewModel
                )
            }
        }
            ) {
            // Diálogo de sincronización de datos maestros
            if (showSyncDialog) {
                ConfirmationDialog(
                    showDialog = showSyncDialog,
                    onDismiss = { 
                        showSyncDialog = false
                        if (syncError != null) {
                            syncError = null
                            sincronizacionViewModel.clearError()
                        }
                    },
                    onConfirm = { 
                        if (syncError != null) {
                            // Reintentar sincronización
                            syncError = null
                            sincronizacionViewModel.clearError()
                            sincronizacionViewModel.sincronizarDatos()
                        }
                    },
                    
                    // Estados
                    isLoading = isSyncing,
                    loadingProgress = if (syncState.masterDataProgressTotal > 0) {
                        (syncState.masterDataProgressCurrent.toFloat() / syncState.masterDataProgressTotal.toFloat()) * 100f
                    } else 0f,
                    successMessage = if (syncSuccess) "Datos maestros sincronizados correctamente" else null,
                    
                    // Títulos
                    title = "Sincronizando Datos Maestros",
                    loadingTitle = "Sincronizando...",
                    successTitle = "¡Sincronización Exitosa!",
                    
                    // Mensajes
                    message = if (syncError != null) syncError!! else "Se están sincronizando los datos maestros",
                    loadingMessage = syncState.masterDataProgressMessage,
                    successMainMessage = "Datos maestros actualizados",
                    
                    // Botones
                    confirmButtonText = "Reintentar",
                    successButtonText = "Continuar",
                    dismissButtonText = "Cancelar",
                    
                    // Colores
                    confirmIconColor = if (syncError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    successIconColor = MaterialTheme.colorScheme.primary,
                    loadingColor = MaterialTheme.colorScheme.primary,
                    confirmButtonColor = if (syncError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    
                    // Iconos
                    confirmIcon = if (syncError != null) Icons.Default.Close else Icons.Default.Refresh,
                    successIcon = Icons.Default.Check
                )
            }
            
            // Diálogo de selección de tipo de toma
            if (showTipoTomaDialog) {
                SeleccionTipoTomaDialog(
                    onDismiss = { showTipoTomaDialog = false },
                    onTipoSeleccionado = { tipoToma ->
                        selectedTipoToma = tipoToma
                        showTipoTomaDialog = false
                        // Ambos tipos van a la misma pantalla (TomaManualScreen)
                        selectedMenuItem = "toma_manual"
                    }
                )
            }
            
            Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            text = getScreenTitle(selectedMenuItem, selectedTipoToma),
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch { 
                                    drawerState.open() 
                                }
                            }
                        ) {
                            Text(
                                text = "☰",
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    actions = {
                        // Información del usuario en la esquina superior derecha
                        Column(
                            modifier = Modifier.padding(end = 16.dp),
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy((-2).dp)
                        ) {
                            Text(
                                text = username,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = sucursal,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "App ${BuildConfig.VERSION_NAME}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                                    colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Contenido principal según el menú seleccionado
                when (selectedMenuItem) {
                    "toma_manual" -> {
                        // Pantalla unificada para ambos tipos de toma
                        val tomaManualViewModel: TomaManualViewModel = hiltViewModel()
                        TomaManualScreen(
                            viewModel = tomaManualViewModel,
                            navController = navController,
                            tipoToma = selectedTipoToma, // Bandera para identificar el tipo
                            onNavigateToHome = {
                                selectedTipoToma = null
                                selectedMenuItem = "menu_principal"
                            }
                        )
                    }
                    "registro_toma" -> {
                        when (selectedTipoToma?.id) {
                            "criterio_seleccion" -> TomaCriterioScreen(
                                onBackPressed = {
                                    selectedTipoToma = null
                                },
                                onNavigateToHome = {
                                    selectedTipoToma = null
                                    selectedMenuItem = "menu_principal"
                                }
                            )
                            "manual" -> {
                                val tomaManualViewModel: TomaManualViewModel = hiltViewModel()
                                TomaManualScreen(
                                    viewModel = tomaManualViewModel,
                                    navController = navController,
                                    tipoToma = selectedTipoToma,
                                    onNavigateToHome = {
                                        selectedTipoToma = null
                                        selectedMenuItem = "menu_principal"
                                    }
                                )
                            }
                            else -> HomeContent(
                                username = username, 
                                sucursal = sucursal,
                                onCardClick = { itemId ->
                                    selectedMenuItem = itemId
                                    if (itemId == "registro_toma") {
                                        // Mostrar directamente el diálogo de tipo de toma sin sincronización automática
                                        showTipoTomaDialog = true
                                    }
                                }
                            )
                        }
                    }
                    "registro_inventario" -> {
                        val registroViewModel: RegistroInventarioViewModel = hiltViewModel()
                        RegistroInventarioScreen(
                            viewModel = registroViewModel,
                            onNavigateToConteo = { nroInventario ->
                                navController.navigate("conteo_inventario/$nroInventario")
                            },
                            navController = navController
                        )
                    }
                    "cancelacion_inventario" -> {
                        CancelacionInventarioScreen(
                            onNavigateBack = {
                                selectedMenuItem = "menu_principal"
                            },
                            onNavigateToArticulos = { nroToma ->
                                nroTomaSeleccionado = nroToma
                                selectedMenuItem = "articulos_toma"
                            }
                        )
                    }
                    "exportar_inventario" ->
                    {
                        ExportacionesScreen(
                            viewModel = hiltViewModel(),
                            navController = navController
                        )
                    }





                     "sincronizar_datos" -> {
                        val sincronizacionViewModel: SincronizacionViewModel = hiltViewModel()
                        SincronizarDatosScreen(
                            sincronizacionViewModel = sincronizacionViewModel
                        )
                    }
                    "informe_conteos_pendientes" -> {
                        InformeConteosPendientesScreen(
                            navController = navController
                        )
                    }
                    "articulos_toma" -> {
                        nroTomaSeleccionado?.let { nroToma ->
                            val articulosViewModel: ArticulosTomaViewModel = hiltViewModel()
                            ArticulosTomaScreen(
                                nroToma = nroToma,
                                viewModel = articulosViewModel,
                                navController
                            )
                        } ?: HomeContent(
                            username = username, 
                            sucursal = sucursal,
                            onCardClick = { itemId ->
                                selectedMenuItem = itemId
                                if (itemId == "registro_toma") {
                                    // Primero sincronizar datos maestros, luego mostrar diálogo de tipo de toma
                                    showSyncDialog = true
                                    sincronizacionViewModel.sincronizarDatos()
                                }
                            }
                        )
                    }
                    else -> HomeContent(
                        username = username, 
                        sucursal = sucursal,
                        onCardClick = { itemId ->
                            selectedMenuItem = itemId
                            if (itemId == "registro_toma") {
                                // Primero sincronizar datos maestros, luego mostrar diálogo de tipo de toma
                                showSyncDialog = true
                                sincronizacionViewModel.sincronizarDatos()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun NavigationDrawerContent(
    username: String,
    sucursal: String,
    selectedItem: String,
    allowedMenuItems: List<MenuItem>,
    isLoadingPermissions: Boolean,
    permissionsError: String?,
    onItemClick: (String) -> Unit,
    onLogout: () -> Unit,
    authViewModel: com.gloria.ui.auth.viewmodel.AuthViewModel
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header del drawer con información del usuario
        DrawerHeader(username = username, sucursal = sucursal)
        
        HorizontalDivider()
        
        // Items del menú
        if (isLoadingPermissions) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Cargando permisos...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else if (permissionsError != null) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = permissionsError,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(allowedMenuItems) { item ->
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { 
                        Text(
                            text = item.title,
                            fontSize = 16.sp
                        )
                    },
                    selected = selectedItem == item.id,
                    onClick = { onItemClick(item.id) },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
            }
        }
        
        HorizontalDivider()
        
        // Toggle de modo oscuro
        ThemeToggleItem(authViewModel)
        
        HorizontalDivider()
        
        // Botón de logout
        NavigationDrawerItem(
            icon = {
                Text(
                    text = "🚪",
                    fontSize = 20.sp
                )
            },
            label = { 
                Text(
                    text = "Cerrar Sesión",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.error
                )
            },
            selected = false,
            onClick = onLogout,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun DrawerHeader(
    username: String,
    sucursal: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF8B0000),      // Rojo oscuro
                        Color(0xFFB22222),      // Rojo medio
                        Color(0xFFDC143C)       // Crimson
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icono de usuario
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.dgbann),
                    contentDescription = "Logo Distribuidora Gloria",
                    modifier = Modifier.size( 100.dp),
                    contentScale = ContentScale.Crop
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Información del usuario
            Text(
                text = username,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = "Sucursal: $sucursal",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "INVENTARIO V.1.0",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


@Composable
private fun HomeContent(
    username: String,
    sucursal: String,
    onCardClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Información de tu sesión",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                InfoRow("Usuario", username)
                InfoRow("Sucursal", sucursal)
                InfoRow("Sistema", "App ${BuildConfig.VERSION_NAME}")
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Selecciona una opción del menú lateral para comenzar.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        


    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ThemeToggleItem(
    authViewModel: com.gloria.ui.auth.viewmodel.AuthViewModel
) {
    val authState by authViewModel.state.collectAsState()
    val isDarkTheme = authState.modoDark
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { 
                authViewModel.updateModoDark(!isDarkTheme)
                ThemeManager.updateTheme(!isDarkTheme)
            }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isDarkTheme) "🌙" else "☀️",
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = if (isDarkTheme) "Modo Oscuro" else "Modo Claro",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        Switch(
            checked = isDarkTheme,
            onCheckedChange = { 
                authViewModel.updateModoDark(it)
                ThemeManager.updateTheme(it)
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

private fun getScreenTitle(selectedMenuItem: String, selectedTipoToma: TipoToma?): String {
    return when (selectedMenuItem) {
        "toma_manual" -> {
            when (selectedTipoToma?.id) {
                "criterio_seleccion" -> "Toma por Criterio"
                "manual" -> "Toma Manual"
                else -> "Registro de Toma"
            }
        }
        "registro_toma" -> {
            when (selectedTipoToma?.id) {
                "criterio_seleccion" -> "Toma por Criterio"
                "manual" -> "Toma Manual"
                else -> "Registro de Toma"
            }
        }
        "registro_inventario" -> "Registro de Inventario"
        "cancelacion_inventario" -> "Cancelación de Inventario"
        "exportar_inventario" -> "Exportar Inventario"
        "exportar_parcial" -> "Exportar Inventario Parcial"
        "sincronizar_datos" -> "Sincronizar Datos"
        "informe_conteos_pendientes" -> "Informe de Conteos Pendientes"
         "articulos_toma" -> "Artículos de Toma"
        "menu_principal" -> "Sistema de Inventario"
        else -> "Sistema de Inventario"
    }
}