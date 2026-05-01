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
    val codeAlpha3: String
)
