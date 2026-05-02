package com.mapex.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pais_paginacion")
data class PaisPaginacionEntity(
    @PrimaryKey val id_contries: String,
    val prev_key: Int?,
    val next_key: Int?
)
