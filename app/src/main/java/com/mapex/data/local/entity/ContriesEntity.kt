package com.mapex.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contries")
data class ContriesEntity(
    @PrimaryKey val id_contries: String,
    val nombre_comun: String,
    val region: String,
    val poblacion: Int,
    val url_banera_png: String,
    val continentes: String // Guardaremos los continentes como texto separado por comas
)
