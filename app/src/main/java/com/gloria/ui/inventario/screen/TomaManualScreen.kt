package com.gloria.ui.inventario.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.gloria.ui.inventario.viewmodel.TomaManualViewModel
import com.gloria.data.entity.*
import com.gloria.ui.components.SelectionCard
import com.gloria.ui.components.SelectionCardWithAction
import com.gloria.ui.components.MultiSelectDialog
import com.gloria.ui.components.MultiSelectionCard
import com.gloria.ui.components.SingleSelectWithAllDialog
import com.gloria.ui.components.GenericSelectionDialog
import com.gloria.ui.components.ArticulosEncontradosDialog
import com.gloria.domain.model.TipoToma
import com.gloria.ui.components.ExitConfirmationDialog
import com.gloria.ui.components.SelectableItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TomaManualScreen(
    viewModel: TomaManualViewModel,
    navController: NavHostController,
    tipoToma: TipoToma? = null, // Bandera para identificar el tipo de toma
    onNavigateToHome: () -> Unit = {} // Callback para navegar al home
) {
    val uiState by viewModel.uiState.collectAsState()
    var showExitDialog by remember { mutableStateOf(false) }

    // Cargar datos iniciales cuando se inicializa la pantalla
    LaunchedEffect(Unit) {
        viewModel.loadSucursales()
        viewModel.loadAreas()
    }
    
    // Establecer el tipo de toma en el ViewModel cuando se inicializa la pantalla
    LaunchedEffect(tipoToma) {
        if (tipoToma != null) {
            val tipoTomaString = when (tipoToma.id) {
                "manual" -> "M"
                "criterio_seleccion" -> "C"
                else -> "M"
            }
            viewModel.setTipoToma(tipoTomaString)
        }
    }
    
    // Limpiar parámetros del ViewModel cuando se sale de la pantalla
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearAllParameters()
        }
    }

    BackHandler {
        showExitDialog = true
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        
            // Mostrar error si existe
            if (uiState.errorMessage != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = uiState.errorMessage!!,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(onClick = { viewModel.clearError() }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Cerrar"
                                )
                            }
                        }
                    }
                }
            }

            // Grid de selección - Primera fila: Sucursal y Depósito
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SelectionCard(
                        title = "Sucursal",
                        selectedValue = uiState.selectedSucursal?.let { "${it.sucCodigo} - ${it.sucDesc}" },
                        onSelectClick = { viewModel.showSucursalDialog() },
                        modifier = Modifier.weight(1f)
                    )
                    
                    SelectionCard(
                        title = "Depósito",
                        selectedValue = uiState.selectedDepartamento?.let { "${it.depCodigo} - ${it.depDesc}" },
                        onSelectClick = { viewModel.showDepartamentoDialog() },
                        modifier = Modifier.weight(1f),
                        isEnabled = uiState.selectedSucursal != null
                    )
                }
            }

            // Segunda fila: Área y Departamento Interno
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SelectionCard(
                        title = "Área",
                        selectedValue = uiState.selectedArea?.let { "${it.areaCodigo} - ${it.areaDesc}" },
                        onSelectClick = { viewModel.showAreaDialog() },
                        modifier = Modifier.weight(1f),
                        isEnabled = uiState.selectedDepartamento != null
                    )
                    
                    SelectionCard(
                        title = "Departamento Interno",
                        selectedValue = uiState.selectedDpto?.let { "${it.dptoCodigo} - ${it.dptoDesc}" },
                        onSelectClick = { viewModel.showDptoDialog() },
                        modifier = Modifier.weight(1f),
                        isEnabled = uiState.selectedArea != null
                    )
                }
            }

            // Tercera fila: Sección y Familia
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SelectionCard(
                        title = "Sección",
                        selectedValue = uiState.selectedSeccion?.let { "${it.seccCodigo} - ${it.seccDesc}" },
                        onSelectClick = { viewModel.showSeccionDialog() },
                        modifier = Modifier.weight(1f),
                        isEnabled = uiState.selectedDpto != null
                    )
                    
                    SelectionCard(
                        title = "Familia",
                        selectedValue = when {
                            uiState.isFamiliaTodos -> "Todas las familias"
                            uiState.selectedFamilia != null -> "${uiState.selectedFamilia!!.fliaCodigo} - ${uiState.selectedFamilia!!.fliaDesc}"
                            else -> null
                        },
                        onSelectClick = { viewModel.showFamiliaDialog() },
                        modifier = Modifier.weight(1f),
                        isEnabled = uiState.selectedSeccion != null
                    )
                }
            }

            // Cuarta fila: Grupo y Subgrupo (solo si no está seleccionado "Todas las familias")
            if (!uiState.isFamiliaTodos) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MultiSelectionCard(
                            title = "Grupos",
                            selectedCount = uiState.selectedGrupos.size,
                            totalCount = uiState.grupos.size,
                            onSelectClick = { viewModel.showGrupoDialog() },
                            modifier = Modifier.weight(1f),
                            isEnabled = uiState.selectedFamilia != null,
                            allSelectedText = "Todos los grupos"
                        )
                        
                        // Solo mostrar el campo de subgrupos si NO se seleccionaron todos los grupos
                        if (!uiState.isGruposTodos) {
                            MultiSelectionCard(
                                title = "Subgrupos",
                                selectedCount = uiState.selectedSubgrupos.size,
                                totalCount = uiState.subgrupos.size,
                                onSelectClick = { viewModel.showSubgrupoDialog() },
                                modifier = Modifier.weight(1f),
                                isEnabled = uiState.selectedGrupos.isNotEmpty(),
                                allSelectedText = "Todos los subgrupos"
                            )
                        }
                    }
                }
            }


            // Mostrar progreso si está cargando
            if (uiState.isLoading) {
                item {
                    Card {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Cargando...",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth()
                            )
                            if (uiState.loadingMessage != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = uiState.loadingMessage!!,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            if (uiState.totalProgress > 0) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${uiState.currentProgress}/${uiState.totalProgress}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            // Botón Ver Artículos (solo si hay artículos encontrados)
            if (uiState.articulosLotes.isNotEmpty()) {
                item {
                    Button(
                        onClick = { viewModel.showArticulosLotesDialog() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ver Artículos")
                    }
                }
            }

            // Grilla de artículos seleccionados
            if (uiState.selectedArticulosLotes.isNotEmpty()) {
                item {
                    Text(
                        text = "Artículos Seleccionados (${uiState.selectedArticulosLotes.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                item {
                    val horizontalScrollState = rememberScrollState()
                    
                    Column {

                        // Botón de crear toma - ubicado inmediatamente después de la tabla
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.showConfirmarTomaDialog() },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !uiState.isLoading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Crear Toma")
                        }
                        // Cabecera fija de la grilla
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .horizontalScroll(horizontalScrollState)
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Código",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(80.dp)
                                )
                                Text(
                                    text = "Descripción",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(150.dp)
                                )
                                Text(
                                    text = "Stock",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(60.dp)
                                )
                                Text(
                                    text = "Lote",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(80.dp)
                                )
                                Text(
                                    text = "Familia",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(100.dp)
                                )
                                Text(
                                    text = "Grupo",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(100.dp)
                                )
                                Text(
                                    text = "Subgrupo",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(100.dp)
                                )
                                Text(
                                    text = "Visible",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(50.dp)
                                )
                            }
                        }
                        
                        // Contenedor con scroll vertical para las filas
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp), // Altura fija para el scroll vertical
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            itemsIndexed(uiState.selectedArticulosLotes) { index, articulo ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .horizontalScroll(horizontalScrollState)
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = articulo.artCodigo,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.width(80.dp)
                                        )
                                        Text(
                                            text = articulo.artDesc,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.width(150.dp),
                                            maxLines = 2
                                        )
                                        Text(
                                            text = articulo.cantidad.toInt().toString(),
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (articulo.cantidad > 0) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error,
                                            modifier = Modifier.width(60.dp)
                                        )
                                        Text(
                                            text = articulo.ardeLote,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (articulo.ardeLote == "N/A") MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.width(80.dp),
                                            maxLines = 1
                                        )
                                        Text(
                                            text = articulo.fliaDesc,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.width(100.dp),
                                            maxLines = 1
                                        )
                                        Text(
                                            text = articulo.grupDesc,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.width(100.dp),
                                            maxLines = 1
                                        )
                                        Text(
                                            text = articulo.sugrDesc,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.width(100.dp),
                                            maxLines = 1
                                        )
                                        Text(
                                            text = if (articulo.inventarioVisible == "Y") "✓" else "✗",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (articulo.inventarioVisible == "Y") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                            modifier = Modifier.width(50.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }

    // Diálogos
    if (uiState.showSucursalDialog) {
        GenericSelectionDialog(
            title = "Seleccionar Sucursal",
            items = uiState.sucursales,
            selectedItem = uiState.selectedSucursal,
            onItemSelected = { sucursal -> viewModel.onSucursalSelected(sucursal) },
            onDismiss = { viewModel.hideSucursalDialog() },
            itemToDisplayText = { sucursal -> sucursal.sucDesc },
            itemToSubtitle = { sucursal -> "Código: ${sucursal.sucCodigo}" },
            icon = {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Sucursal",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )
    }

    if (uiState.showDepartamentoDialog) {
        GenericSelectionDialog(
            title = "Seleccionar Depósito",
            items = uiState.departamentos,
            selectedItem = uiState.selectedDepartamento,
            onItemSelected = { departamento -> viewModel.onDepartamentoSelected(departamento) },
            onDismiss = { viewModel.hideDepartamentoDialog() },
            itemToDisplayText = { departamento -> departamento.depDesc },
            itemToSubtitle = { departamento -> "Código: ${departamento.depCodigo}" },
            icon = {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Depósito",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )
    }

    if (uiState.showAreaDialog) {
        GenericSelectionDialog(
            title = "Seleccionar Área",
            items = uiState.areas,
            selectedItem = uiState.selectedArea,
            onItemSelected = { area -> viewModel.onAreaSelected(area) },
            onDismiss = { viewModel.hideAreaDialog() },
            itemToDisplayText = { area -> area.areaDesc },
            itemToSubtitle = { area -> "Código: ${area.areaCodigo}" },
            icon = {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Área",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )
    }

    if (uiState.showDptoDialog) {
        GenericSelectionDialog(
            title = "Seleccionar Departamento Interno",
            items = uiState.dptos,
            selectedItem = uiState.selectedDpto,
            onItemSelected = { dpto -> viewModel.onDptoSelected(dpto) },
            onDismiss = { viewModel.hideDptoDialog() },
            itemToDisplayText = { dpto -> dpto.dptoDesc },
            itemToSubtitle = { dpto -> "Código: ${dpto.dptoCodigo}" },
            icon = {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Departamento",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )
    }

    if (uiState.showSeccionDialog) {
        GenericSelectionDialog(
            title = "Seleccionar Sección",
            items = uiState.secciones,
            selectedItem = uiState.selectedSeccion,
            onItemSelected = { seccion -> viewModel.onSeccionSelected(seccion) },
            onDismiss = { viewModel.hideSeccionDialog() },
            itemToDisplayText = { seccion -> seccion.seccDesc },
            itemToSubtitle = { seccion -> "Código: ${seccion.seccCodigo}" },
            icon = {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Sección",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )
    }

    if (uiState.showFamiliaDialog) {
        SingleSelectWithAllDialog(
            title = "Seleccionar Familia",
            items = uiState.familias,
            selectedItem = uiState.selectedFamilia,
            isAllSelected = uiState.isFamiliaTodos,
            onItemSelected = { familia -> viewModel.onFamiliaSelected(familia) },
            onAllSelected = { viewModel.onFamiliaTodosSelected() },
            onConfirm = { viewModel.hideFamiliaDialog() },
            onDismiss = { viewModel.hideFamiliaDialog() },
            allOptionText = "Todas las Familias",
            itemToSelectableItem = { familia ->
                SelectableItem(
                    id = familia.fliaCodigo.toString(),
                    title = familia.fliaDesc,
                    subtitle = "Código: ${familia.fliaCodigo}"
                )
            }
        )
    }

    // Diálogos de multiselección usando el nuevo componente (solo si no está seleccionado "Todas las familias")
    if (uiState.showGrupoDialog && !uiState.isFamiliaTodos) {
        MultiSelectDialog(
            title = "Seleccionar Grupos",
            items = uiState.grupos,
            selectedItems = uiState.selectedGrupos,
            onItemToggle = { grupo -> viewModel.onGrupoToggled(grupo) },
            onSelectAll = { viewModel.onGruposTodosToggled() },
            onDeselectAll = { viewModel.onGruposTodosToggled() },
            onConfirm = { viewModel.confirmGrupoSelection() },
            onDismiss = { viewModel.hideGrupoDialog() },
            itemToSelectableItem = { grupo ->
                SelectableItem(
                    id = grupo.grupCodigo.toString(),
                    title = grupo.grupDesc,
                    subtitle = "Código: ${grupo.grupCodigo}"
                )
            }
        )
    }

    if (uiState.showSubgrupoDialog && !uiState.isFamiliaTodos && !uiState.isGruposTodos) {
        MultiSelectDialog(
            title = "Seleccionar Subgrupos",
            items = uiState.subgrupos,
            selectedItems = uiState.selectedSubgrupos,
            onItemToggle = { subgrupo -> viewModel.onSubgrupoToggled(subgrupo) },
            onSelectAll = { viewModel.onSubgruposTodosToggled() },
            onDeselectAll = { viewModel.onSubgruposTodosToggled() },
            onConfirm = { viewModel.confirmSubgrupoSelection() },
            onDismiss = { viewModel.hideSubgrupoDialog() },
            itemToSelectableItem = { subgrupo ->
                SelectableItem(
                    id = subgrupo.sugrCodigo.toString(),
                    title = subgrupo.sugrDesc,
                    subtitle = "Código: ${subgrupo.sugrCodigo} - Grupo: ${viewModel.getGrupoDescripcionForSubgrupo(subgrupo)}"
                )
            }
        )
    }

    if (uiState.showArticulosLotesDialog) {
        ArticulosEncontradosDialog(
            articulosLotes = uiState.articulosLotes,
            selectedArticulosLotes = uiState.selectedArticulosLotes,
            onArticuloToggle = { articulo -> viewModel.toggleArticuloLote(articulo) },
            onSelectAll = { articulos -> viewModel.selectSpecificArticulosLotes(articulos) },
            onDeselectAll = { viewModel.clearArticulosLotesSelection() },
            onUpdateInventarioVisible = { inventarioVisible -> viewModel.updateInventarioVisibleMark(inventarioVisible) },
            onDismiss = { viewModel.hideArticulosLotesDialog() },
            inventarioVisible = uiState.inventarioVisible,
            autoSelectAll = tipoToma?.id == "criterio_seleccion", // Selección automática para toma por criterio
            disableDeselection = tipoToma?.id == "criterio_seleccion" // Deshabilitar deselección para toma por criterio
         )
    }

    if (uiState.showConfirmarTomaDialog) {
        AlertDialog(
            onDismissRequest = { 
                if (!uiState.isLoading && uiState.successMessage == null) {
                    viewModel.hideConfirmarTomaDialog()
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Título
                    Text(
                        text = when {
                            uiState.isLoading -> "Creando Toma..."
                            uiState.successMessage != null -> "¡Éxito!"
                            else -> "Confirmar Toma"
                        },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    // Contenido según el estado
                    when {
                        uiState.isLoading -> {
                            // Mostrar loading con porcentaje
                            CircularProgressIndicator(
                                progress = uiState.loadingProgress / 100f,
                                modifier = Modifier.size(80.dp),
                                strokeWidth = 8.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            
                            Text(
                                text = "Procesando... ${uiState.loadingProgress.toInt()}%",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            
                            Text(
                                text = "Creando toma con ${uiState.selectedArticulosLotes.size} artículos",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        uiState.successMessage != null -> {
                            // Mostrar mensaje de éxito
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            
                            Text(
                                text = "Registro realizado correctamente",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            
                            Text(
                                text = uiState.successMessage!!,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                        }
                        else -> {
                            // Mostrar mensaje de confirmación
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            
                            Text(
                                text = "¿Está seguro de que desea crear la toma del inventario con ${uiState.selectedArticulosLotes.size} artículos seleccionados?",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            },
            confirmButton = {
                when {
                    uiState.isLoading -> {
                        // No mostrar botón durante loading
                    }
                    uiState.successMessage != null -> {
                        TextButton(onClick = { 
                            viewModel.hideConfirmarTomaDialog()
                            viewModel.clearSuccessMessage()
                            viewModel.clearAllParameters() // ✅ Limpiar todos los parámetros
                            onNavigateToHome()
                        }) {
                            Text("Aceptar")
                        }
                    }
                    else -> {
                        TextButton(onClick = { 
                            viewModel.crearTomaInventario()
                        }) {
                            Text("Confirmar")
                        }
                    }
                }
            },
            dismissButton = {
                if (!uiState.isLoading && uiState.successMessage == null) {
                    TextButton(onClick = { viewModel.hideConfirmarTomaDialog() }) {
                        Text("Cancelar")
                    }
                }
            }
        )
    }

    // Diálogo de confirmación de salida
    ExitConfirmationDialog(
        showDialog = showExitDialog,
        onDismiss = { showExitDialog = false },
        onConfirm = { 
            viewModel.clearAllParameters() // ✅ Limpiar todos los parámetros al salir
            onNavigateToHome() 
        },
        title = "Salir de la toma",
        message = "¿Estás seguro de que deseas volver al menu principal?",
        warningMessage = "Los cambios no guardados se perderán.",
        confirmButtonText = "Sí, salir",
        dismissButtonText = "Cancelar"
    )
}