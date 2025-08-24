package com.gloria.domain.model

data class TipoToma(
    val id: String,
    val titulo: String,
    val descripcion: String
)

object TiposToma {
    val tipos = listOf(
        TipoToma(
            id = "criterio_seleccion",
            titulo = "Por criterio de selección",
            descripcion = "Generar toma basada en criterios específicos"
        ),
        TipoToma(
            id = "manual",
            titulo = "Manual",
            descripcion = "Crear toma manualmente"
        )
    )
}
