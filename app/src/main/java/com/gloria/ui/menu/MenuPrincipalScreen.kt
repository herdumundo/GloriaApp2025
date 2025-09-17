package com.gloria.ui.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gloria.ui.inventario.viewmodel.TomaManualViewModel
import androidx.navigation.NavHostController
import androidx.activity.compose.BackHandler
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import com.gloria.ui.inventario.screen.ArticulosTomaScreen
import com.gloria.ui.inventario.screen.CancelacionInventarioScreen
import com.gloria.ui.inventario.screen.ConteoInventarioScreen
import com.gloria.ui.inventario.screen.RegistroInventarioScreen
import com.gloria.ui.inventario.screen.SincronizarDatosScreen
import com.gloria.ui.inventario.screen.TomaCriterioScreen
import com.gloria.ui.inventario.screen.TomaManualScreen
import com.gloria.ui.informe.screen.InformeConteosPendientesScreen
import com.gloria.ui.inventario.viewmodel.ConteoInventarioViewModel
import com.gloria.ui.inventario.viewmodel.RegistroInventarioViewModel
import com.gloria.ui.inventario.viewmodel.SincronizacionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuPrincipalScreen(
    navController: NavHostController,
    onNavigateToTomaManual: () -> Unit,
    onNavigateToRegistroEscaneados: () -> Unit = {},
    onNavigateToCapturaManual: () -> Unit = {},
    onNavigateToValidacionCodigos: () -> Unit = {},
    username: String = "Usuario",
    sucursal: String = "Sucursal",
    onLogoutClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val viewModel: MenuPrincipalViewModel = hiltViewModel()
    val loggedUser by viewModel.loggedUser.collectAsState()
    
    var selectedMenuItem by remember { mutableStateOf("home") }
    var currentScreen by remember { mutableStateOf("main") }
    var showTipoTomaDialog by remember { mutableStateOf(false) }
    var selectedTipoToma by remember { mutableStateOf<com.gloria.domain.model.TipoToma?>(null) }
    var nroInventarioSeleccionado by remember { mutableStateOf(0) }
    var nroTomaSeleccionado by remember { mutableStateOf(0) }
    
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    // Obtener la versión de la app
    val context = LocalContext.current
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val appVersion = packageInfo.versionName
    
    // BackHandler para manejar la navegación de regreso
    BackHandler {
        when (currentScreen) {
            "conteo_inventario" -> {
                currentScreen = "registro_inventario"
                nroInventarioSeleccionado = 0
            }
            "articulos_toma" -> {
                currentScreen = "cancelacion_inventario"
                nroTomaSeleccionado = 0
            }
            "registro_inventario", "sincronizar_datos", "cancelacion_inventario" -> {
                currentScreen = "main"
                selectedMenuItem = "home"
            }
            "toma" -> {
                currentScreen = "main"
                selectedTipoToma = null
            }
            else -> {
                // Si estamos en la pantalla principal, no hacer nada
                // o podrías cerrar la app aquí si lo deseas
            }
        }
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
                    username = loggedUser?.username ?: username,
                    sucursal = sucursal,
                    selectedItem = selectedMenuItem,
                    onItemClick = { itemId ->
                        selectedMenuItem = itemId
                        when (itemId) {
                            "registro_toma" -> {
                                showTipoTomaDialog = true
                            }
                            "registro_inventario" -> {
                                currentScreen = "registro_inventario"
                            }
                            "cancelacion_inventario" -> {
                                currentScreen = "cancelacion_inventario"
                            }
                            "sincronizar_datos" -> {
                                currentScreen = "sincronizar_datos"
                            }
                            "informe_conteos_pendientes" -> {
                                currentScreen = "informe_conteos_pendientes"
                            }
                            else -> {
                                currentScreen = "main"
                            }
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
        // Diálogo de selección de tipo de toma
        if (showTipoTomaDialog) {
            com.gloria.ui.components.SeleccionTipoTomaDialog(
                onDismiss = { showTipoTomaDialog = false },
                onTipoSeleccionado = { tipoToma ->
                    selectedTipoToma = tipoToma
                    currentScreen = "toma"
                    showTipoTomaDialog = false
                }
            )
        }
        
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = getScreenTitle(selectedMenuItem),
                            style = MaterialTheme.typography.titleLarge,
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
                                text = loggedUser?.username ?: username,
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
                                text = "v$appVersion",
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
                // Contenido principal
                when (currentScreen) {
                    "toma" -> {
                        when (selectedTipoToma?.id) {
                            "criterio_seleccion" -> TomaCriterioScreen(
                                onBackPressed = {
                                    currentScreen = "main"
                                    selectedTipoToma = null
                                }
                            )
                            "manual" -> {
                                val tomaManualViewModel: TomaManualViewModel = hiltViewModel()
                                TomaManualScreen(
                                    viewModel = tomaManualViewModel,
                                    navController = navController
                                )
                            }
                            else -> {
                                // Contenido del menú principal
                                LazyColumn(
                                    modifier = modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    item {
                                        // Título de bienvenida
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.primaryContainer
                                            )
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(24.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Spacer(modifier = Modifier.height(16.dp))
                                                
                                                Text(
                                                    text = "Registro de Toma de Inventario",
                                                    style = MaterialTheme.typography.headlineMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    textAlign = TextAlign.Center,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                                
                                                Spacer(modifier = Modifier.height(8.dp))
                                                
                                                Text(
                                                    text = "Selecciona una opción del menú lateral para comenzar.",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    }
                                    
                                    item {
                                        // Información adicional
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                                            )
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp)
                                            ) {
                                                Text(
                                                    text = "• Guardado automático de progreso",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                
                                                Spacer(modifier = Modifier.height(4.dp))
                                                
                                                Text(
                                                    text = "• Sincronización en tiempo real",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                
                                                Spacer(modifier = Modifier.height(4.dp))
                                                
                                                Text(
                                                    text = "• Respaldo automático de datos",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    "sincronizar_datos" -> {
                        val sincronizacionViewModel:  SincronizacionViewModel= hiltViewModel()
                        SincronizarDatosScreen(
                           sincronizacionViewModel = sincronizacionViewModel
                       )
                    }
                    "registro_inventario" -> {
                        val registroViewModel:  RegistroInventarioViewModel = hiltViewModel()
                        RegistroInventarioScreen(
                             viewModel = registroViewModel,
                             onNavigateToConteo = { nroInventario ->
                                 nroInventarioSeleccionado = nroInventario
                                 currentScreen = "conteo_inventario"
                             },
                             navController = navController
                         )
                    }
                    "cancelacion_inventario" -> {
                        CancelacionInventarioScreen(
                           onNavigateBack = {
                               currentScreen = "main"
                               selectedMenuItem = "home"
                           },
                           onNavigateToArticulos = { nroToma ->
                               nroTomaSeleccionado = nroToma
                               currentScreen = "articulos_toma"
                           }
                       )
                    }
                   /* "articulos_toma" -> {
                        ArticulosTomaScreen(
                             nroToma = nroTomaSeleccionado,
                            articulosTomaViewModel,
                            navController
                         )
                    }*/
                    "conteo_inventario" -> {
                        val conteoViewModel: ConteoInventarioViewModel = hiltViewModel()
                        ConteoInventarioScreen(
                            nroInventario = nroInventarioSeleccionado,
                            viewModel = conteoViewModel,
                            navController = navController
                        )
                    }
                    "informe_conteos_pendientes" -> {
                        InformeConteosPendientesScreen(
                            navController = navController
                        )
                    }
                    else -> {
                        // Contenido del menú principal
                        LazyColumn(
                            modifier = modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                // Título de bienvenida
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        
                                        Text(
                                            text = "Registro de Toma de Inventario",
                                            style = MaterialTheme.typography.headlineMedium,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        
                                        Spacer(modifier = Modifier.height(8.dp))
                                        
                                        Text(
                                            text = "Selecciona una opción del menú lateral para comenzar.",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                            
                            item {
                                // Información adicional
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        Text(
                                            text = "• Guardado automático de progreso",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        
                                        Spacer(modifier = Modifier.height(4.dp))
                                        
                                        Text(
                                            text = "• Sincronización en tiempo real",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        
                                        Spacer(modifier = Modifier.height(4.dp))
                                        
                                        Text(
                                            text = "• Respaldo automático de datos",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
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
            items(com.gloria.domain.model.MenuItems.items) { item ->
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
private fun ThemeToggleItem() {
    val isDarkTheme = com.gloria.ui.theme.ThemeManager.isDarkTheme
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { com.gloria.ui.theme.ThemeManager.toggleTheme() }
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
            onCheckedChange = { com.gloria.ui.theme.ThemeManager.toggleTheme() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
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
private fun MenuOption(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (enabled) 4.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (enabled) {
                        Modifier.clickable { onClick() }
                    } else {
                        Modifier
                    }
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.outline
                )
            }
            
            if (enabled) {
                Text(
                    text = "→",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

private fun getScreenTitle(selectedMenuItem: String): String {
    return when (selectedMenuItem) {
        "registro_toma" -> "Registro de Toma"
        "registro_inventario" -> "Registro de Inventario"
        "cancelacion_inventario" -> "Cancelación de Inventario"
        "exportar_inventario" -> "Exportar Inventario"
        "exportar_parcial" -> "Exportar Inventario Parcial"
        "sincronizar_datos" -> "Sincronizar Datos"
        "informe_conteos_pendientes" -> "Informe de Conteos Pendientes"
        else -> "Sistema de Inventario"
    }
}