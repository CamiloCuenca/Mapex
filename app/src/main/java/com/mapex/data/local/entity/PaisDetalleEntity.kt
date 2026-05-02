package com.mapex.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "pais_detalle",
    foreignKeys = [
        ForeignKey(
            entity = ContriesEntity::class,
            parentColumns = ["id_contries"],
            childColumns = ["id_contries"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PaisDetalleEntity(
    @PrimaryKey val id_contries: String, // Ahora esta es la PK, 1-a-1 con el país
    val nombre_oficial: String,
    val subregion: String,
    val superficie_km2: Double,
    val capital: String,
    val moneda_texto: String,
    val idioma_texto: String,
    val enlace_map: String,
    val escudo_url: String? = null,
    val fronteras: String = "",
    val zonas_horarias: String = "",
    val latitud_longitud: String = "",
    val codigo_llamada: String? = null,
    val tld: String = "",
    val landlocked: Boolean = false,
    val car_side: String? = null
)
