package com.gloria.ui.main.screen

import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.gloria.domain.model.MenuItems
import com.gloria.ui.inventario.screen.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(
    navController: NavHostController,
    username: String,
    sucursal: String,
    onLogoutClick: () -> Unit
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
    
    // ViewModel para sincronización
    val sincronizacionViewModel: SincronizacionViewModel = hiltViewModel()


    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    // Observar estado de sincronización
    val syncState by sincronizacionViewModel.uiState.collectAsState()
    
    LaunchedEffect(syncState) {
        if (syncState.isLoading) {
            isSyncing = true
            syncError = null
            syncSuccess = false
        } else if (syncState.isSuccess) {
            isSyncing = false
            syncSuccess = true
            // Después del éxito, mostrar el diálogo de selección de tipo de toma
            showTipoTomaDialog = true
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
                    onItemClick = { itemId ->
                        selectedMenuItem = itemId
                        if (itemId == "registro_toma") {
                            // Primero sincronizar datos maestros, luego mostrar diálogo de tipo de toma
                            showSyncDialog = true
                            sincronizacionViewModel.sincronizarDatos()
                        }
                        scope.launch { 
                            drawerState.close() 
                        }
                    },
                    onLogout = onLogoutClick
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
                    message = if (syncError != null) syncError!! else "Se están sincronizando los datos maestros desde Oracle",
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
                                text = "Sistema de Inventario",
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
                                        // Primero sincronizar datos maestros, luego mostrar diálogo de tipo de toma
                                        showSyncDialog = true
                                        sincronizacionViewModel.sincronizarDatos()
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
                    "exportar_inventario" -> ExportarInventarioScreen()
                    "exportar_parcial" -> ExportarParcialScreen()
                    "sincronizar_datos" -> {
                        val sincronizacionViewModel: SincronizacionViewModel = hiltViewModel()
                        SincronizarDatosScreen(
                            sincronizacionViewModel = sincronizacionViewModel
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
    onItemClick: (String) -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header del drawer con información del usuario
        DrawerHeader(username = username, sucursal = sucursal)
        
        HorizontalDivider()
        
        // Items del menú
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(MenuItems.items) { item ->
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
        
        HorizontalDivider()
        
        // Toggle de modo oscuro
        ThemeToggleItem()
        
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
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Usuario",
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
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
        // Icono principal
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Sistema de Inventario",
                modifier = Modifier.size(60.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        8
        Spacer(modifier = Modifier.height(32.dp))
        

        
        Spacer(modifier = Modifier.height(16.dp))
        
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
                InfoRow("Sistema", "Inventario v1.0")
                
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
private fun ThemeToggleItem() {
    val isDarkTheme = ThemeManager.isDarkTheme
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable  { ThemeManager.toggleTheme() }
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
            onCheckedChange = { ThemeManager.toggleTheme() },
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
        "articulos_toma" -> "Artículos de Toma"
        "menu_principal" -> "Sistema de Inventario"
        else -> "Sistema de Inventario"
    }
}