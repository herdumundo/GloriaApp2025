package com.gloria.ui.screens.inventario

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import com.gloria.ui.theme.AppIcons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import com.gloria.viewmodel.TomaManualViewModel
import com.gloria.ui.components.SearchableDialog
import com.gloria.ui.components.SearchableMultiSelectDialog
import com.gloria.ui.components.CompactSelectionCard
import com.gloria.ui.components.ArticulosLotesDialog
import com.gloria.ui.components.ArticulosSeleccionadosGrid
import com.gloria.data.AppDatabase
import com.gloria.data.repository.ArticuloLoteRepository
import com.gloria.repository.SincronizacionCompletaRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TomaManualScreen(
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    
    val viewModel = remember {
        TomaManualViewModel(
            sucursalDepartamentoDao = database.sucursalDepartamentoDao(),
            areaDao = database.areaDao(),
            departamentoDao = database.departamentoDao(),
            seccionDao = database.seccionDao(),
            familiaDao = database.familiaDao(),
            grupoDao = database.grupoDao(),
            subgrupoDao = database.subgrupoDao(),
            articuloLoteRepository = ArticuloLoteRepository()
        )
    }
    
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        
        // Grid de tarjetas en 2 columnas
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            // Fila 1: Sucursal + Depósito
            item {
                CompactSelectionCard(
                    label = "Sucursal",
                    selectedText = uiState.selectedSucursal?.let { "${it.sucCodigo} - ${it.sucDesc}" },
                    onClick = { viewModel.showSucursalDialog() },
                    isCompleted = uiState.selectedSucursal != null
                )
            }
            
            item {
                CompactSelectionCard(
                    label = "Depósito",
                    selectedText = uiState.selectedDepartamento?.let { "${it.depCodigo} - ${it.depDesc}" },
                    onClick = { viewModel.showDepartamentoDialog() },
                    enabled = uiState.selectedSucursal != null,
                    isCompleted = uiState.selectedDepartamento != null
                )
            }
            
            // Fila 2: Área + Departamento
            item {
                CompactSelectionCard(
                    label = "Área",
                    selectedText = uiState.selectedArea?.let { "${it.areaCodigo} - ${it.areaDesc}" },
                    onClick = { viewModel.showAreaDialog() },
                    enabled = uiState.selectedDepartamento != null,
                    isCompleted = uiState.selectedArea != null
                )
            }
            
            item {
                CompactSelectionCard(
                    label = "Departamento",
                    selectedText = uiState.selectedDpto?.let { "${it.dptoCodigo} - ${it.dptoDesc}" },
                    onClick = { viewModel.showDptoDialog() },
                    enabled = uiState.selectedArea != null,
                    isCompleted = uiState.selectedDpto != null
                )
            }
            
            // Fila 3: Sección + Familia
            item {
                CompactSelectionCard(
                    label = "Sección",
                    selectedText = uiState.selectedSeccion?.let { "${it.seccCodigo} - ${it.seccDesc}" },
                    onClick = { viewModel.showSeccionDialog() },
                    enabled = uiState.selectedDpto != null,
                    isCompleted = uiState.selectedSeccion != null
                )
            }
            
            item {
                CompactSelectionCard(
                    label = "Familia",
                    selectedText = if (uiState.isFamiliaTodos) {
                        "Todas las familias"
                    } else {
                        uiState.selectedFamilia?.let { "${it.fliaCodigo} - ${it.fliaDesc}" }
                    },
                    onClick = { viewModel.showFamiliaDialog() },
                    enabled = uiState.selectedSeccion != null,
                    isCompleted = uiState.selectedFamilia != null || uiState.isFamiliaTodos
                )
            }
            
            // Fila 4: Grupo + Subgrupos (solo visibles si NO se seleccionó "Todos" en familia)
            if (!uiState.isFamiliaTodos) {
                item {
                    CompactSelectionCard(
                        label = "Grupo",
                        selectedText = when {
                            uiState.isGruposTodos -> "Todos los grupos"
                            uiState.selectedGrupos.isNotEmpty() -> "${uiState.selectedGrupos.size} grupos seleccionados"
                            else -> null
                        },
                        onClick = { viewModel.showGrupoDialog() },
                        enabled = uiState.selectedFamilia != null,
                        isCompleted = uiState.selectedGrupos.isNotEmpty() || uiState.isGruposTodos
                    )
                }
                
                item {
                    CompactSelectionCard(
                        label = "Subgrupos",
                        selectedText = when {
                            uiState.isSubgruposTodos -> "Todos los subgrupos (${uiState.subgrupos.size})"
                            uiState.selectedSubgrupos.isNotEmpty() -> "${uiState.selectedSubgrupos.size} subgrupo(s) seleccionado(s)"
                            else -> null
                        },
                        onClick = { viewModel.showSubgrupoDialog() },
                        enabled = uiState.selectedGrupos.isNotEmpty() || uiState.isGruposTodos,
                        isCompleted = uiState.selectedSubgrupos.isNotEmpty() || uiState.isSubgruposTodos
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Botón para cargar artículos con lotes
        if (uiState.selectedSubgrupos.isNotEmpty() || uiState.isSubgruposTodos || uiState.isFamiliaTodos) {
            Button(
                onClick = { viewModel.loadArticulosLotes() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                                    Text(
                        text = if (uiState.isLoading) {
                            uiState.loadingMessage ?: "Buscando productos..."
                        } else {
                            "Buscar productos en base a filtros."
                        },
                        fontSize = 16.sp,
                        color = Color.White
                    )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Mensaje de error
        if (uiState.errorMessage != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Error",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = uiState.errorMessage ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    TextButton(
                        onClick = { viewModel.clearError() }
                    ) {
                        Text("Cerrar")
                    }
                }
            }
        }
        
        // Grilla de productos seleccionados
        if (uiState.selectedArticulosLotes.isNotEmpty()) {
            ArticulosSeleccionadosGrid(
                articulos = uiState.selectedArticulosLotes,
                onRemoveArticulo = { articulo ->
                    viewModel.toggleArticuloLote(articulo)
                }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        // Botón de crear toma
        Button(
            onClick = { /* TODO: Implementar lógica de creación */ },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("Crear Toma", fontSize = 16.sp)
        }
    }
    
    // Diálogo de artículos con lotes
    if (uiState.showArticulosLotesDialog) {
        ArticulosLotesDialog(
            articulosLotes = uiState.articulosLotes,
            selectedArticulosLotes = uiState.selectedArticulosLotes,
            onDismiss = { viewModel.hideArticulosLotesDialog() },
            onConfirm = { selectedArticulos ->
                // TODO: Implementar lógica para los artículos seleccionados
                viewModel.hideArticulosLotesDialog()
            },
            onToggleArticulo = { viewModel.toggleArticuloLote(it) },
            onSelectAll = { viewModel.selectAllArticulosLotes() },
            onClearSelection = { viewModel.clearArticulosLotesSelection() },
            onSelectSpecific = { articulos -> viewModel.selectSpecificArticulosLotes(articulos) }
        )
    }
    
    // Diálogos
    SearchableDialog(
        title = "Seleccionar Sucursal",
        items = uiState.sucursales,
        selectedItem = uiState.selectedSucursal,
        onItemSelected = { viewModel.onSucursalSelected(it) },
        onDismiss = { viewModel.hideSucursalDialog() },
        getItemText = { "${it.sucCodigo} - ${it.sucDesc}" },
        showDialog = uiState.showSucursalDialog
    )
    
    SearchableDialog(
        title = "Seleccionar Depósito",
        items = uiState.departamentos,
        selectedItem = uiState.selectedDepartamento,
        onItemSelected = { viewModel.onDepartamentoSelected(it) },
        onDismiss = { viewModel.hideDepartamentoDialog() },
        getItemText = { "${it.depCodigo} - ${it.depDesc}" },
        showDialog = uiState.showDepartamentoDialog
    )
    
    SearchableDialog(
        title = "Seleccionar Área",
        items = uiState.areas,
        selectedItem = uiState.selectedArea,
        onItemSelected = { viewModel.onAreaSelected(it) },
        onDismiss = { viewModel.hideAreaDialog() },
        getItemText = { "${it.areaCodigo} - ${it.areaDesc}" },
        showDialog = uiState.showAreaDialog
    )
    
    SearchableDialog(
        title = "Seleccionar Departamento",
        items = uiState.dptos,
        selectedItem = uiState.selectedDpto,
        onItemSelected = { viewModel.onDptoSelected(it) },
        onDismiss = { viewModel.hideDptoDialog() },
        getItemText = { "${it.dptoCodigo} - ${it.dptoDesc}" },
        showDialog = uiState.showDptoDialog
    )
    
    SearchableDialog(
        title = "Seleccionar Sección",
        items = uiState.secciones,
        selectedItem = uiState.selectedSeccion,
        onItemSelected = { viewModel.onSeccionSelected(it) },
        onDismiss = { viewModel.hideSeccionDialog() },
        getItemText = { "${it.seccCodigo} - ${it.seccDesc}" },
        showDialog = uiState.showSeccionDialog
    )
    
    SearchableDialog(
        title = "Seleccionar Familia",
        items = uiState.familias,
        selectedItem = uiState.selectedFamilia,
        onItemSelected = { viewModel.onFamiliaSelected(it) },
        onDismiss = { viewModel.hideFamiliaDialog() },
        getItemText = { "${it.fliaCodigo} - ${it.fliaDesc}" },
        showDialog = uiState.showFamiliaDialog,
        showTodosOption = true,
        onTodosSelected = { viewModel.onFamiliaTodosSelected() }
    )
    
    SearchableMultiSelectDialog(
        title = "Seleccionar Grupos",
        items = uiState.grupos,
        selectedItems = uiState.selectedGrupos,
        onItemToggled = { viewModel.onGrupoToggled(it) },
        onDismiss = { viewModel.hideGrupoDialog() },
        onConfirm = { viewModel.confirmGrupoSelection() },
        getItemText = { "${it.grupCodigo} - ${it.grupDesc}" },
        showDialog = uiState.showGrupoDialog,
        showTodosOption = true,
        isTodosSelected = uiState.isGruposTodos,
        onTodosToggled = { viewModel.onGruposTodosToggled() }
    )
    
    SearchableMultiSelectDialog(
        title = "Seleccionar Subgrupos",
        items = uiState.subgrupos,
        selectedItems = uiState.selectedSubgrupos,
        onItemToggled = { viewModel.onSubgrupoToggled(it) },
        onDismiss = { viewModel.hideSubgrupoDialog() },
        onConfirm = { viewModel.confirmSubgrupoSelection() },
        getItemText = { subgrupo -> 
            val grupoDesc = uiState.grupos.find { it.grupCodigo == subgrupo.sugrGrupo }?.grupDesc ?: "Grupo ${subgrupo.sugrGrupo}"
            "${subgrupo.sugrCodigo} - ${subgrupo.sugrDesc} (${grupoDesc})"
        },
        showDialog = uiState.showSubgrupoDialog,
        showTodosOption = true,
        isTodosSelected = uiState.isSubgruposTodos,
        onTodosToggled = { viewModel.onSubgruposTodosToggled() }
    )
}



@Composable
private fun SelectionButton(
    label: String,
    selectedText: String?,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selectedText != null) 
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) 
            else 
                MaterialTheme.colorScheme.surface,
            contentColor = if (selectedText != null) 
                MaterialTheme.colorScheme.primary 
            else 
                MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (selectedText != null) 
                MaterialTheme.colorScheme.primary 
            else 
                MaterialTheme.colorScheme.outline
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (selectedText != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = selectedText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                } else {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Seleccione...",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
            
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Seleccionar",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
