package com.mapex.domain.model

data class Country(
    val id: String,
    val commonName: String,
    val officialName: String,
    val region: String,
    val subregion: String,
    val capital: List<String>,
    val population: Int,
    val area: Double?,
    val timezones: List<String>,
    val languages: List<String>,
    val currencies: List<String>,
    val flags: String?,
    val codeAlpha2: String,
    val codeAlpha3: String,
    // Extended REST Countries fields
    val coatOfArms: String? = null,
    val borders: List<String> = emptyList(),
    val tld: List<String> = emptyList(),
    val callingCode: String? = null,
    val googleMapsUrl: String? = null,
    val latlng: List<Double> = emptyList(),
    val landlocked: Boolean = false,
    val gini: Double? = null,
    val carSide: String? = null,
    val startOfWeek: String? = null,
    val continents: List<String> = emptyList()
)
